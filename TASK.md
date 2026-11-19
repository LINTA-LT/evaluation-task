# PDB assessment task

Thank you for taking part. This document describes what we ask you to do, what you receive, and what we will look at.

## 1. Background

PDB is the public search application for intellectual property rights (trademarks, designs, patents, disputes, Nice classification). It is a Spring Boot application written several years ago:

- Java 8, Spring Boot 2.2, Thymeleaf pages
- trademarks, designs, disputes and Nice classification are searched in Elasticsearch 7.10, which is filled from MySQL 8 by Logstash pipelines; the application talks to Elasticsearch with the 6.8 transport client
- patents are read from MySQL directly through JPA
- an open-data export runs as a Spring Batch job and produces XML files validated against the XSD schemas in `src/main/resources/xsd`

You are taking over this application. We want to see how you set up a legacy project, bring it to a current technology stack, add a useful feature, and communicate about it.

## 2. What you receive

- the application source code (sanitised: all environment-specific configuration and secrets were removed)
- an anonymised MySQL dump with the databases the application uses (`pdb`, `pdb_patents`, `tmview`, `dsview`), to be placed at `dump/pdb-assessment-dump.sql.gz`
- a smoke check in `smoke-check/` for the existing behaviour of the application
- this document

You will not get access to any of our systems. You develop on your own machine. We deploy your submission ourselves into our own isolated environment and test it there, following only your README (see section 4.1).

## 3. Rules

- The task has three parts: deployment (section 4), upgrade (section 5) and new functionality (section 6). Write down how much time you spend on each part, and how much of it was waiting for builds, indexing or downloads. Partial work is welcome. If you run out of time, stop and describe in `NOTES.md` what is missing and how you would finish it.
- You may work on the parts in any order. The final submission must start and run on the upgraded stack from Part 2.
- Use of AI coding assistants is allowed. If you use them, say so in `NOTES.md` and describe what you used them for. You remain responsible for everything you submit: the code, the README and the notes must be consistent with each other, and we will check that.
- We prefer self-explanatory code (clear naming and structure) over explanatory comments. Existing comments should stay as they are.

## 4. Part 1: reproducible deployment

Make the application and everything it needs start from an empty environment by following the README. This is the part we deploy ourselves, so write it for someone who has never seen the project.

Requirements:

1. A documented list of steps starts everything needed: MySQL with the dump loaded, Elasticsearch, Logstash with the pipelines, and the application, all on the upgraded stack from Part 2. Fewer manual steps are better, but the steps may include preparing configuration.
2. The system must be ready within 30 minutes of the start command (see the readiness check below). Keep the time as short as is reasonable for the size of the data and state the time you measured in the README.
3. The versions of the components must be compatible with each other and with what the application uses. Our production MySQL runs with `sql_mode` set to `STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION`.
4. After startup, the four indices in Elasticsearch (trademark, design, dispute and Nice classification) are filled from the dump, because the application pages need all of them, and the application is reachable on port 8080 under `/pdb`. The document counts match the source data by the rule of the existing pipelines: the document id stays as it is in them (the application number for trademarks and designs, the reference for disputes), so records with the same id become one document. Which of several records with the same id survives is not compared.
5. MySQL data and Elasticsearch indices live in persistent volumes. After a normal stop and start, the dump is not imported again, the indices are not rebuilt, Logstash does not reprocess everything, no data is duplicated, and the system becomes ready much faster than on the first start. The documented reset clears the data.
6. The setup must be repeatable on a clean machine. Use Docker and Docker Compose.
7. Settings that differ between environments (credentials, ports, paths) are configurable per environment and not hard-coded in the images or the application, for example through a `.env` file for Docker Compose. How you do it is up to you; document it in the README.
8. Provide a `README` section "Deployment" with prerequisites, steps, how to tell when the system is ready, how to re-run the indexing, how to stop and reset, and common problems.

### 4.1 Target environment and deployment contract

We deploy your submission on a fresh virtual machine with this specification. Please test on a machine that matches it as closely as you can.

| Item | Value |
|---|---|
| Operating system | Ubuntu 24.04 LTS (default kernel settings) |
| Resources | 4 vCPU, 16 GB RAM, 60 GB disk |
| Preinstalled software | Docker Engine, Docker Compose v2, curl, the Eclipse Temurin JDK (the version you choose, see below). Nothing else: no Maven (use the Maven wrapper `./mvnw`, which downloads Maven from Maven Central), no Logstash, no Elasticsearch, no MySQL |
| Network | Outbound internet access. No access to internal networks |

The contract:

- The repository root contains everything needed. We run the commands in your README from the repository root and nothing else.
- You choose how the application is built: inside a container (for example a multi-stage Dockerfile), or on the host with the Temurin JDK and `./mvnw`. The exact Temurin version you need must be stated in the README as a prerequisite; we install that version on the testing machine before we start. Either way, the README steps must produce a working application from a clean checkout, and it must be clear in the README which way you chose. How you develop locally (IDE, local JDK) is up to you.
- We place the dump at `dump/pdb-assessment-dump.sql.gz` in the repository root before the first start. Your setup must load it from this path.
- The application is reachable at `http://<host>:8080/pdb`.
- We consider the system ready when `GET /pdb/trademark/quick-search?q=<a known application number>` returns a page with a link to that record (Part 3). If you did not implement Part 3, document another readiness check. We wait up to 30 minutes from the start command, including image downloads and indexing. Write in the README how long your setup takes.
- Host settings: the host has default kernel settings. If your setup needs a host setting to be changed, document it exactly, but prefer a setup that needs none. We apply only what the README says.
- Reset must be documented and must work (for example `docker compose down -v` followed by the start command).
- If the system does not start by following the README, we may try the troubleshooting steps in it once. We do not debug your configuration. If it still does not start, we assess the remaining parts using our own reference environment, and Part 1 scores low.

Notes:

- The Logstash pipelines in `src/main/resources/logstash-pipelines` are the starting point. You may change them for local use; explain what you changed and why.
- Patents are read from MySQL directly and need no indexing.
- Images are not part of the dump. The application fetches them from a public website configured in `application-local.yml`, and pages may show missing images.

## 5. Part 2: upgrade

Move the application from the legacy stack to the current one. Use the latest stable release of each component that is available when you start, and name the versions you chose in `NOTES.md`:

- Java: the latest long-term-support (LTS) release, for the build and for the runtime
- Spring Boot: the latest stable major version, with the matching Spring Framework and Jakarta namespace
- Elasticsearch: the latest stable major version, with the Logstash of the same version, and the supported Java client of that version instead of the 6.8 transport client (no deprecated or compatibility-mode clients)
- All other dependencies: the latest stable versions that work with the application.

If a component does not work with the latest release, you may use the newest release that does, and you explain why in `NOTES.md`.

Existing behaviour must stay the same: the trademark, design, patent (Lithuanian and EU), dispute and Nice classification pages, and the Excel exports. The open-data export is not part of the evaluation. If its code cannot reasonably be made to compile on the new stack, you may remove it; say so in `NOTES.md`.

Requirements:

1. The versions are visible in `pom.xml`, the `Dockerfile` and the compose file.
2. Replace or remove vulnerable or obsolete libraries where you can, and list what you changed and why in `NOTES.md`.
3. The project has no automated tests, and we do not ask you to write tests for the existing application. Show how you checked that behaviour is preserved. You can use the smoke check provided in `smoke-check/` (requests with recorded expected responses and a script that runs them against the running application). Any additional checks or tests you add are welcome. Describe in `NOTES.md` what you verified and how.

## 6. Part 3: new functionality, a search box for trademarks and designs

Today users must fill in forms with many fields. Add a "quick search": one input box where the user types free text and gets ranked results, similar to a web search engine. It is needed separately for trademarks and for designs (two separate searches, not one combined search). Patents, disputes and Nice classification are out of scope.

### 6.1 Behaviour

The user does not choose which field to search. The same text is matched against these fields:

| Type | Fields |
|---|---|
| Trademark | application number, registration number (if the record has one), mark name (word element), owner / applicant name, goods and services terms (the Nice classification text) |
| Design | application number, registration number (if the record has one), design title or product indication, owner / applicant name |

Rules:

