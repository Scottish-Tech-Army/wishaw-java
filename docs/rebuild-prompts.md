# Wishaw YMCA Rebuild Prompts

This document consolidates the requirements and direction used to build the Wishaw YMCA eSports Badge Portal so the UI and backend can be regenerated accurately.

It now also includes reusable prompt templates for building other product-ready enterprise applications from business requirement documents, not just this specific platform.

## Start Here

Yes, this file can now be used as a reusable input to an agent for planning and building UI, backend, and database layers.

To use it well, do not send it as a standalone generic instruction file with no product inputs. Pair it with the actual business requirement documents and fill in the placeholders in the reusable template sections.

Use the Wishaw-specific prompts later in this document as worked examples. Use the reusable sections near the top for future app creation.

## Step-By-Step Usage Sequence

Follow this order so the agent understands the requirements first and only then builds the apps.

### Step 1: Collect The Right Inputs

Before running the agent, prepare these inputs:

- business requirement document or BRD
- user stories or backlog
- non-functional requirements
- wireframes, mockups, or design references
- integration constraints and API rules
- security, compliance, hosting, and deployment constraints
- preferred frontend, backend, and database technologies if they are already chosen

### Step 2: Fill In The Reusable App Template

Use the section `Fill-In Template For Reusable App Creation` first.

This gives the agent a structured request covering:

- product name and domain
- users and business goals
- required features
- roles and permissions
- integrations
- operational constraints
- database expectations
- delivery sequence

### Step 3: Make The Agent Understand The Requirements

Before asking the agent to build anything, use the section `Reusable Prompt Template: BRD Analysis And Delivery Plan`.

This step should force the agent to:

- read the requirement documents carefully
- summarize the business problem and users
- identify workflows, business rules, modules, and non-functional requirements
- list assumptions, gaps, and risks
- propose the architecture, route map, API approach, and database direction

This is the most important step. If the agent does not understand the requirements first, the generated apps will be generic and unreliable.

### Step 4: Review And Freeze The Plan

Before build execution, review the planning output and confirm or adjust:

- product scope
- modules and workflows
- user roles and permissions
- UI route map and screens
- backend module boundaries
- API contract approach
- database model and migration approach
- deployment targets

### Step 5: Build The Full Application

After the requirements and plan are clear, use the section `Master Agent Prompt For Product Creation`.

This is the main build prompt for creating:

- frontend application
- backend application
- database schema, migrations, indexes, and seed setup
- environment configuration
- tests, documentation, Docker assets, and Kubernetes assets when required

If you want an alternative full-stack build prompt, use `Reusable Prompt Template: Enterprise Full-Stack App Builder`.

### Step 6: Build By Layer If Needed

If you do not want the full stack built in one pass, use the focused templates instead:

- `Reusable Prompt Template: UI Prompt From Business Requirements`
- `Reusable Prompt Template: Backend Prompt From Business Requirements`
- `Reusable Prompt Template: Database Prompt From Business Requirements`

### Step 7: Validate And Harden The Build

After generation, require the agent to validate:

- critical end-to-end business flows
- login and role-based access
- major CRUD and reporting flows
- database migrations and seed data
- build and run commands
- API documentation and health endpoints
- Docker and Kubernetes readiness if applicable

### Step 8: Use The Wishaw Sections As Examples

The Wishaw-specific prompts later in this document show what a domain-specific frontend and backend build prompt looks like after the requirements are known.

## Quick Start Summary

For easiest reuse, use this simple sequence:

1. Fill the reusable app template.
2. Run the BRD analysis prompt.
3. Review and confirm the resulting plan.
4. Run the master build prompt.
5. Validate the generated UI, backend, and database outputs.

## Master Agent Prompt For Product Creation

Use this after the requirement-understanding step when you want an agent to create a full application from requirement documents, including frontend, backend, and database.

