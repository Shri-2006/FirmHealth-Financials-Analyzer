# FirmHealth Financials Analyzer — Project Plan (Draft 1)
 
> **Author:** Shriyans  
> **Last Updated:** June 2026  
> **Status:** Planning Phase
 
---
 
## What Is This?
 
FirmHealth is a multi-language financial analysis tool that ingests public company financial statements, extracts key balance sheet and income statement metrics, and produces a structured financial health report. On top of that analysis layer it adds health scoring, distress flags, and credit recommendations.
 
The core principle: automate what a junior financial analyst does manually — reading SEC filings, computing ratios, flagging risk — and expose it through a clean API and dashboard.
 
---
 
## Why Multi-Language?
 
Each language is chosen because it is the natural fit for its role, not just to demonstrate breadth. This mirrors how real enterprise financial software is actually built.
 
| Language | Role | Real-World Parallel |
|---|---|---|
| **Java** | Main backend, business logic, scoring engine | Bloomberg Terminal, SAP core systems, banking infrastructure |
| **Python** | ML service, data ingestion, financial data APIs | Quant desks, fintech data pipelines, every data science team |
| **C** | Math engine — ratio calculations | High-frequency trading, performance-critical financial math |
| **R** | Statistical analysis, trend modeling, report generation | Academic finance, risk modeling, actuarial science |
| **SAP Build Apps** | Frontend dashboard | Enterprise low-code, Fortune 500 standard tooling |
 
---
 
## Architecture Overview
 
```
┌─────────────────────────────────────┐
│       SAP Build Apps (Frontend)     │
│    Consumes REST API via HTTP/JSON  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     Java Spring Boot (Core API)     │  ← Main backend
│  - REST endpoints                   │
│  - Business logic & orchestration   │
│  - Health scoring engine (OOP)      │
│  - Distress flags & credit rules    │
│  - Calls C library via JNI          │
│  - Calls Python ML service via HTTP │
│  - Calls R analytics via HTTP       │
└──────┬──────────────┬───────────────┘
       │              │
       ▼              ▼
┌────────────┐  ┌─────────────────────┐
│ C Library  │  │  Python ML Service  │
│ (ratios)   │  │  Flask microservice │
│ via JNI or │  │  SEC data ingestion │
│ subprocess │  │  ML distress model  │
└────────────┘  └──────────┬──────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │   R Analytics   │
                  │  Plumber API    │
                  │  Trend analysis │
                  │  Report output  │
                  └─────────────────┘
```
 
---
 
## Data Flow
 
```
Public Financial Data (SEC EDGAR API or CSV upload)
        ↓
   Python Service — fetches and parses raw filings
        ↓
   Python Extractor — pulls structured metrics
        ↓
   ┌────┴──────────────────────┐
   ↓                           ↓
  C Math Engine             Java Scorer
  (liquidity, leverage,     (weighted health score,
   profitability ratios)     distress flags,
                             credit recommendation)
   └────────┬────────────────┘
            ↓
       R Analytics
       (multi-period trends, statistical report)
            ↓
       JSON response
            ↓
   SAP Build Apps dashboard renders results
```
 
---
 
## Key Financial Metrics
 
### From the Balance Sheet
- Current Assets / Current Liabilities → **Current Ratio**
- Total Debt / Total Equity → **Debt-to-Equity Ratio**
- Cash and equivalents
### From the Income Statement
- Revenue, Net Income → **Profit Margin**
- EBIT / Interest Expense → **Interest Coverage Ratio**
- Year-over-year revenue growth
### Composite / Derived
- **Altman Z-Score** — predicts likelihood of financial distress
- **Weighted Health Score** — 0 to 100, proprietary composite
---
 
## Project Structure
 
```
FirmHealth/
│
├── backend/                      ← Java Spring Boot
│   ├── src/main/java/firmhealth/
│   │   ├── api/                  ← REST controllers
│   │   ├── scoring/              ← HealthScorer.java, DistressFlags.java
│   │   ├── credit/               ← CreditAdvisor.java
│   │   └── bridge/               ← calls to C, Python, R
│   └── Dockerfile
│
├── math_engine/                  ← C
│   ├── ratios.c
│   ├── ratios.h
│   └── Makefile
│
├── ml_service/                   ← Python
│   ├── app.py                    ← Flask entry point
│   ├── ingestor.py               ← SEC EDGAR API calls
│   ├── extractor.py              ← metric extraction
│   ├── model.py                  ← ML distress model
│   └── Dockerfile
│
├── analytics/                    ← R
│   ├── plumber.R                 ← API entry point
│   ├── trends.R                  ← trend analysis
│   ├── report_gen.R              ← report generation
│   └── Dockerfile
│
├── docker-compose.yml            ← spins up all services
├── kubernetes/                   ← Phase 6, production deployment
│   ├── java-deployment.yaml
│   ├── python-deployment.yaml
│   └── r-deployment.yaml
│
└── draft1.md                     ← this file
```
 
