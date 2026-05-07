# Run and Test Guide

This document explains how to run, verify, and test the Wishaw YMCA Esports badging backend locally.

For a full end-to-end connectivity and showcase checklist across backend and frontend, see `docs/backend-frontend-connectivity-validation.md`.

The application is a Spring Boot REST API with:

- Spring Boot `3.5.11`
- Java `17` source compatibility
- H2 in-memory database
- session-based authentication using `JSESSIONID`
- a static OpenAPI contract served from `/openapi.yaml`

## Quick Start

If you only need the shortest working path on this workstation:

1. Open a shell in the repository root.
2. Start the app with:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -DskipTests spring-boot:run
```

3. Wait for startup logs to show that Spring Boot and the seed data are ready.
4. Open `http://localhost:8080/openapi.yaml` or log in with one of the seeded accounts listed below.
5. Run the test suite with:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true test
```

## Project Runtime Snapshot

| Setting | Value |
| --- | --- |
| Application name | `wishaw-java` |
| Default port | `8080` |
| Database | `jdbc:h2:mem:wishawdb` |
| H2 console path | `/h2-console` |
| OpenAPI path | `/openapi.yaml` |
| Auth style | Session cookie |
| Seeded super admin | `superadmin` / `admin123` |

These values come from `pom.xml` and `src/main/resources/application.properties`.

## Prerequisites

You need the following installed locally:

- Java `17` or newer
- Maven `3.9+`
- Network access to the Maven repositories configured for your environment

This repository has been exercised in this workspace using Java `24`, but the project itself targets Java `17`.

## Known Environment Constraints On This Workstation

This machine has a few environment-specific constraints that affect the commands you should use:

- `mvnw.cmd` is blocked by local group policy.
- Default Maven dependency resolution can fail due certificate trust and revocation issues when talking to the corporate artifact infrastructure.
- The reliable workaround on this machine is to force Maven onto Wagon transport and allow the SSL bypass flags to take effect.

Because of that, the recommended commands in this document use:

```bash
-Dmaven.resolver.transport=wagon
-Dmaven.wagon.http.ssl.insecure=true
-Dmaven.wagon.http.ssl.allowall=true
```

If your own workstation resolves Maven dependencies normally, you can use the shorter standard commands shown in each section.

## Repository Root

Run all commands from the repository root:

```bash
cd C:/Users/5611124/source/repos/51a7db-tfg_hack_wishaw-java
```

In Git Bash, the same location is typically:

```bash
cd ~/source/repos/51a7db-tfg_hack_wishaw-java
```

## Start The Application

### Recommended command for this workstation

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -DskipTests spring-boot:run
```

### Standard command if Maven works normally

```bash
mvn -DskipTests spring-boot:run
```

### What successful startup looks like

Startup is healthy when the logs show messages equivalent to:

- Tomcat started on port `8080`
- H2 console available at `/h2-console`
- `WishawJavaApplication` started successfully
- `DataSeeder` logged that seed data loaded successfully

Stop the running backend with `Ctrl+C` in the shell where it is running.

## Local URLs After Startup

Once the backend is running, these URLs should respond locally:

| Purpose | URL |
| --- | --- |
| Base application | `http://localhost:8080` |
| OpenAPI contract | `http://localhost:8080/openapi.yaml` |
| H2 console | `http://localhost:8080/h2-console` |

## H2 Console Access

The H2 console is enabled only for local development.

Use these settings when signing in:

| Field | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:wishawdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` |
| User name | `sa` |
| Password | leave blank |

Important notes:

- This is an in-memory database.
- All data resets when the app stops.
- Seed data is recreated on each fresh startup.

## Seeded Accounts

The backend seeds development users and sample data on startup.

| Role | Username | Password | Typical Use |
| --- | --- | --- | --- |
| Super admin | `superadmin` | `admin123` | Full admin access |
| Centre admin | `centreadmin` | `admin123` | Centre-scoped admin testing |
| Player | `player1` | `player123` | Player self-service profile/progress testing |
| Parent | `parent1` | `parent123` | Parent read-only flow testing |

## Authentication Model

This API uses session-based authentication.

The flow is:

1. `POST /api/v1/auth/login`
2. The server establishes a session and returns a `JSESSIONID` cookie.
3. Subsequent authenticated requests must send that cookie back.

If you call authenticated endpoints without the session cookie, you should expect `401 Unauthorized` or `403 Forbidden` depending on the route and role requirements.

## Verify The OpenAPI Document

The API contract is served as a static file from:

```text
src/main/resources/static/openapi.yaml
```

To fetch it locally after startup:

```bash
curl http://localhost:8080/openapi.yaml
```

Or open it in a browser:

```text
http://localhost:8080/openapi.yaml
```

## Run The Full Test Suite

### Recommended command for this workstation

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true test
```