```text
You are building a production-ready enterprise application from the supplied business requirement documents and supporting inputs.

Treat the requirement documents as the primary source of truth.

Your responsibilities include requirement analysis, planning, frontend generation, backend generation, database design, validation, documentation, and deployment readiness.

Inputs:
- Product name: [PRODUCT_NAME]
- Domain: [DOMAIN]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Supporting documents: [PASTE_OR_ATTACH_SUPPORTING_DOCS]
- Preferred frontend stack: [FRONTEND_STACK_OR_LEAVE_OPEN]
- Preferred backend stack: [BACKEND_STACK_OR_LEAVE_OPEN]
- Preferred database: [POSTGRES_MYSQL_SQLSERVER_MONGODB_OR_LEAVE_OPEN]
- Deployment targets: [LOCAL_DOCKER_KUBERNETES_CLOUD_STATIC_HOSTING]
- Integrations: [SSO_EMAIL_PAYMENT_STORAGE_ETL_WEBHOOKS_REPORTING_ETC]
- Constraints: [SECURITY_COMPLIANCE_BUDGET_TIMELINE_TEAM_STANDARDS]

Execution rules:
1. Read and understand the requirement documents before proposing code.
2. Produce a structured plan before implementation.
3. Keep frontend, backend, and database contracts aligned.
4. If requirements are missing or ambiguous, identify them explicitly and state assumptions.
5. Build for maintainability, product quality, and enterprise readiness rather than quick scaffolding.

Stage 1: Requirement analysis
- Extract business goals, users, roles, workflows, modules, business rules, reporting needs, integrations, and non-functional requirements.
- Identify assumptions, risks, ambiguities, and missing inputs.

Stage 2: Architecture and planning
- Propose the target architecture for frontend, backend, database, and deployment.
- Define UI route map, screen inventory, backend modules, service boundaries, API style, auth model, and operational model.
- Produce phased implementation milestones.

Stage 3: Frontend delivery
- Build a production-grade frontend with reusable components, accessible UX, responsive layouts, clear information hierarchy, route guards, state management, typed API integration, loading states, empty states, validation errors, and failure handling.

Stage 4: Backend delivery
- Build a production-grade backend with layered architecture, validation, business services, DTO-based APIs, authentication, authorization, consistent error responses, logging, observability, and testable business logic.

Stage 5: Database delivery
- Design the logical and physical data model from the requirement documents.
- Choose an appropriate database technology and justify the choice when not fixed.
- Create schema definitions or migrations.
- Define tables or collections, keys, indexes, constraints, relationships, and audit fields.
- Add seed or reference data required for local development, demos, and validation.
- Account for transactional integrity, performance, retention, and future reporting needs.

Stage 6: Enterprise concerns
- Add environment-aware configuration.
- Add authentication and authorization.
- Define and implement a caching strategy, or explicitly document why caching is unnecessary for each major module.
- Add resilience measures appropriate to the architecture, such as health checks, retry strategy where justified, graceful degradation, idempotency where needed, operational diagnostics, and stateless scalability where appropriate.
- Add Docker packaging and Kubernetes manifests when required by the deployment target.

Stage 7: Validation and handoff
- Validate critical end-to-end journeys.
- Verify build, test, and run commands.
- Verify auth flows, role-based access, critical CRUD flows, reporting flows, and health endpoints.
- Produce setup, deployment, and troubleshooting documentation.

Required outputs:
- requirement summary
- assumptions and risks
- architecture summary
- phased implementation plan
- UI route map and screen list
- backend module breakdown
- API summary
- database model and migration approach
- deployment approach
- validation checklist
- known tradeoffs

Important constraints:
- Do not produce generic code that ignores the BRD.
- Do not skip the database design phase.
- Do not build frontend and backend independently without a shared contract.
- Prefer production-quality structure over minimal demo scaffolding.
```

## Fill-In Template For Reusable App Creation

Use this section first as a reusable one-file input template for future apps. Replace the placeholders and provide your business documents with it.

