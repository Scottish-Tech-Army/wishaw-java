# Copilot instructions for the Wishaw backend

## Build, run, and test

- Run the backend locally with `mvn -DskipTests spring-boot:run`.
- Package without tests with `mvn -DskipTests package`.
- Run the full backend suite with `mvn test`.
- Run a single test class with `mvn -Dtest=ProgressServiceTest test`.
- The main integration suites are `mvn -Dtest=BackendScaffoldIntegrationTests test` and `mvn -Dtest=BackendWorkflowContractIntegrationTests test`.
- There is no separate lint task configured in this repo.
- On this workstation, `mvnw.cmd` is blocked and Maven resolution can require the Wagon SSL flags documented in `docs/run-and-test.md`. If standard Maven commands fail here, use the commands from that doc verbatim.

## High-level architecture

- This repo is a Spring Boot REST API under `/api/v1`, not a server-rendered app. The companion UI lives in the sibling repo `51a7db-tfg_hack_wishaw-node-main`.
- Authentication is session-based. `POST /api/v1/auth/login` establishes a Spring session and returns `JSESSIONID`; the Node/EJS frontend stores that cookie in its own session and forwards it on subsequent live requests.
- The main execution path is `controller -> service -> repository/entity`. `SecurityConfig` handles coarse route-level authorization, while service classes still enforce finer rules such as centre scoping.
- Player progress, profile reads, and award flows converge in `service/player/ProgressService.java`. Parent read-only flows layer on top of that through `ParentReadOnlyService`.
- CSV import is a multi-step backend workflow, not a single write endpoint. The live path is upload/parse (`CsvImportService`) -> preview (`ImportPreviewService`) -> map unresolved usernames to existing player accounts (`PlayerMappingService`) -> commit (`ImportCommitService`).
- The static API contract is served from `src/main/resources/static/openapi.yaml`.
- Local development uses an in-memory H2 database. Seed data and credentials come from `DataSeeder` plus `application.properties`, so data resets on restart.

## Key conventions

- Keep the backend DTO-first and JSON-only. Do not add server-side HTML rendering or frontend-specific response shaping here.
- When adding or changing admin/player access rules, update both layers of authorization: `SecurityConfig` request matchers and any service-level centre-scope checks. Route-level role checks alone are not enough in this codebase.
- Do not reintroduce import-time player auto-creation or hardcoded import passwords. The current live import flow requires explicit mapping to existing `PLAYER` accounts before commit.
- `SecurityConfig` is also where H2 console exposure is gated through `APP_H2_CONSOLE_ENABLED`. Changes there affect local verification and frontend integration immediately.
- All passwords should flow through `PasswordEncoder`/BCrypt. Seeded credentials are for local development only.
- If you change import, auth, leaderboard, or progress behavior, rerun the integration suites plus the focused service tests before considering the change complete.