### Standard command if Maven works normally

```bash
mvn test
```

This suite covers:

- auth login success and failure
- logout and session behavior
- progress recalculation
- legacy points update
- challenge award flow
- parent read-only authorization
- leaderboard behavior
- CSV preview parsing
- CSV commit idempotency

## Useful Targeted Test Commands

Run the main backend integration suite:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dtest=BackendScaffoldIntegrationTests test
```

Run the workflow and contract integration suite:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dtest=BackendWorkflowContractIntegrationTests test
```

Run the focused unit tests for business logic:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dtest=ProgressServiceTest,LeaderboardServiceTest,AuthServiceTest,CsvParserTest test
```

Run a single test class by name:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dtest=ProgressServiceTest test
```

## Optional Build Command

If you want to compile and package the application without running the tests:

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -DskipTests package
```

If your environment does not need the workaround:

```bash
mvn -DskipTests package
```

## Smoke Test The Running Application With curl

The examples below assume the backend is already running on `http://localhost:8080`.

### Admin login

```bash
curl -i -c cookies.txt -H "Content-Type: application/json" -d '{"username":"superadmin","password":"admin123"}' http://localhost:8080/api/v1/auth/login
```

### Check the authenticated user

```bash
curl -b cookies.txt http://localhost:8080/api/v1/auth/me
```

### Check the global leaderboard

```bash
curl -b cookies.txt http://localhost:8080/api/v1/leaderboards/global
```

### Log in as the seeded player and fetch their profile

```bash
curl -i -c player-cookies.txt -H "Content-Type: application/json" -d '{"username":"player1","password":"player123"}' http://localhost:8080/api/v1/auth/login
curl -b player-cookies.txt http://localhost:8080/api/v1/me/profile
```

### Log in as the seeded parent and list linked players

```bash
curl -i -c parent-cookies.txt -H "Content-Type: application/json" -d '{"username":"parent1","password":"parent123"}' http://localhost:8080/api/v1/auth/login
curl -b parent-cookies.txt http://localhost:8080/api/v1/parent/players
```

## Smoke Test The Running Application In PowerShell

On Windows, `curl` inside PowerShell is often an alias rather than the actual `curl.exe` binary. If you want the real curl behavior, use `curl.exe` explicitly.

### Option 1: use curl.exe

```powershell
curl.exe -i -c cookies.txt -H "Content-Type: application/json" -d '{"username":"superadmin","password":"admin123"}' http://localhost:8080/api/v1/auth/login
curl.exe -b cookies.txt http://localhost:8080/api/v1/auth/me
```

### Option 2: use Invoke-RestMethod with a web session

```powershell
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

Invoke-RestMethod \
	-Method Post \
	-Uri "http://localhost:8080/api/v1/auth/login" \
	-WebSession $session \
	-ContentType "application/json" \
	-Body '{"username":"superadmin","password":"admin123"}'

Invoke-RestMethod \
	-Method Get \
	-Uri "http://localhost:8080/api/v1/auth/me" \
	-WebSession $session
```

## Recommended Manual Verification Checklist

After starting the backend, a good manual verification pass is:

1. Fetch `/openapi.yaml` and confirm the document loads.
2. Log in as `superadmin` and call `/api/v1/auth/me`.
3. Log in as `player1` and call `/api/v1/me/profile`.
4. Log in as `parent1` and call `/api/v1/parent/players`.
5. Call `/api/v1/leaderboards/global` with an authenticated session.
6. Open `/h2-console` if you need to inspect seeded rows directly.

## Troubleshooting

### `mvnw.cmd` does not run

On this workstation, that is expected. Use `mvn` instead of the Maven wrapper.

### Default Maven command fails with certificate or plugin resolution errors

Use the Wagon-based command variants from this guide. That is the known-good path in this environment.

### A direct Maven Central override was attempted and failed

Avoid relying on the `.mvn/direct-central-settings.xml` override path on this machine. The working path here is the Wagon-based Maven command, not the direct-central settings override.

### Port `8080` is already in use

Stop the process using that port or temporarily change `server.port` in `src/main/resources/application.properties`.

### Login succeeds but later API calls return `401`

That usually means the session cookie was not sent back. Make sure you preserve and resend `JSESSIONID`.

### Data disappears after a restart

That is expected. H2 is configured as an in-memory database for local development.

### Test output contains warnings about `Unsafe`, Mockito agent loading, or `H2Dialect`

Those warnings were observed during local test runs in this workspace. They are currently non-fatal and do not stop the suite from passing.

## Summary

For this workstation, the safest default workflow is:

1. Start with the Wagon-based `spring-boot:run` command.
2. Verify `openapi.yaml`, login, and one or two role-based endpoints.
3. Run the Wagon-based `mvn test` command.

That path is the one already exercised successfully against this repository.