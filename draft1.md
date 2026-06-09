# FirmHealth Financials Analyzer — Project Plan (Draft 1)

> **Author:** Shriyans  
> **Last Updated:** June 2026  
> **Status:** In Progress — Phase 3

---

## License

**Current (Phases 0–3):** Repository is private. Apache 2.0 license in place temporarily.

**Phase 4 action:** Swap Apache 2.0 for All Rights Reserved in the `LICENSE` file:
```
Copyright 2026 Shriyans Singh. All Rights Reserved.

This software and its source code are proprietary and confidential.
Unauthorized copying, distribution, or use of this software,
via any medium, is strictly prohibited without explicit written
permission from the copyright holder.
```

**After Phase 4:** Repository can be made public safely — All Rights Reserved means anyone can view the code but cannot copy, distribute, or sell it without explicit written permission.

**Before client handoff:** A separate commercial contract must be signed granting the firm full rights to sell, modify, distribute, and white-label the software. This contract lives outside the repo. Do not hand over the project without it.

**Do not enable template repository** — this would allow others to clone the structure and undermine the proprietary license.

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
| **SAP AI Core** | Natural language summarization of analysis results — **optional feature** | Enterprise AI, explainable outputs for non-technical users |

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
│  - Calls SAP AI Core for summaries (optional, feature-flagged)  │
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

## Credit Workflow Coverage

FirmHealth automates the analytical steps of a standard credit assessment. Based on the workflow in `Workflow`:

| Step | Description | FirmHealth Coverage |
|---|---|---|
| 1. Gather Information | Legal name, financials, references | CSV upload or SEC EDGAR pull |
| 2. Credit Checks | Credit scores, payment history | SEC filings + ML distress model |
| 3. Financial Strength | Liquidity, D/E, profitability | C math engine |
| 4. Business Risk | Industry, market, qualitative factors | Java scoring engine |
| 5. Credit Exposure | Monthly purchases × payment term factor | `CreditAdvisor.java` formula |
| 6. Internal Policy | Evaluator's own thresholds and rules | Evaluator profile input |
| 7. Risk Rating | Low / Medium / High classification | `DistressFlags.java` |
| 8. Obtain Approvals | Human approval chain | **Out of scope — evaluator handles internally** |
| 9. Set Credit Limit | Document limit, terms, review date | Final JSON report output |
| 10. Monitor & Review | Periodic re-analysis | **Future v2 feature** |

### Evaluator Identity
The user running an analysis must provide their own profile, because credit recommendations are calibrated to the evaluator — not just the subject being evaluated.

**Evaluator inputs:**
- Company size / revenue tier
- Industry
- Payment terms they offer (Net 30, Net 60, etc.)
- Internal policy thresholds (maximum limits by customer size, approval requirements)

This feeds directly into `CreditAdvisor.java`, which takes two inputs — the subject's financial profile and the evaluator's policy profile — and produces a recommendation calibrated to both.

### Supported Evaluation Scenarios
- **Public firm evaluating private firm** — evaluator enters ticker, subject uploads CSV
- **Private firm evaluating public firm** — evaluator uploads CSV, subject looked up via SEC EDGAR
- **Two private firms** — both sides upload CSVs
- **Public firm evaluating public firm** — both sides looked up via SEC EDGAR

---

## Fallback Strategy

Each external service has a fallback to prevent the entire pipeline from failing if one component goes down. This is a standard enterprise pattern called a **circuit breaker**.

| Primary Service | Failure Scenario | Fallback |
|---|---|---|
| **C math engine** | JNI crash or compilation error | Java reimplements the same ratio formulas natively in `AnalysisService` |
| **R analytics** | Plumber service down or unresponsive | Python (pandas, numpy, scipy) handles trend analysis in the ML service |
| **Python ML service** | Flask service down | Java returns a partial response with available data, flags ML scoring as unavailable |

### Fallback Behavior
- Fallbacks are silent to the end user where possible — the response still returns, with a flag indicating which layer was used
- All fallback events are logged for monitoring
- No fallback should crash the Java backend — every external call is wrapped in try/catch with a defined default behavior

---

## Data Flow

