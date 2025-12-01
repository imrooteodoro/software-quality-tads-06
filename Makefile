VENV=.venv
PY=$(VENV)/bin/python
PIP=$(PY) -m pip

.PHONY: all build test run venv install-deps metrics plot metrics-plot clean dist-clean help

all: build

build:
	mvn clean package

test:
	mvn test

run:
	@if ls target/*.jar 1> /dev/null 2>&1; then \
		java -jar target/*.jar; \
	else \
		echo "Jar not found. Run 'make build' first."; \
	fi

venv:
	@if [ ! -d $(VENV) ]; then \
		python -m venv $(VENV); \
		$(PIP) install --upgrade pip; \
	else \
		echo "venv already exists"; \
	fi

install-deps: venv
	$(PIP) install -r requirements.txt

metrics: install-deps
	$(PY) -m lizard -l java -o METRICS.json src/main/java

plot: install-deps
	$(PY) tools/plot_lizard.py

metrics-plot: metrics plot

clean:
	mvn clean || true
	rm -f METRICS.json
	rm -f tools/METRICS_parsed.json
	rm -f tools/complexity_top10.png

dist-clean: clean
	rm -rf $(VENV)

help:
	@echo "Makefile targets:"
	@echo "  make build          - mvn clean package"
	@echo "  make test           - run mvn test"
	@echo "  make run            - run the generated jar (target/*.jar)"
	@echo "  make venv           - create Python venv at .venv"
	@echo "  make install-deps   - install lizard and matplotlib in venv"
	@echo "  make metrics        - run lizard and generate METRICS.json"
	@echo "  make plot           - run plot script to create tools/complexity_top10.png"
	@echo "  make metrics-plot   - metrics && plot"
	@echo "  make clean          - remove build artifacts and metrics files"
	@echo "  make dist-clean     - clean + remove .venv"