```text
Application creation request

Product name: [PRODUCT_NAME]
Business domain: [DOMAIN]
Primary users: [USER_TYPES]
Business goal: [BUSINESS_GOAL]

Business requirement documents:
- [ATTACH_OR_PASTE_BRD]
- [ATTACH_OR_PASTE_USER_STORIES]
- [ATTACH_OR_PASTE_NFRS]

Preferred technology direction:
- Frontend: [STACK]
- Backend: [STACK]
- Database: [DATABASE]
- Hosting or deployment: [LOCAL_DOCKER_K8S_CLOUD]

Mandatory features:
- [FEATURE_1]
- [FEATURE_2]
- [FEATURE_3]

User roles:
- [ROLE_1]
- [ROLE_2]
- [ROLE_3]

Integrations:
- [INTEGRATION_1]
- [INTEGRATION_2]

Security and compliance constraints:
- [CONSTRAINT_1]
- [CONSTRAINT_2]

Operational requirements:
- [OBSERVABILITY_REQUIREMENTS]
- [AVAILABILITY_REQUIREMENTS]
- [PERFORMANCE_REQUIREMENTS]
- [SCALING_REQUIREMENTS]

Database requirements:
- Design the schema from the business rules.
- Create migrations or initialization scripts.
- Add indexes, constraints, and audit fields.
- Add seed data for local and demo use.
- Support reporting and operational queries.

Expected delivery sequence:
1. summarize the requirements
2. identify assumptions and open questions
3. propose architecture
4. define routes, APIs, modules, and data model
5. build frontend
6. build backend
7. build database schema and seed setup
8. add tests, docs, Docker, and Kubernetes artifacts if applicable
9. validate end-to-end behavior

Expected final deliverables:
- frontend app
- backend app
- database schema or migrations
- seed data
- API documentation
- environment configuration
- Docker assets
- Kubernetes assets if required
- validation notes
```

## Reusable BRD-To-Build Workflow

Use this workflow when you want an AI builder to read requirement documents first, extract the real scope, produce a plan, and then build the UI and backend end to end.

### Inputs To Provide

Before using the reusable prompts later in this document, provide as many of these assets as you have:

- business requirement document or BRD
- functional requirement specification
- user stories or backlog
- wireframes, mockups, or design references
- API standards or integration constraints
- security, compliance, and hosting constraints
- non-functional requirements for performance, scalability, availability, auditability, and observability
- data model notes, spreadsheet samples, or migration inputs
- deployment targets such as local, cloud VM, Docker, Kubernetes, static hosting, or app service

### Recommended Delivery Sequence

For best results, ask the AI to work in this order:

1. Understand the requirement documents.
2. Extract functional and non-functional requirements.
3. Identify assumptions, ambiguities, and risks.
4. Produce a build plan and target architecture.
5. Define domain model, roles, workflows, APIs, and UI route map.
6. Build frontend, backend, and database to the agreed contract.
7. Add tests, docs, environment config, migrations, and deployment assets.
8. Validate core business journeys end to end.

### Standard Output Expectations

When using reusable prompts, require the AI to produce these outputs before or during implementation:

- requirements summary grouped by module
- functional scope and exclusions
- user roles and permissions matrix
- UI sitemap and major screens
- backend domain model and API contract
- database model, schema, constraints, and migration approach
- integration points and external dependencies
- enterprise concerns such as auth, authorization, caching, resilience, audit, observability, and deployment
- phased implementation plan
- acceptance criteria and validation checklist

## Reusable Prompt Template: BRD Analysis And Delivery Plan

Use this prompt first when you want the AI to understand your documents and prepare a strong implementation plan before writing code.

```text
You are building a production-ready enterprise web application from provided business requirement documents.

Your first responsibility is to understand the documents before proposing or generating code.

Inputs:
- Product name: [PRODUCT_NAME]
- Domain: [DOMAIN]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Additional specs: [PASTE_OR_ATTACH_SUPPORTING_DOCS]
- Preferred frontend stack: [FRONTEND_STACK_OR_LEAVE_OPEN]
- Preferred backend stack: [BACKEND_STACK_OR_LEAVE_OPEN]
- Deployment target: [LOCAL_AND_TARGET_ENVIRONMENTS]
- Constraints: [SECURITY_COMPLIANCE_TIMELINE_TEAM_INTEGRATION_CONSTRAINTS]

Your tasks:
1. Read the requirement documents carefully and extract the real business problem, target users, workflows, business rules, and non-functional requirements.
2. Produce a structured requirements breakdown covering:
  - product vision and business outcome
  - user personas and roles
  - functional modules
  - critical workflows
  - data entities and relationships
  - integrations and external systems
  - reporting and analytics needs
  - security and compliance expectations
  - performance, scale, availability, audit, and operational requirements
3. Explicitly list ambiguities, missing details, risky assumptions, and questions that should be clarified.
4. Produce an implementation plan with phases, milestones, and dependencies.
5. Propose a high-confidence target architecture for both frontend and backend.
6. Define:
  - UI route map and screen inventory
  - backend service boundaries and major modules
  - API design approach
  - authentication and authorization model
  - data storage and database design approach
  - caching strategy
  - resilience strategy
  - observability strategy
  - deployment strategy using Docker and Kubernetes if appropriate
7. Produce acceptance criteria for MVP and production readiness.

Expected output format:
- Executive summary
- Requirement breakdown
- Clarifications and assumptions
- Recommended architecture
- Delivery plan
- Acceptance criteria

Important instructions:
- Do not jump straight into coding.
- Ground every proposal in the supplied requirement documents.
- When the documents are incomplete, identify gaps clearly instead of inventing hidden requirements.
- Optimize for an enterprise-ready, maintainable, product-quality solution.
```

