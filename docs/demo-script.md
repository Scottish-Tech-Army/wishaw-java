# Wishaw YMCA Enterprise Demo Script

## Purpose

Use this script to demo the Wishaw YMCA eSports Badge Portal as a polished enterprise application, not just a hackathon prototype.

The goal is to show:

- real player and admin journeys
- clear operational value for Wishaw YMCA
- built-in extensibility and configuration
- enterprise non-functional qualities
- simple deployment and environment flexibility

## Demo Setup Checklist

Before the session, have these running and ready:

- frontend at http://127.0.0.1:3000
- backend at http://localhost:8080
- swagger at http://localhost:8080/swagger-ui.html
- h2 console at http://localhost:8080/h2-console

Use these credentials:

- Player demo: `player1@wymca.org / player123`
- Admin demo: `admin@wymca.org / admin123`

Keep these tabs open:

- player UI
- admin UI
- Swagger UI
- H2 console or a DB query window
- optional: Kubernetes manifest folder or repo tree for deployment discussion

## 30-Second Opening

"What you are about to see is a full-stack eSports badge and tournament platform built for Wishaw YMCA. It replaces a manual WordPress plus spreadsheet workflow with a role-based, API-driven platform that supports player progression, admin operations, configurable game rules, and enterprise-ready deployment patterns."

## Demo Storyline

## Scene 1: Start With The Problem

Say:

"Wishaw YMCA was tracking progression through spreadsheets and manually reflecting that into a WordPress site. That is difficult to scale, difficult to maintain, and hard for young people and parents to navigate. This platform turns that process into a structured digital product."

Show:

- the challenge document summary
- then immediately switch to the live app

## Scene 2: Player Registration And Safe Onboarding

Open the registration page.

Say:

"The onboarding flow is self-service, but still governed. We capture the core player identity data, including date of birth, because age restrictions can be enforced at the game or tournament level without manual checking by staff."

Show:

- display name, first name, last name, DOB, email, password
- note that DOB is not cosmetic; it feeds policy enforcement

Wow point:

- mention that registration returns a live authenticated session and immediately places the new player inside the platform

## Scene 3: Player Experience

Log in as the player user.

Say:

"From the player perspective, this is not a data-entry tool. It is a progression platform. The experience is centered on motivation, visibility, and next actions."

Walk through:

1. Home dashboard
   - show badge progress snapshot
   - show active tournaments
   - show navigation into modules, leaderboard, tournaments
2. Badges
   - show the 5 main badges
   - explain sub-badge accumulation and XP visibility
3. Modules
   - show learning modules and their session structure
   - connect this back to youth development, not just gameplay
4. Leaderboard
   - show centre-level and player-level comparison
5. Tournaments
   - open a tournament detail page
   - show participants, schedule, leaderboard, and join/leave behavior
6. Stats
   - show player performance summary
7. Notifications
   - show read/unread workflow and platform communication
8. Profile
   - show editable personal profile and DOB persistence

Enterprise message:

"This gives young people and parents a live digital record of progress instead of asking staff to manually recreate progress reports from spreadsheets."

## Scene 4: Age Governance In Action

This is one of the strongest live demo moments.

Say:

"The platform does not just display content. It enforces operational policy. Here, game eligibility can be configured by sport, and the backend blocks ineligible joins automatically."

Show:

1. Log in as admin.
2. Open Sports Management.
3. Edit a sport and set `minAge` or `minAge/maxAge`.
4. Return to the player journey and attempt to join the related tournament with an ineligible player.
5. Show the user-friendly validation message.

What to say while doing it:

"This is configurable, not hardcoded. Staff do not need a developer to change age rules. And enforcement happens on the server, so the rule still holds even if the UI changes."

## Scene 5: Admin Operations

Open the admin dashboard.

Say:

"This is where the solution stops being a nice frontend and becomes an operational system. Non-technical admins can configure the platform through workflows instead of spreadsheets and database access."

Walk through:

1. Admin dashboard
   - total tournaments
   - active tournaments
   - player counts
   - match counts
   - top performers
2. Sports management
   - create and edit sports
   - configure age limits
3. Tournament management
   - create tournament
   - edit it
   - publish it
   - complete or cancel it
4. Badges management
   - create badge definitions used by progression flows
5. Centres and groups
   - show organisational structure
6. Modules and analytics views
   - show extendable admin surface for programme operations

Value statement:

"The admin surface is designed to reduce dependency on one expert staff member. That is critical for a charity with tight staffing and budget constraints."

## Scene 6: API And Integration Story

Open Swagger UI.

Say:

"Everything in the UI runs on a documented API. That gives Wishaw YMCA a platform, not a closed demo. It means the system is easier to extend, easier to integrate, and easier to support."

Show:

- auth endpoints
- profile endpoints
- sports and tournaments endpoints
- notifications and stats endpoints
- note support for both `/api` and `/api/v1`

Optional line:

"The UI and backend were validated end to end against these routes, including login, registration, tournaments, profile updates, notifications, and admin dashboards."

## Scene 7: Database Visibility And Supportability

Open H2 console or Postgres notes.

Say:

"The data model is transparent and inspectable. For local development we use H2 with seeded demo data. For persistent environments we can use PostgreSQL."

Show:

- `USER_ACCOUNTS`
- `USER_PROFILES`
- `SPORTS`
- `TOURNAMENTS`
- `TOURNAMENT_PARTICIPANTS`
- age-related columns like `DATE_OF_BIRTH`, `MIN_AGE`, `MAX_AGE`

Value statement:

"This is important operationally because support teams, developers, and future platform operators can inspect the data directly without relying on brittle manual exports."

## Scene 8: Enterprise Non-Functional Requirements

Pause on architecture or docs and explicitly call these out.

Say:

"A lot of the value here is in the non-functional foundation. This platform is built to be maintainable, operable, and extensible."

Highlight these NFRs:

- Role-based access control for player and admin capabilities
- JWT access and refresh token security model
- Stateless backend suitable for horizontal scaling
- Consistent JSON error contract
- Correlation-id logging support for traceability
- Health endpoints for monitoring and orchestration
- OpenAPI and Swagger for discoverability and support
- H2 for local speed, PostgreSQL for persistence, Kubernetes profile for deployment
- Environment-based configuration for frontend and backend
- Seeded demo data for fast onboarding and demos
- Integration and unit test coverage for critical flows
- Configurable sport age policies instead of hardcoded restrictions
- Local same-origin API proxying in the frontend to prevent cross-origin instability in development

## Scene 9: Plug-And-Play Configuration Story

Say:

"This system is intentionally configurable. It is not a one-off hardcoded site."

Show or mention:

- environment-based frontend API switching
- local, development, test, and production frontend env files
- backend profiles: `h2`, `postgres`, `k8s`
- seed-data toggle
- CORS origin configuration
- JWT expiry and secret configuration
- Kubernetes config map, secret template, deployment, service, ingress, and HPA manifests
- support for both relative and absolute backend route strategies via the frontend config

## Scene 10: Easy Deployment Story

Say:

"This is designed to run simply in development, but it also has a clear path to operational deployment."

### Local frontend

```powershell
Set-Location <frontend-repo>
npm install
npm run dev
```

### Local backend with H2

```powershell
Set-Location <backend-repo>
mvn clean package
java -jar target/wishaw-java-0.0.1-SNAPSHOT.jar
```

### Backend with Postgres

```powershell
$env:SPRING_PROFILES_ACTIVE='postgres'
$env:DB_URL='jdbc:postgresql://localhost:5432/wishaw'
$env:DB_USERNAME='wishaw'
$env:DB_PASSWORD='wishaw'
java -jar target/wishaw-java-0.0.1-SNAPSHOT.jar
```

### Kubernetes-ready deployment story

Say:

"For container or cluster deployment, the repository already includes the building blocks: config map, deployment, service, ingress, secret template, and horizontal pod autoscaler manifest."

## Strong Closing Statement

Use this almost verbatim:

"What makes this compelling is not just that it works. It transforms a manual programme into a structured digital service. Players get a clearer journey. Parents get visibility. Staff get time back. And the organisation gets a platform that can be configured, deployed, documented, and potentially licensed beyond a single site."

## Optional Executive Summary Close

If the audience is senior and short on time:

"In one platform, Wishaw YMCA gets youth progression, badge tracking, tournament operations, configurable governance, role-based admin tooling, documented APIs, multiple deployment profiles, and a foundation that is credible beyond a single pilot."

## Demo Recovery Notes

If anything goes wrong during the demo:

- If login fails, use the seeded admin or player credentials again.
- If browser state is stale, refresh the frontend and log back in.
- If local data looks unexpected, show the H2 console and demonstrate the seeded rows.
- If a UI action is blocked, switch to Swagger to prove the backend contract live.
- If you need a wow fallback, use the sport age-limit configuration and show the enforced join rejection.

## Suggested Demo Timing

- Problem framing: 1 minute
- Player journey: 3 minutes
- Age-policy governance moment: 2 minutes
- Admin journey: 3 minutes
- API, DB, NFRs, and deployment: 3 minutes
- Close and Q&A: 1 to 2 minutes

Total: 10 to 14 minutes