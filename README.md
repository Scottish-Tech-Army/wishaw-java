# Wishaw Java API

Spring Boot backend for the Wishaw YMCA Esports Badge Portal.

This service provides authentication, profile management, badges, modules, tournaments, leaderboards, notifications, local H2 development, PostgreSQL support, and Kubernetes deployment manifests.

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+ if you also want to run the frontend

### Run Locally With H2

```powershell
mvn spring-boot:run
```

The default profile is `h2`, so the API starts with seeded demo data.

Useful local URLs:

- API health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`

Demo users:

- `admin@wymca.org` / `admin123`
- `player1@wymca.org` / `player123`
- `player2@wymca.org` / `player123`

## Frontend Pairing

Run the sibling frontend repo after this API is up:

```powershell
cd ..\51a7db-tfg_hack_wishaw-react
npm ci
npm run dev
```

The frontend runs on `http://127.0.0.1:3000` and proxies `/api` to this backend.

## Build

```powershell
mvn clean package
```

Important: stop any running jar before rebuilding. On Windows, the packaged jar can remain locked if it is still running from `target`.

## Deploy

- Local persistent DB: use the `postgres` profile.
- Kubernetes: manifests live in `k8s/`.
- Container image: build from the included `Dockerfile` after packaging the jar.

## Full Guide

See [docs/setup-run-deploy.md](docs/setup-run-deploy.md) for full setup, run, build, and deployment steps.