1. Case-insensitive and diacritics-insensitive: `zalgiris` finds `Žalgiris`.
2. Numbers are matched regardless of separators, spaces and letter case. For example, if a number is stored as `2019 1234`, then `2019-1234`, `20191234` and `2019  1234` should all find it. An exact number match must be ranked first.
3. Multi-word queries: all words must be present, in any order. Every word may be incomplete, that is, the beginning of a word in the record (the user is still typing), for example `nord veh` should find `Nordic Vehicles`.
4. Ranking: exact number match first, then exact name match, then matches at the beginning of a name, then other matches. Matches in the mark or design name should generally rank above matches in the owner name, and matches in the owner name above matches in the goods and services terms.
5. Special characters typed by the user (`"`, `*`, `\`, `(`, `:`, `/`, `-`) must not break the query or cause errors, and must not change the meaning of the search in unintended ways.
6. Empty or very short input returns an empty result with a clear indication, not an error. Limit the maximum length of input.
7. The page size is fixed at 50 entries, as on the existing results page. The user does not choose it. The quick search box on the web pages sends the user to the existing results page, which is reused. Change it only where the quick search needs it (for example for the Excel export in rule 9), and keep how it looks, pages and sorts. The quick search URLs (section 6.3) show the same pages of 50 for automated checks.
8. Existing form-based and advanced searches continue to work unchanged.
9. The "Export to Excel" link on the results page exports the quick search results the way it exports the results of the existing searches: the rows of the page that is shown (up to 50, not all matching records), in the order shown (including the selected sorting), with the same columns, images and date cells as the existing export. How you implement it is up to you.

### 6.2 User interface

Add the quick search box to the existing trademark and design search pages and show the results on the existing results page, reusing its templates and current paging. Adjust the page only where the quick search needs it (for example the Excel export). Keep it simple. The interface is not the focus of the assessment, but it must work in a browser, and the lt/en language switch must keep working.

### 6.3 URL contract

Automated checks will call these URLs and read the existing results page, so they must work exactly like this.

```
GET /pdb/trademark/quick-search?q={text}&offset={n}
GET /pdb/design/quick-search?q={text}&offset={n}
```

- The response is the existing results page (`200 OK`, HTML), with the same paging as now: `offset` starts from 0, default 0, and the page size is 50.
- Without `sortField`, the results are in relevance order, best match first. The sorting of the results page keeps working for quick search results, exactly as for the existing searches: the column header links add `sortField` and `sortDir` (for example `sortField=applicationnumber.keyword&sortDir=asc`, also `registrationnumber_numeric`, `dtexpiry` and `status_<lang>.keyword`) to the same URL, keep `q`, and reset `offset`. With them, the results are sorted by that column, in the same order as the existing searches give (for example the existing sort by application number puts the filing date first and the number second; keep that order), and the paging works on the sorted results. The results page keeps what the checks read: the link of each result to its details page (`/pdb/trademark/details/{applicationNumber}` or `/pdb/design/details/{applicationNumber}`, with the application number as stored, not normalised), in the order shown, and the total number of results as shown now.
- Empty or too short `q` returns `200` with the results page showing zero results and a clear message.
- An invalid `offset` (not a number, or negative) returns `400`.

Example queries you can use for your own testing (the full evaluation set is not shared):

- a full application number of a trademark in the dump, written once with the stored separators and once without
- a part of a company name with diacritics, typed without diacritics
- two words of a trademark name in reverse order

### 6.4 Tests

- Write the tests you consider appropriate for the new functionality, and explain in `NOTES.md` what you chose to test and why.
- Tests must not depend on our systems, the environment from Part 1 or the dump. Tests that protect the matching and ranking rules in section 6.1 are the most valuable. A small set of test data (including records that should not match) against a containerised or embedded Elasticsearch is one way to do it, but it is not required.
- Tests must be runnable on the target environment from section 4.1 (Docker, Docker Compose and the Temurin JDK named in your README, Maven through `./mvnw`). Say in the README how to run them.

### 6.5 Code expectations

- Follow the structure and naming of the existing code where it is reasonable. Reuse the existing query-builder utilities where this makes sense.
- Avoid duplicating the same logic for trademarks and designs where it can be shared cleanly.
- We run static analysis (SonarQube, default Java rules) on the code you add or change. Only new and changed code is judged, not the existing legacy code. Keep your changes free of bugs, vulnerabilities and obvious code smells. An IDE plugin such as SonarLint will show you the same kind of findings while you work.

## 7. Notes

Add a file `NOTES.md` with at most two pages, covering:

1. **What you did and did not do.** Include assumptions, deviations from this document, what is unfinished, and any use of AI tools.
2. **Upgrade.** The libraries and components you replaced or removed, and why.
3. **Time.** The time you spent on each part, including waiting and debugging the environment.
4. **Remaining risks.** The main technical risks you still see in the project (for example in dependencies, configuration, testing, operations), in the order you would address them, with a sentence on why for each. Do not implement them.

## 8. Submission

- A zip of the whole project directory including `.git`, with your commits on top of the provided code.
- `README` section "Deployment" and `NOTES.md` as described above.
- Send it by the deadline in the invitation. Questions about the task (not about the solution) can be sent to the contact in the invitation. We will answer within one working day.

## 9. What we will look at

We deploy your submission into our own evaluation environment (section 4.1), following your README literally, and then check:

- whether the environment starts cleanly on a clean machine, how long it takes, how many manual steps it needs, whether data persists across a restart, and how easy it is to reset
- whether the application on the new stack behaves as it did before (a fixed set of requests across trademark, design, patent, dispute, Nice classification and the Excel exports)
- whether the quick search behaves as described, using a set of queries we prepared
- the design and quality of the code and tests, including a static analysis of the new code and a scan of the dependencies
- the written notes
- how you use version control, judged on the commit history
- how clearly you communicate, including honesty about what is unfinished

Each of the three parts (deployment, upgrade, quick search) is scored separately. To pass, a submission must collect at least 50% of the maximum points in every part. A strong result in one part does not make up for a part that is below the minimum.

Your materials and result are confidential and will be used only for this assessment. Please delete the provided code and data after you have submitted your work and received our feedback.
