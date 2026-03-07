# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

"toldyouso" is a Java web application that lets users save timestamped, unmodifiable predictions to later prove "I told you so." It's a Jakarta EE (JSF/CDI) app deployed as a WAR on Tomcat with CouchDB as the database.

## Build & Run

**Prerequisites:** Java 21, Maven, Docker, curl

**Build:**
```bash
mvn package          # produces target/toldyouso.war
mvn clean package    # clean build
```

**Run locally (full stack via Docker Compose):**
```bash
docker compose up     # builds app image, starts CouchDB + Tomcat
```
App available at `http://localhost:8080`.

**Build & push image:**
```bash
./oglimmer.sh build          # build Docker image and push to registry
./oglimmer.sh release        # interactive version bump, tag, build, push
./oglimmer.sh show           # show current version
./oglimmer.sh --help         # all options
```

**Configuration:** Loaded from a properties file specified via `-Dtoldyouso.properties=<path>`. Properties include `couchdb.host`, `couchdb.port`, `couchdb.user`, `couchdb.password`, `toldyouso.domain`, and SMTP settings (`smtp.host`, `smtp.port`, `smtp.ssl`, `smtp.user`, `smtp.password`, `smtp.from`). See `Configuration.java` for defaults.

## Architecture

- **Java 21, Jakarta EE 10 (Faces 4.0, CDI 4.0)** — runs on Tomcat 10.1 with Weld 5 (CDI) and Mojarra 4 (JSF)
- **UI:** Standard JSF (Facelets) with Bootstrap 5 via WebJars, XHTML templates in `src/main/webapp/`
- **Database:** CouchDB accessed via Ektorp library. CouchDB view definitions in `src/couchdb/`
- **Key packages under `src/main/java/de/oglimmer/`:**
  - `model/` — Domain objects (`User`, `SmartAssEntry`)
  - `db/` — DAO interfaces; `db/couchdb/` — CouchDB implementations using Ektorp
  - `web/` — Servlet (`GetServlet` routes `/` and `/<id>` paths) and context listener
  - `web/beans/` — JSF managed beans (login, registration, save/get entry portals)
  - `toldyouso/log/` — Logback configurator
  - `util/` — Configuration singleton, crypto (bcrypt), email service, link generation
- **Routing:** `GetServlet` maps `/` — root goes to `index.xhtml`, any `/<id>` path forwards to `display.xhtml` to show a saved entry
- **Lombok** is used for boilerplate reduction (provided scope)
- **No tests** — the project has no unit or integration tests
- **Deployment:** Multi-stage Dockerfile (Maven build → Tomcat 10.1 runtime). Helm chart in `helm/toldyouso/` for Kubernetes deployment