## Reusable Prompt Template: Enterprise Full-Stack App Builder

Use this prompt after the planning step, or use it together with the planning template if you want a single strong reusable prompt.

```text
Build a production-ready enterprise web application end to end using the provided business requirement documents.

Inputs:
- Product name: [PRODUCT_NAME]
- Domain: [DOMAIN]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Supporting docs: [PASTE_OR_ATTACH_SUPPORTING_DOCS]
- Frontend stack: [E.G. REACT_TYPESCRIPT_VITE_TAILWIND]
- Backend stack: [E.G. JAVA17_SPRING_BOOT_POSTGRES]
- Deployment target: [LOCAL_DOCKER_KUBERNETES_CLOUD]
- Integration constraints: [AUTH_SSO_PAYMENT_EMAIL_FILES_ETL_REPORTING]
- Quality constraints: [PERFORMANCE_SECURITY_AUDITABILITY_SLA]

Execution mode:
- First understand the requirement documents.
- Then plan the solution.
- Then build the application end to end.
- Keep the frontend and backend contracts aligned throughout.

Mandatory delivery stages:

Stage 1: Requirement understanding
- Read all requirement documents and summarize the product goals, users, workflows, entities, business rules, and non-functional requirements.
- Identify missing details, assumptions, and risks.

Stage 2: Solution planning
- Produce the target architecture.
- Define the module boundaries.
- Define the API contract.
- Define the UI route map and screen inventory.
- Define the data model and persistence approach.
- Define the deployment model.

Stage 3: Frontend build
- Build a production-grade frontend with reusable components, clean state management, route guards, typed API integration, loading/error states, responsive layouts, and accessible UX.
- Ensure the UI supports the business workflows described in the requirement documents.

Stage 4: Backend build
- Build a production-grade backend with layered architecture, DTO-based APIs, validation, persistence, authentication, authorization, business-rule enforcement, structured errors, logging, and observability.
- Ensure backend APIs match the frontend contract exactly.

Stage 5: Database build
- Design the schema from the requirement documents.
- Define tables or collections, relationships, indexes, constraints, audit fields, and reporting-friendly structures.
- Create migrations or initialization scripts.
- Add seed and reference data for local development and demos.

Stage 6: Enterprise readiness
- Add environment-aware configuration.
- Add authentication and authorization.
- Add an explicit caching strategy or document why caching is not used for each major module.
- Add resilience measures appropriate for the architecture, such as retries where justified, health checks, graceful error handling, stateless scaling, idempotency where needed, and operational diagnostics.
- Add Docker packaging and Kubernetes manifests if the deployment target requires them.
- Add API documentation and runbook-style setup instructions.

Stage 7: Validation
- Validate critical flows end to end.
- Verify build and run commands succeed.
- Verify login, role-based access, key CRUD workflows, reporting flows, and operational health endpoints.

Frontend requirements to always consider:
- clear information architecture
- mobile responsiveness
- accessibility
- meaningful loading and error states
- secure session handling
- maintainable component structure
- environment-based API configuration

Backend requirements to always consider:
- clear layered architecture
- validation and consistent error responses
- role-based access control
- transactional integrity
- testable business services
- observability
- deployment-readiness

Required output:
- requirement summary
- architecture summary
- implementation plan
- frontend deliverables
- backend deliverables
- database deliverables
- deployment deliverables
- validation checklist
- known assumptions and tradeoffs

Important instructions:
- Do not treat the BRD as optional context; it is the primary source of truth.
- Do not generate generic scaffolding that ignores the domain.
- Build only after mapping the requirements to modules, workflows, APIs, and data.
- Keep the implementation realistic, maintainable, and product-ready.
```

