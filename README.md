# FirmHealth Analyzer

## Description
FirmHealth Analyzer is a standalone Python tool that evaluates a company’s financial statements using publicly available data (primarily SEC filings). Given a ticker, it extracts key balance sheet and income statement metrics, computes core financial ratios, and produces a structured financial health report. The system is designed with a **financial analysis-first approach**, where outputs such as health scores, distress indicators, and credit recommendations are built on top of reliable, explainable fundamentals.

---

# Development Plan (Flexible / Subject to Change)

> ⚠️ This plan is intentionally lightweight and modular.  
> It will be refined after gathering detailed requirements from the primary use case (dad’s company).

---

## Current Priority Context
- Primary focus right now: **Finish Enterprise SAP AI Knowledge System**
- FirmHealth Analyzer is a **secondary parallel project**
- Goal: build a **clean v1 foundation**, not a fully complete system

---

## Phase 0 — Setup (Quick Start)

### Goal
Get a working skeleton repo ready with minimal friction.

### Tasks
- Create repo: `firmhealth-analyzer`
- Set up basic structure
- Add dependencies:
  - `requests`
  - `pandas`
- Create entry script:
  ```bash
  python analyze.py AAPL
