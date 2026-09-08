# FirmHealth Financials Analyzer

> **Copyright 2026 Shriyans Singh. All Rights Reserved.**
> This software and its source code are proprietary and confidential. Unauthorized copying, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the copyright holder.

---

## What Is This?

FirmHealth is a multi-language enterprise financial analysis platform that automates credit risk assessment. It ingests public and private company financial data, computes key financial ratios, scores financial health, flags distress signals, and produces structured credit limit recommendations — automating what a junior financial analyst would spend hours doing manually.

The core principle: give any organization the ability to evaluate a counterparty's financial health before extending credit, regardless of whether that counterparty is a publicly listed firm or a private company.

---

## Architecture

```
┌─────────────────────────────────────┐
│      HTML/CSS/JS Frontend           │
│    Consumes REST API via HTTP/JSON  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     Java Spring Boot (Core API)     │  ← Main backend (port 8080)
│  - REST endpoints                   │
│  - Business logic & orchestration   │
│  - Health scoring engine            │
│  - Distress flags & credit rules    │
│  - Calls C math engine via subprocess│
│  - Calls Python ML service via HTTP │
│  - Calls R analytics via HTTP       │
│  - AI summarization (optional)      │
└──────┬──────────────┬───────────────┘
       │              │
       ▼              ▼
┌────────────┐  ┌─────────────────────┐
│ C Engine   │  │  Python ML Service  │
│ (port n/a) │  │  (port 5000)        │
│ subprocess │  │  SEC EDGAR + ML     │
└────────────┘  └──────────┬──────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │  R Analytics    │
                  │  (port 8001)    │
                  │  Trend analysis │
                  └─────────────────┘
```

### Why Multi-Language?

Each language is chosen because it is the natural fit for its role — mirroring how real enterprise financial software is actually built.

| Language | Role | Real-World Parallel |
|---|---|---|
| **Java** | Main backend, business logic, scoring engine | Bloomberg Terminal, SAP core systems, banking infrastructure |
| **Python** | ML service, data ingestion, financial data APIs | Quant desks, fintech data pipelines |
| **C** | Math engine — ratio calculations | High-frequency trading, performance-critical financial math |
| **R** | Statistical analysis, trend modeling | Academic finance, risk modeling, actuarial science |
| **HTML/CSS/JS** | Frontend dashboard | Zero build complexity, no framework dependency |

---

## Features

- **Public firm analysis** — enter a ticker symbol, data pulled automatically from SEC EDGAR
- **Private firm analysis** — upload a CSV of financial statements
- **Two-sided evaluation** — both the evaluator and subject can be public or private firms
- **Evaluator profile** — credit recommendations calibrated to the evaluating organization's own financial position and internal policy
- **Five financial ratios** — Current Ratio, Debt-to-Equity, Profit Margin, Interest Coverage, Altman Z-Score
- **ML distress model** — logistic regression trained on 78,682 rows of NYSE/NASDAQ bankruptcy data (1999–2018)
- **Statistical trend analysis** — multi-period trend modeling via R
- **AI summarization** — optional plain English explanation of results (SAP AI Core, Google AI Studio, or Ollama)
- **Full fallback chain** — C fails → Java recomputes; R fails → Python handles trends; Python fails → Java returns partial response
- **Custom training data** — upload proprietary financial datasets to retrain the distress model

---

## Supported Evaluation Scenarios

| Evaluator | Subject | Data Source |
|---|---|---|
| Public firm | Public firm | Both via SEC EDGAR |
| Public firm | Private firm | Evaluator via EDGAR, subject via CSV |
| Private firm | Public firm | Evaluator via CSV, subject via EDGAR |
| Private firm | Private firm | Both via CSV upload |

---

## Project Structure

```
FirmHealth/
│
├── backend/                           ← Java Spring Boot
│   ├── src/main/java/com/firmhealth/
│   │   ├── api/                       ← AnalysisController.java
│   │   ├── dto/                       ← Request/Response DTOs
│   │   ├── service/                   ← AnalysisService.java
│   │   ├── scoring/                   ← HealthScorer.java, DistressFlags.java
│   │   ├── credit/                    ← CreditAdvisor.java
│   │   └── bridge/                    ← CBridge.java, RBridge.java, PythonBridge.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── math_engine/                       ← C
│   ├── ratios.c
│   ├── ratios.h
│   ├── cJSON.c / cJSON.h
│   └── Makefile
│
├── ml_service/                        ← Python
│   ├── app.py                         ← Flask entry point
│   ├── ingestor.py                    ← SEC EDGAR ingestion
│   ├── sanitizer.py                   ← CSV ingestion
│   ├── model.py                       ← ML distress model
│   ├── train_model.py                 ← Model training
│   ├── fallback_trends.py             ← Python fallback for R
│   └── data/                          ← Training datasets
│
├── analytics/                         ← R
│   ├── plumber.R                      ← API entry point
│   ├── trends.R                       ← Trend analysis
│   ├── report_gen.R                   ← Report generation
│   └── run.R                          ← Startup script
│
├── frontend/                          ← HTML/CSS/JS
│   ├── index.html
│   ├── style.css
│   └── dashboard.js
│
├── tools/
│   └── check_sap_models.py            ← SAP AI Core model checker utility
│
├── docker-compose.yml
├── kubernetes/
│   ├── java-deployment.yaml
│   ├── python-deployment.yaml
│   └── r-deployment.yaml
│
├── decisions.md                       ← Architectural decision log
├── draft1.md                          ← Full project plan and phase log
└── firmhealthcodes.md                 ← Full codebase reference
```