## Reusable Prompt Template: UI Prompt From Business Requirements

Use this when you want only the frontend to be generated from requirement documents.

```text
Build a production-ready frontend from the provided business requirement documents.

Inputs:
- Product name: [PRODUCT_NAME]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Supporting design docs: [PASTE_OR_ATTACH_MOCKUPS_OR_REFERENCES]
- Frontend stack: [STACK]
- Auth model: [JWT_SESSION_SSO_ETC]
- API contract input: [OPENAPI_BACKEND_DOCS_OR_ENDPOINT_SUMMARY]

Your tasks:
1. Understand the requirement documents and extract user roles, user journeys, pages, states, validation rules, and admin vs user capabilities.
2. Produce a route map and page inventory before implementation.
3. Build a responsive, accessible, production-quality UI with reusable components and a clean API integration layer.
4. Support loading, empty, success, validation-error, and failure states across all critical flows.
5. Ensure environment-aware configuration for local, test, and production.
6. Add route guards and session handling aligned with the backend auth model.
7. Document assumptions where the requirement docs are incomplete.

Required deliverables:
- route map
- component architecture
- screen implementations
- typed API layer
- state management setup
- environment config
- build and run instructions
```

## Reusable Prompt Template: Backend Prompt From Business Requirements

Use this when you want only the backend to be generated from requirement documents.

```text
Build a production-ready backend from the provided business requirement documents.

Inputs:
- Product name: [PRODUCT_NAME]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Backend stack: [STACK]
- Data storage target: [POSTGRES_MYSQL_MONGODB_ETC]
- Deployment target: [LOCAL_DOCKER_KUBERNETES_CLOUD]
- Security requirements: [JWT_RBAC_SSO_AUDIT_ENCRYPTION]
- Integration constraints: [THIRD_PARTY_SYSTEMS_EVENTS_FILES_WEBHOOKS]

Your tasks:
1. Read the requirement documents first and extract entities, workflows, business rules, permissions, integrations, and reporting needs.
2. Design the domain model, API contract, validation rules, and service boundaries before implementation.
3. Build a layered, maintainable backend with controllers, services, repositories, DTOs, validation, centralized exception handling, logging, and observability.
4. Implement authentication and authorization aligned to the user roles described in the documents.
5. Add a suitable caching and resilience strategy, or explicitly justify not using one where unnecessary.
6. Add Docker and Kubernetes deployment support when the deployment target calls for it.
7. Add tests for high-risk business rules and critical security paths.

Required deliverables:
- domain model
- API design
- business rules implementation
- auth and authorization model
- persistence configuration
- observability and ops setup
- deployment assets
- setup and validation documentation
```

## Reusable Prompt Template: Database Prompt From Business Requirements

Use this when you want only the database layer to be designed and created from requirement documents.

```text
Build a production-ready database design from the provided business requirement documents.

Inputs:
- Product name: [PRODUCT_NAME]
- Business requirement documents: [PASTE_OR_ATTACH_BRD_CONTENT]
- Preferred database: [POSTGRES_MYSQL_SQLSERVER_MONGODB_ETC]
- Expected scale and reporting needs: [TRANSACTION_VOLUME_ANALYTICS_RETENTION]
- Deployment target: [LOCAL_DOCKER_KUBERNETES_CLOUD]
- Data governance constraints: [AUDIT_RETENTION_PII_ENCRYPTION_COMPLIANCE]

Your tasks:
1. Read the requirement documents first and extract entities, relationships, business rules, reporting needs, retention needs, and integrity constraints.
2. Design the logical and physical data model.
3. Choose keys, indexes, uniqueness constraints, foreign keys, audit fields, and lifecycle fields.
4. Create schema definitions, migrations, or initialization scripts.
5. Add seed and reference data needed for local development, demos, and validation.
6. Document assumptions, scaling considerations, and tradeoffs.
7. Ensure the database design supports the backend workflows and reporting requirements.

Required deliverables:
- entity and relationship summary
- schema or collection design
- migrations or initialization scripts
- constraints and indexing strategy
- seed data strategy
- operational and reporting considerations
- setup documentation
```