```
Public Financial Data (SEC EDGAR API or CSV upload)
        ↓
   Python Service
   - SEC EDGAR path: fetches and parses raw filings
   - CSV upload path: sanitizes, validates, and structures raw CSV
   Both paths produce identical clean structured metrics
        ↓
   Python Extractor — outputs clean structured metrics dict
        ↓
   ┌────┴──────────────────────┐
   ↓                           ↓
  C Math Engine             Java Scorer
  (receives clean numbers    (weighted health score,
   only — no file I/O,        distress flags,
   pure ratio computation)    credit recommendation)
   if C fails → Java fallback computes same ratios
   └────────┬────────────────┘
            ↓
       R Analytics
       (multi-period trends, statistical report)
        if R fails → Python fallback (pandas, scipy)
            ↓
       SAP AI Core (optional — feature-flagged via application.properties)
       (natural language summary of results,
        plain English explanation of ratios,
        reasoning behind risk rating and credit recommendation)
        If disabled: raw JSON returned without summary field
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
├── backend/                           ← Java Spring Boot
│   ├── src/main/java/com/firmhealth/
│   │   ├── api/                       ← AnalysisController.java
│   │   ├── dto/                       ← AnalyzeRequestDTO.java, AnalyzeResponseDTO.java
│   │   ├── service/                   ← AnalysisService.java (stubbed, wired Phase 2-4)
│   │   ├── scoring/                   ← HealthScorer.java, DistressFlags.java
│   │   ├── credit/                    ← CreditAdvisor.java
│   │   └── bridge/                    ← CBridge.java, RBridge.java, PythonBridge.java, fallback logic
│   ├── src/main/resources/
│   │   └── application.properties     ← port 8080, Swagger config
│   ├── pom.xml                        ← Maven dependencies
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

## Phase Completion Log

### Phase 0 — Complete
- Folder structure created and pushed to GitHub
- `.gitignore` configured for Java, Python, R, and general secrets
- `ml_service/Dockerfile` written (not yet buildable — pending `requirements.txt` and `app.py` from Phase 3)

### Phase 1 — Complete
Spring Boot 3.5.14 backend running on port 8080. All four REST endpoints live and tested.

**Files created:** `FirmHealthApplication.java`, `AnalysisController.java`, `AnalyzeRequestDTO.java`, `AnalyzeResponseDTO.java`, `AnalysisService.java`, `application.properties`, `pom.xml`

**Dependencies:** Spring Web, Validation, Actuator, DevTools, Lombok, SpringDoc OpenAPI, Spring Boot Test

**Endpoints confirmed working:**

| Method | Endpoint | Status |
|---|---|---|
| POST | `/api/analyze` | Stub response, validation working |
| GET | `/api/report/{ticker}` | Stub response |
| GET | `/api/company/{ticker}` | Stub metrics map |
| DELETE | `/api/report/{ticker}` | 204 No Content |

**Validation tested:** uppercase-only ticker enforced, empty input rejected with 400.  
**Swagger UI** live at `/swagger-ui.html`.  
`AnalysisService` methods are stubbed — real logic wired in as Phases 2–4 complete.

---

### Phase 2 — Complete
C math engine built and integrated with Java via subprocess. Apache 2.0 license added.

**Files created:** `math_engine/ratios.h`, `math_engine/ratios.c`, `math_engine/Makefile`, `math_engine/cJSON.c`, `math_engine/cJSON.h`, `backend/.../bridge/CBridge.java`, `LICENSE`

**Files modified:** `backend/.../service/AnalysisService.java`, `.gitignore`

**Key decisions:**
- Subprocess over JNI — C crash cannot take down the JVM; `ProcessBuilder` isolates the risk
- stdin/stdout JSON as IPC — Java speaks JSON natively, no extra parsing layer needed
- Sentinel value `-1.0` for divide-by-zero — distinguishes error from a valid zero ratio
- Java fallback mirrors C exactly — `computeRatiosFallback()` runs silently if C returns null

**Ratios implemented:** Current Ratio, Debt-to-Equity, Profit Margin, Interest Coverage, Altman Z-Score — all five with divide-by-zero guards.

**Still stubbed:** `analyze()`, `getReport()`, `deleteReport()`, and real input data — all unblocked by Phase 3.

---

### Phase 3 — In Progress

**Bug fix carried over from Phase 2:** The `-1.0` sentinel value for divide-by-zero in the C math engine is incorrect — negative ratios are valid in finance (negative equity, negative profit margins). Fix before Phase 3 builds on top of C output:
- In C: return `NAN` (from `math.h`) instead of `-1.0`
- In Java CBridge/fallback: check with `Double.isNaN(result)` instead of `result == -1.0`
- Update decisions.md to document why this was caught and corrected

| Phase | Content | Estimated Time | Status |
|---|---|---|---|
| **Phase 0** | Concepts, folder structure, Git setup, first Dockerfile | 1 week | ✅ Complete |
| **Phase 1** | Java Spring Boot skeleton, REST endpoints, DTOs, service layer | 2 weeks | ✅ Complete |
| **Phase 2** | C ratio engine, JNI integration, Java fallback for C, Apache 2.0 license | 2 weeks | ✅ Complete |
| **Phase 3** | Python ML service (Flask + distress model) | 1.5 weeks | 🔄 In Progress |
| **Phase 4** | R analytics service (Plumber + trend analysis), Python fallback for R, swap to All Rights Reserved license | 1.5 weeks | ⏳ Pending |
| **Phase 5** | SAP Build Apps frontend, SAP AI Core summarization (optional, feature-flagged), API wiring | 2 weeks | ⏳ Pending |
| **Phase 6** | Docker Compose polish, Kubernetes config | 1 week | ⏳ Pending |
| **Phase 7** | Testing, documentation, final polish — all four deliverable documents | 2 weeks | ⏳ Pending |

**Total: ~13–14 weeks** at 8–12 hours per week.  
**Minimum viable product** (Phases 0–3): ~5–6 weeks, fully functional core.

---

## Phase 7 Deliverables

Four documents to be produced at the end of Phase 7 once the full system is built and working.

### 1. User Manual
**Audience:** Credit analysts and finance teams using the product day-to-day.
- How to submit a ticker for a public firm analysis
- How to upload a CSV for a private firm
- How to provide evaluator profile information
- How to interpret the health score, distress flags, and credit recommendation
- How to re-run an analysis
- **Deployment Appendix** (for IT teams):
  - Docker Compose self-hosted setup
  - SAP BTP deployment path
  - Kubernetes production deployment
  - Environment variables and secrets configuration
  - Health check endpoints (Spring Boot Actuator)
  - Troubleshooting common failures

### 2. Project Memo
**Audience:** CTO or technical lead evaluating adoption or integration.
- Full architecture overview
- Why each language was chosen (links to decisions.md)
- Fallback strategy and circuit breaker pattern
- Data sources and their limitations
- Hosting options
- How to update the system:
  - Adding a new financial ratio (which files to touch across C, Java, Python)
  - Updating the ML distress model with new training data
  - Changing scoring weights in the Java health scorer
  - Adding a new API endpoint
  - Rebuilding and redeploying after a change
- **Two versions:** one for senior developers, one for student interns — same content, different assumed background knowledge

### 3. Pitch Document
**Audience:** Salespeople presenting to enterprise clients.
- The problem: manual credit analysis takes hours per firm
- The solution: automated pipeline that produces a structured report in seconds
- Who it's for: any firm evaluating a credit relationship (large evaluating small, small evaluating large, private-to-private, public-to-public)
- What it replaces: junior analyst hours, spreadsheet-based ratio calculations
- Competitive advantage: multi-source data, explainable outputs, deployable anywhere
- Before/after comparison
- No code, no architecture — pure business value

### 4. decisions.md
**Audience:** Future developers and Shriyans in interviews.
- Started in Phase 3, updated every phase
- Captures: what was decided, what alternatives existed, why this option was chosen, what the tradeoff is
- Backfills Phases 0–2 decisions retroactively

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
| **Phase 2 chat** | C math engine, subprocess bridge, Java fallback |
| **Phase 3 chat** | Python Flask ML service, decisions.md started |
| **Phase 4 chat** | R Plumber analytics — **R is learned from scratch in this chat** |
| **Phase 5 chat** | SAP Build Apps frontend, SAP AI Core integration |
| **Phase 6 chat** | Docker Compose, Kubernetes |
| **Phase 7 chat** | Testing, all four deliverable documents |

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