---

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/analyze` | Submit ticker, trigger full analysis pipeline |
| `GET` | `/api/report/{ticker}` | Retrieve generated health report |
| `GET` | `/api/company/{ticker}` | Get raw extracted financial metrics |
| `DELETE` | `/api/report/{ticker}` | Remove a stored report |

All responses are JSON. The API is stateless.

---

## Running Locally

### Prerequisites
- Docker and Docker Compose installed
- No SAP account required for core functionality

### Start all services
```bash
docker compose up
```

This spins up four containers:
- Java backend on port 8080
- Python ML service on port 5000
- R analytics on port 8001
- Nginx frontend on port 80

### Build the C math engine manually (for development)
```bash
cd math_engine
make executable
```

### Run the Python service manually
```bash
cd ml_service
pip install -r requirements.txt
python train_model.py
python app.py
```

### Run the Java backend manually
```bash
cd backend
mvn spring-boot:run
```

### Health checks
```
GET http://localhost:8080/actuator/health   ← Java backend
GET http://localhost:5000/health            ← Python ML service
GET http://localhost:8001/health            ← R analytics
```

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## Configuration

All configuration lives in `backend/src/main/resources/application.properties`.

```properties
# Server
server.port=8080

# C math engine path — adjust per deployment environment
firmhealth.c-engine.path=../math_engine/ratios

# Service URLs
firmhealth.r-analytics.url=http://localhost:8001
firmhealth.python-service.url=http://localhost:5000

# AI summarization — set provider to: sap | google | ollama | none
firmhealth.ai-summary.provider=none

# SAP AI Core (if provider=sap)
firmhealth.ai-summary.sap.model=anthropic--claude-4.5-sonnet
# Other supported models:
# anthropic--claude-4.5-haiku
# gpt-5-nano
# gpt-5-mini
# gemini-2.5-flash
# gemini-2.5-flash-lite
# gemini-3.1-flash-lite
```

---

## AI Summarization (Optional)

FirmHealth can append a plain English explanation of the analysis results using an LLM. This is off by default and can be enabled by setting `firmhealth.ai-summary.provider` in `application.properties`.

| Provider | Cost | Notes |
|---|---|---|
| `sap` | Paid | SAP AI Core via SAP Orchestration — enterprise grade |
| `google` | Free tier | Google AI Studio — fast, cloud-based |
| `ollama` | Free | Self-hosted in Docker — full data privacy, no external API |
| `none` | Free | Disabled — raw JSON returned without summary field |

### Checking available SAP AI Core models
```bash
cd tools
python3 check_sap_models.py
```
Set environment variables `SAP_AUTH_URL`, `SAP_CLIENT_ID`, `SAP_CLIENT_SECRET`, `SAP_AI_API_URL`, `SAP_ORCHESTRATION_DEPLOYMENT_ID` before running.

---

## Fallback Chain

FirmHealth is designed so that no single service failure takes down the whole pipeline.

| Primary | Fails | Fallback |
|---|---|---|
| C math engine | Crash or missing binary | Java recomputes same ratios natively |
| R analytics | Plumber service down | Python (pandas + scipy) handles trend analysis |
| Python ML service | Flask service down | Java returns partial response, flags ML as unavailable |

All fallback events are logged. The end user receives a response regardless.

---

## Data Sources

- **SEC EDGAR API** — free, official US public company filings (primary)
- **Yahoo Finance** — fallback for market cap data only
- **CSV upload** — manual input for private firm financial statements
- **Custom training data** — `POST /train` on the Python service accepts proprietary datasets

---

## Financial Metrics Computed

| Metric | Formula |
|---|---|
| Current Ratio | Current Assets / Current Liabilities |
| Debt-to-Equity | Total Debt / Total Equity |
| Profit Margin | Net Income / Revenue |
| Interest Coverage | EBIT / Interest Expense |
| Altman Z-Score | 1.2(WC/TA) + 1.4(RE/TA) + 3.3(EBIT/TA) + 0.6(MCE/TL) + (S/TA) |

---

## Production Deployment (SAP BTP)

SAP Business Technology Platform supports Docker containers natively. Deploy each service as a container on BTP and point the SAP Build Apps or HTML frontend at the exposed API endpoint.

For Kubernetes deployment:
```bash
kubectl apply -f kubernetes/
```

---

## Development Status

| Phase | Description | Status |
|---|---|---|
| Phase 0 | Folder structure, Git, Dockerfile | ✅ Complete |
| Phase 1 | Java Spring Boot core API | ✅ Complete |
| Phase 2 | C math engine, Java fallback | ✅ Complete |
| Phase 3 | Python Flask ML service | ✅ Complete |
| Phase 4 | R analytics, Python fallback, license | ✅ Complete |
| Phase 5 | Frontend, AI summarization | 🔄 In Progress |
| Phase 6 | Docker Compose, Kubernetes | ⏳ Pending |
| Phase 7 | Testing, documentation | ⏳ Pending |

---

## Built By

**Shriyans Singh** — sophomore at Stony Brook University, BS Business Management + MS Computer Science (AI & Data Science, Honors).

*For licensing inquiries or commercial use, contact the copyright holder directly.*