## Shared Product Context

Use this context before either prompt:

- Project name: Wishaw YMCA eSports Badge Portal.
- Problem: replace a WordPress plus spreadsheet workflow used to manage youth eSports badge progression, tournaments, modules, notifications, and admin reporting.
- Audience: young players, parents, volunteers, youth workers, centre managers, and YMCA administrators.
- Core outcome: deliver a scalable, mobile-friendly web application that reduces admin effort, improves parent and player visibility, and is suitable for future SaaS licensing to other YMCA organisations.
- Functional domains:
  - authentication and registration
  - player profile and progress
  - badge framework with 5 main badges and multiple sub-badges
  - learning modules and schedules
  - sports catalogue and tournament management
  - match scheduling, scoring, and attendance
  - player and tournament leaderboards
  - notifications and announcements
  - admin dashboard and configuration tools
  - centres and groups catalogue
  - configurable age restrictions by game/sport and player DOB validation
- Demo users to seed in local/dev:
  - admin@wymca.org / admin123
  - player1@wymca.org / player123
  - player2@wymca.org / player123
- Local URLs:
  - frontend: http://127.0.0.1:3000
  - backend: http://localhost:8080
  - swagger: http://localhost:8080/swagger-ui.html
  - openapi: http://localhost:8080/v3/api-docs
  - h2 console: http://localhost:8080/h2-console

## Prompt 1: Frontend Rebuild Prompt

Use this as a copy-paste prompt for rebuilding the UI from scratch after reviewing the relevant business requirement documents:

```text
Build a production-quality frontend for the Wishaw YMCA eSports Badge Portal using React 19, TypeScript, Vite, Tailwind CSS, Zustand, React Router, Recharts, React Hot Toast, and a clean reusable component layer.

Before implementation, read the provided business requirement documents, summarize the user journeys, route map, screen inventory, assumptions, and API dependencies, then build the UI to that plan.

Project context:
- This app replaces a manual WordPress plus spreadsheet workflow used by Wishaw YMCA to manage youth eSports progression.
- It must serve young players, parents, and non-technical admins.
- The system tracks modules, badges, XP, tournaments, matches, attendance, leaderboards, notifications, centres, and groups.
- It should be suitable for future packaging as a SaaS platform for other YMCA organisations.

Design and UX goals:
- Mobile-friendly, polished, modern, and high-confidence enterprise UX.
- Accessible layouts with clear information hierarchy.
- Responsive across laptop, tablet, and phone.
- Keep the visual language intentional and distinctive, not generic scaffold styling.
- Use a strong dashboard experience for both players and admins.

Frontend architecture requirements:
- Use React 19 + TypeScript + Vite.
- Use Zustand for auth and notification state.
- Use React Router for route-based navigation.
- Build a unified API layer with support for both mock mode and live backend mode.
- Add environment-aware config so local development can use a same-origin `/api` base path and Vite proxying to the backend.
- Add route guards for authenticated users and admin-only areas.
- Keep auth tokens in browser storage and support refresh token flow.

Functional pages and features:
- Public auth:
  - login page
  - registration page
- Player area:
  - home dashboard
  - badges page
  - modules list and module detail
  - global leaderboard
  - tournaments list and tournament detail
  - match detail
  - my tournaments
  - player stats
  - profile page
  - notifications page
- Admin area:
  - admin dashboard
  - sports management
  - tournament list, create, edit, detail, publish, complete, cancel
  - badges management
  - centres and groups view
  - modules management view
  - analytics view

Data and contract expectations:
- Align all API calls to a Spring Boot backend that supports both `/api` and `/api/v1` route prefixes.
- Use these endpoint families:
  - `/auth/*`
  - `/profile`
  - `/sports`
  - `/tournaments`
  - `/matches`
  - `/leaderboard`
  - `/notifications`
  - `/teams`
  - `/modules`
  - `/badges/*`
  - `/centres`
  - `/groups`
  - `/stats/*`
- Registration and profile management must capture `dateOfBirth`.
- Sports management must allow admins to configure optional `minAge` and `maxAge` restrictions.
- Tournament and sport views should surface age restrictions clearly where relevant.

Mock/live behavior:
- Provide a realistic mock API with seeded data for all major flows.
- Allow switching between mocked frontend-only behavior and the live backend using env variables.
- In local dev, force the frontend to call `/api` and proxy to `http://localhost:8080`.

Developer experience requirements:
- Ensure `npm install`, `npm run dev`, and `npm run build` succeed.
- Add `.env.development`, `.env.test`, `.env.production`, and `.env.local` patterns.
- Use a strict local dev port of `127.0.0.1:3000`.

Deliverables:
- full routed React application
- typed API client and types
- state stores for auth and notifications
- mock API and mock data
- protected routes
- responsive admin and player pages
- local dev proxy configuration
- age-aware registration, profile, and sport configuration UI

Acceptance criteria:
- Demo users can log in successfully.
- New players can register successfully and are logged in after registration.
- Profile updates persist against the backend.
- Admins can configure sport age limits.
- Player join attempts fail with a meaningful error when outside an allowed age range.
- All core pages load against the live backend without route mismatches.
```

## Prompt 2: Backend Rebuild Prompt

Use this as a copy-paste prompt for rebuilding the backend from scratch after reviewing the relevant business requirement documents:

```text
Build an enterprise-ready backend for the Wishaw YMCA eSports Badge Portal using Java 17, Spring Boot 3.4.x, Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation, Actuator, H2, PostgreSQL, JJWT, and springdoc OpenAPI.

Before implementation, read the provided business requirement documents, summarize the domain model, workflows, business rules, APIs, assumptions, and operational requirements, then build the backend to that plan.

Project context:
- This backend supports a React frontend for youth eSports progression, tournaments, badges, modules, notifications, and admin management.
- The platform is intended to replace a manual WordPress plus spreadsheet process used by Wishaw YMCA.
- The end state should be robust enough to evolve into a multi-tenant SaaS-style YMCA platform.

Architecture requirements:
- Use a layered architecture with controller, service, repository, DTO, entity, mapper, config, security, and exception packages.
- Expose both `/api/*` and `/api/v1/*` routes for frontend compatibility.
- Make the API DTO-driven and keep the frontend contract stable.
- Use centralized exception handling with a consistent JSON error envelope.
- Add request correlation id support for observability.

Security requirements:
- Implement JWT access token and refresh token auth.
- Support login, register, refresh, logout, and current-session endpoints.
- Enforce role-based authorization for admin-only functions.
- Keep the API stateless.
- Allow Swagger, health endpoints, and H2 console locally.

Persistence requirements:
- Support profiles:
  - `h2` for local development with in-memory seeded data
  - `postgres` for local persistent use
  - `k8s` for container/Kubernetes deployment
- Use JPA entities for:
  - users and profiles
  - centres and groups
  - badges and earned sub-badges
  - modules and session schedule items
  - sports with optional configurable `minAge` and `maxAge`
  - tournaments, participants, matches, teams, attendance, scores
  - notifications and announcements
  - calories logs if needed by leaderboard features
- Seed demo users and realistic sample data for badges, modules, sports, tournaments, notifications, and leaderboard demos.
- Ensure local seeding always upserts the demo users and profiles on startup.

Functional endpoints to implement:
- Auth:
  - POST `/api/auth/login`
  - POST `/api/auth/register`
  - POST `/api/auth/refresh`
  - POST `/api/auth/logout`
  - GET `/api/auth/me`
- Profile:
  - GET/PUT `/api/profile`
  - POST `/api/profile/photo`
  - POST `/api/profile/photo/overlay`
- Sports:
  - GET/POST `/api/sports`
  - PUT/DELETE `/api/sports/{id}`
- Tournaments:
  - GET/POST `/api/tournaments`
  - GET/PUT `/api/tournaments/{id}`
  - POST `/api/tournaments/{id}/publish`
  - POST `/api/tournaments/{id}/cancel`
  - POST `/api/tournaments/{id}/complete`
  - POST `/api/tournaments/{id}/join`
  - DELETE `/api/tournaments/{id}/leave`
  - GET `/api/tournaments/{id}/participants`
- Matches:
  - GET `/api/matches/tournament/{id}`
  - GET/POST `/api/matches`
  - GET/PUT `/api/matches/{id}`
  - POST `/api/matches/{id}/score`
  - GET `/api/matches/{id}/score`
  - GET `/api/matches/{id}/score/audit`
  - POST `/api/matches/{id}/attendance`
- Leaderboard:
  - GET `/api/leaderboard/global`
  - GET `/api/leaderboard/tournament/{id}`
  - GET/POST `/api/leaderboard/badges`
  - POST `/api/leaderboard/badges/assign`
  - GET `/api/leaderboard/badges/user/{userId}`
  - POST `/api/leaderboard/calories`
  - GET `/api/leaderboard/calories/user/{userId}`
- Notifications:
  - GET `/api/notifications`
  - PUT `/api/notifications/{id}/read`
  - PUT `/api/notifications/read-all`
  - POST `/api/notifications/announcements`
  - GET `/api/notifications/announcements/tournament/{id}`
  - GET `/api/notifications/share/{type}/{id}`
  - GET `/api/notifications/gallery/tournament/{id}`
- Catalogue:
  - GET `/api/centres`
  - GET `/api/groups`
  - GET `/api/modules`
  - GET `/api/modules/{id}`
  - GET `/api/badges/main`
  - GET `/api/badges/sub`
  - GET `/api/badges/progress/{userId}`
  - POST `/api/badges/award`
- Stats:
  - GET `/api/stats/player/{userId}`
  - GET `/api/stats/admin/dashboard`
- Teams:
  - GET `/api/teams/tournament/{id}`
  - POST `/api/teams`

Important business rules:
- Registration must create a player user and a linked player profile in one transaction.
- Registration and profile update must support `dateOfBirth` in `YYYY-MM-DD` format.
- Sports can have nullable `minAge` and `maxAge` restrictions.
- Validate that minAge and maxAge are not negative and minAge is not greater than maxAge.
- Tournament join must block users whose profile DOB makes them ineligible for the tournament sport age policy.
- When no age restriction exists, joining should behave normally.

Observability and enterprise requirements:
- Actuator health/info endpoints.
- Swagger UI and OpenAPI JSON.
- Correlation id filter.
- Global exception handler.
- Jacoco-ready test coverage configuration.
- Kubernetes manifests for configmap, deployment, service, ingress, HPA, and example secret.

Seed and demo requirements:
- Seed these users in local H2 mode:
  - admin@wymca.org / admin123
  - player1@wymca.org / player123
  - player2@wymca.org / player123
- Seed sample centres, groups, modules, badges, sports, tournaments, matches, notifications, stats-friendly data, and DOB values.

Documentation requirements:
- Add a local DB access document.
- Add curl or Bruno-friendly API examples.
- Ensure Swagger documents the API clearly.

Testing requirements:
- Add focused unit/integration tests for auth, JWT, badge rules, and tournament age restriction enforcement.
- `mvn clean install` or `mvn clean package` should succeed.

Acceptance criteria:
- Frontend routes can call backend endpoints without path mismatches.
- Demo credentials work locally.
- Registration works for a new player without session identity conflicts.
- Age-restricted join attempts fail with a clear message.
- Swagger UI works at `/swagger-ui.html`.
- H2 console works locally at `/h2-console`.
```

## Optional Full-Stack Prompt

If you want one single regeneration prompt for the whole platform, use both prompts above together and add this instruction:

```text
Build the frontend and backend together as one integrated platform. Start from the shared product context and the provided business requirement documents. First produce a requirement summary, assumptions list, architecture, route map, domain model, API contract, and phased implementation plan. Then generate the React frontend, generate the Spring Boot backend to match the frontend API contract exactly, and verify the system locally with seeded demo users, Swagger, H2 console, local Vite proxying, and end-to-end login, registration, profile update, tournament browsing, and admin sport age-limit configuration.
```