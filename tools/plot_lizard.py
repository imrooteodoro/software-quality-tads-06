#!/usr/bin/env python3
"""
Parse the lizard textual/JSON output saved in METRICS.json (lizard's -o may produce textual tables)
and generate a PNG showing the top-10 functions by cyclomatic complexity (CCN).

Saves:
 - tools/complexity_top10.png
 - tools/METRICS_parsed.json (intermediate parsed data)

Run with the project's venv Python, e.g.:
  /path/to/.venv/bin/python tools/plot_lizard.py
"""
import json
import re
from pathlib import Path
import matplotlib.pyplot as plt


def parse_lizard_text(path: Path):
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    entries = []

    # regex for lines that start with numbers columns and end with location
    row_re = re.compile(r"^\s*(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(.+)$")

    for line in lines:
        m = row_re.match(line)
        if not m:
            continue
        nloc = int(m.group(1))
        ccn = int(m.group(2))
        token = int(m.group(3))
        param = int(m.group(4))
        length = int(m.group(5))
        location = m.group(6).strip()

        # location format: Namespace::function@start-end@path
        parts = location.split("@")
        func_full = parts[0] if parts else location
        # try to extract simple function name
        func_name = func_full.split("::")[-1]
        file_path = parts[-1] if len(parts) >= 3 else None

        entry = {
            "nloc": nloc,
            "ccn": ccn,
            "token": token,
            "param": param,
            "length": length,
            "location": location,
            "function": func_name,
            "file": file_path,
        }
        entries.append(entry)

    return entries


def main():
    base = Path(__file__).resolve().parents[1]
    metrics_path = base / "METRICS.json"
    out_png = base / "tools" / "complexity_top10.png"
    parsed_json = base / "tools" / "METRICS_parsed.json"

    if not metrics_path.exists():
        print(f"METRICS file not found: {metrics_path}")
        return 2

    entries = parse_lizard_text(metrics_path)
    if not entries:
        print("No function entries parsed from METRICS.json")
        return 3

    # sort by CCN desc then by nloc desc
    entries.sort(key=lambda e: (e["ccn"], e["nloc"]), reverse=True)

    # write parsed JSON
    parsed_json.write_text(json.dumps(entries, indent=2), encoding="utf-8")

    top = entries[:10]

    labels = [f"{e['function']}\n({Path(e['file']).name if e['file'] else ''})" for e in top]
    ccns = [e["ccn"] for e in top]
    nlocs = [e["nloc"] for e in top]

    # use a commonly available built-in style
    plt.style.use('ggplot')
    fig, ax = plt.subplots(figsize=(10, 6))

    y_pos = range(len(labels))[::-1]
    ax.barh(y_pos, ccns, align='center', color='C1')
    ax.set_yticks(y_pos)
    ax.set_yticklabels(labels)
    ax.set_xlabel('Cyclomatic Complexity (CCN)')
    ax.set_title('Top 10 functions by Cyclomatic Complexity')

    # annotate with CCN and nloc
    for i, (ccn, nloc) in enumerate(zip(ccns, nlocs)):
        ax.text(ccn + 0.1, len(labels) - 1 - i, f"CCN={ccn}  NLOC={nloc}", va='center')

    fig.tight_layout()
    out_png.parent.mkdir(parents=True, exist_ok=True)
    fig.savefig(out_png)
    print(f"Saved plot to {out_png}")
    print(f"Saved parsed metrics to {parsed_json}")
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
