# FirmHealth -  Decisions.md

## Phase 0

### Decision 1 - Folder structure separated by language
**Decided:** structured and separated files by purpose
**Alternatives considered:** Single folder for all code / monorepo flat structure
**Why this option:** keep clutter free and easier to update
**Tradeoff:** Additional overhead due to jumping from folder to folder, and has to jump through directories when developing.

### Decision 2 - One Dockerfile per service
**Decided:** one docker per microsoervice
**Alternatives considered:** Single Dockerfile for entire project
**Why this option:** if one docker fails, the rest dont. also reduces the overhead per individual containers, so if some containers aren't activated it reduces overhead
**Tradeoff:** more complex and easier for one to fail (kubernates should help with this in future implementations)

---

## Phase 1

### Decision 3 - Java Spring Boot as main backend
**Decided:** springboot as main backend
**Alternatives considered:** Python FastAPI / Node.js Express
**Why this option:** language I know best, is often used in enterpise applications for its stability
**Tradeoff:** Increased complexity

### Decision 4 - Subprocess over JNI for C bridge
**Decided:** Subprocess
**Alternatives considered:** JNI (Java Native Interface)
**Why this option:** If the C math engine subprocess fails it wont crash the entire system, but a JNI has the potential to crash the system
**Tradeoff:** More complexity and latency

---

## Phase 2

### Decision 5 - NAN over -1.0 as sentinel value for divide-by-zero
**Decided:** NaN
**Alternatives considered:** -1.0 sentinel / 0.0 default
**Why this option:** NaN is a clear declaration that this is an invalid result, while -1.0 or 0.0 were potentially valid results and saying them could lead to faulty decisions
**Tradeoff:** No real tradeoffs except having to add checks in the analysis

### Decision 6 - JSON over stdin/stdout as IPC between Java and C
**Decided:** JSON
**Alternatives considered:** Binary format / CSV over stdin
**Why this option:** The API calls would have to use JSON format anyways so doing it this way is safer. Binary format I don't know so attempting that is dangerous, and CSV over stdin is risky because C requires structured files, and if its not done right, it could crash. Stdin/stdout is also local to the machine so it doesn't riks having leaked data, although the data isn't really something that needs to be worried about, its still good practice
**Tradeoff:** Having to read documentation to ensure the stdin is being done right.

---

## Phase 3

### Decision 7 - Flask as Python microservice framework
**Decided:** Flask
**Alternatives considered:** FastAPI / Django
**Why this option:** I have used Flask in a minor way during my past internship in Incture Technologies (Summer 2025). Additionally, it is simple and faster to set up. 
**Tradeoff:** Not as fast as FastAPI, and not as professional as Django

### Decision 8 - SEC EDGAR as primary data source
**Decided:** SEC Edgar
**Alternatives considered:** Yahoo Finance only / Bloomberg API
**Why this option:** SEC EDGAR is the official data, and is the most reliable data source for trust. Yahoo Finance as backup in case the SEC EDGAR scraper fails is due tto its long history of being mostly accurate and stable
**Tradeoff:** Often changes due to updates, and requires constant maintaince to ensure the SEC EDGAR portion is working

### Decision 9 - Yahoo Finance as fallback only
**Decided:** Yahoo Finance
**Alternatives considered:** Calling both simultaneously and reconciling
**Why this option:** Fallback because its getting mostly the same data over both, with minor discrepancies and thus isn't worth doing both at the same time and wasting resources
**Tradeoff:** Not as accurate data, diversifying sources is always good.

### Decision 10 - Logistic regression over complex ML models
**Decided:** Logistic Regression
**Alternatives considered:** Neural network / Random forest / Rule-based scoring
**Why this option:** Neural is far too complex, random forest is slower to compute and and less efficient, rule based can't handle relationship between data easily, logistic regression is standard for credit risk assessment, is easy to interpret, and lightweight
**Tradeoff:** struggles with complex data, which is finance (fails to account for reputation and sentiment, among other data)

### Decision 11 - joblib for model serialization
**Decided:** joblib
**Alternatives considered:** Retraining on every startup / pickle
**Why this option:** retraining is a waste of computational resources and can result in differing results for the same ticker, and pickle is more general, while joblib is considered a recommended standard for scikit, which is what I am using for the logisitcal regression model, and is much faster than pickle. 
**Tradeoff:** higher overhead than pickle, insecure (but so is pickle)

### Decision 12 - Custom training data via /train route
**Decided:** Custom Training 
**Alternatives considered:** Hardcoded dataset only / Deferred to v2
**Why this option:** Hardcoded wouldn't be as accurate as real world data and deferring to v2 would not allow me to test to ensure this is actually working.
**Tradeoff:** real world existing data sets that are available for general public is not the best and is not optimized for my usage.

### Decision 13 - Example CSV for custom CSV format
**Decided:** make a example csv
**Alternatives considered:** raw financial columns and Python computes ratios
**Why this option:** Makes it clear to users what the expected input for custom CSV should look like
**Tradeoff:** No real trade off, using the sample data set as the basis would not have worked well considering some data columns were missing, and it would be difficult to figure out which column is which ratio, and how to get the ingestor to work.

### Decision 14 - extractor.py cut from Phase 3
**Decided:** remove it
**Alternatives considered:** Keeping extractor.py as separate validation layer
**Why this option** data is already validated during ingestion
**Tradeoff:** validation is now implicit rather than explicit, so if it gets complex in later updates, its harder to find and justify.

---

*This document is updated at the end of every phase.*