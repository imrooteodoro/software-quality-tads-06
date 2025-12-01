#!/usr/bin/env python3
import os
import re
import json

JAVA_EXT = '.java'

# heuristics for splitting methods: find method signatures and body braces
method_sig_re = re.compile(r"(public|private|protected|static|\s)+[\w<>\[\]]+\s+(\w+)\s*\([^)]*\)\s*\{")

# McCabe tokens
mcc_tokens = [r"\bif\b", r"\bfor\b", r"\bwhile\b", r"\bcase\b", r"\bcatch\b", r"\?", r"&&", r"\|\|"]


def count_mccabe(method_body):
    count = 1
    for tok in mcc_tokens:
        count += len(re.findall(tok, method_body))
    return count


def extract_methods(java_text):
    # naive: find method start indices by regex for signature, then find matching braces
    methods = []
    for m in re.finditer(r"([\w\s<>\[\],]+)\s+(\w+)\s*\(([^)]*)\)\s*\{", java_text):
        start = m.start()
        name = m.group(2)
        # find matching brace from m.end()-1
        idx = m.end() - 1
        depth = 0
        for i in range(idx, len(java_text)):
            if java_text[i] == '{':
                depth += 1
            elif java_text[i] == '}':
                depth -= 1
                if depth == 0:
                    end = i + 1
                    body = java_text[m.end()-1:end]
                    methods.append((name, body, start, end))
                    break
    return methods


def loc(text):
    lines = text.splitlines()
    code_lines = [l for l in lines if l.strip() and not l.strip().startswith("//")]
    return len(code_lines)


def analyze_project(root):
    results = {}
    for dirpath, dirnames, filenames in os.walk(root):
        for fn in filenames:
            if fn.endswith(JAVA_EXT):
                path = os.path.join(dirpath, fn)
                with open(path, 'r', encoding='utf-8') as f:
                    txt = f.read()
                methods = extract_methods(txt)
                class_key = os.path.relpath(path, root)
                results[class_key] = []
                for name, body, s, e in methods:
                    m_loc = loc(body)
                    m_mcc = count_mccabe(body)
                    results[class_key].append({'method': name, 'mccabe': m_mcc, 'loc': m_loc, 'start': s, 'end': e})
    return results


if __name__ == '__main__':
    root = os.path.join(os.path.dirname(__file__), '..')
    root = os.path.abspath(root)
    res = analyze_project(os.path.join(root, 'src'))
    # compute aggregates
    metrics = {'classes': {}, 'summary': {}}
    all_mcc = []
    for cls, methods in res.items():
        if not methods:
            continue
        mccs = [m['mccabe'] for m in methods]
        locs = [m['loc'] for m in methods]
        metrics['classes'][cls] = {
            'methods': methods,
            'max_mccabe': max(mccs),
            'avg_mccabe': sum(mccs)/len(mccs),
            'max_loc': max(locs),
            'avg_loc': sum(locs)/len(locs)
        }
        all_mcc.extend(mccs)
    if all_mcc:
        metrics['summary'] = {
            'max_mccabe': max(all_mcc),
            'avg_mccabe': sum(all_mcc)/len(all_mcc),
            'methods_analyzed': len(all_mcc)
        }
    else:
        metrics['summary'] = {'methods_analyzed': 0}

    out = os.path.join(root, 'METRICS.json')
    with open(out, 'w', encoding='utf-8') as f:
        json.dump(metrics, f, indent=2)
    print('Metrics written to', out)