---
 
## REST API Endpoints (Java Backend)
 
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/analyze` | Submit ticker, trigger full analysis pipeline |
| `GET` | `/api/report/{ticker}` | Retrieve generated health report |
| `GET` | `/api/company/{ticker}` | Get raw extracted financial metrics |
| `DELETE` | `/api/report/{ticker}` | Remove a stored report |
 
All responses are JSON. The API is stateless — every request carries all the information needed to fulfill it.
 
---
 
## Hosting Strategy
 
### Local / Self-Hosted
```bash
docker compose up
```
Spins up all four containers: Java backend, Python ML service, R analytics, Nginx reverse proxy.
 
### Production (SAP BTP)
SAP Business Technology Platform supports Docker containers natively. Deploy each service as a container on BTP. SAP Build Apps frontend connects to the exposed API endpoint.
 
### Anyone Else
`docker compose up` works on any machine with Docker installed. No SAP account needed to run the backend stack.
 
---
 
## Phase Plan & Timeline
 
| Phase | Content | Estimated Time |
|---|---|---|
| **Phase 0** | Concepts, folder structure, Git setup, first Dockerfile | 1 week |
| **Phase 1** | Java Spring Boot skeleton, SEC data ingestion via Python | 2 weeks |
| **Phase 2** | C ratio engine, JNI integration with Java | 1.5 weeks |
| **Phase 3** | Python ML service (Flask + distress model) | 1.5 weeks |
| **Phase 4** | R analytics service (Plumber + trend analysis) | 1 week |
| **Phase 5** | SAP Build Apps frontend, API wiring | 1.5 weeks |
| **Phase 6** | Docker Compose polish, Kubernetes config | 1 week |
| **Phase 7** | Testing, documentation, final polish | 1 week |
 
**Total: ~10–11 weeks** at 8–12 hours per week.  
**Minimum viable product** (Phases 0–3): ~5–6 weeks, fully functional core.
 
---
 
## Learning Approach
 
Every module follows this order before any code is written:
 
1. **Concept check** — answer 2–3 questions about the relevant idea in your own words
2. **Algorithm design on paper** — sketch the logic before touching a keyboard
3. **Write the code** — every single line written by Shriyans
4. **Review and connect** — bring it back to the master chat to verify it fits the architecture
---
 
## Chat Structure
 
| Chat | Purpose |
|---|---|
| **Master chat (this one)** | Architecture decisions, cross-module connections, phase reviews |
| **Phase 0 chat** | Folder structure, Git, first Dockerfile |
| **Phase 1 chat** | Java Spring Boot, REST controllers, Python ingestion |
| **Phase 2 chat** | C math engine, JNI bridge |
| **Phase 3 chat** | Python Flask ML service |
| **Phase 4 chat** | R Plumber analytics |
| **Phase 5 chat** | SAP Build Apps frontend |
| **Phase 6 chat** | Docker Compose, Kubernetes |
 
---
 
## Data Sources
 
- **SEC EDGAR API** — free, official US public company filings
- **Yahoo Finance** (`yfinance` Python library) — fallback and supplemental data
- **CSV upload** — manual input option for any financial statement
---
 
## Key Concepts to Know Before Starting
 
Before opening Phase 0 chat, be able to explain in your own words:
 
- [ ] What is a REST API and how does it differ from other communication styles?
- [ ] What is a Docker container and how is it different from a virtual machine?
- [ ] What is the difference between a balance sheet and an income statement?
- [ ] What does the current ratio tell you about a company?
- [ ] What is the Altman Z-Score and what does it predict?
- [ ] Why does OOP (Java) fit a rules-based scoring engine?
- [ ] Why is C faster than Python for math-heavy loops?
---
 
*This document is a living plan. Update it as decisions change across phases.*