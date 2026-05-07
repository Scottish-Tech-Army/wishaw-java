# Wishaw YMCA eSports Badge Portal — Backend-Only Specification

> **Purpose**: This document extracts the **backend-relevant information** from the uploaded brief/scope/tracker materials and then turns that into a **detailed backend specification**.
>
> **Structure**:
> 1. **Source-derived backend facts** (directly grounded in the docs)
> 2. **Proposed backend design** (implementation guidance inferred from those facts)
>
> Use this as the backend handoff for an engineering agent or backend developer.

---

# 1) Source-derived backend-only information

## 1.1 Problem the backend must solve

The current process relies on a self-built WordPress site and spreadsheets. Profiles were updated manually, and once the programme expanded to multiple groups/sites and introduced modules, the existing setup could no longer support the workflow. The organisation started tracking progress in Google Sheets instead, which left player profiles out of date and increased admin effort. This means the backend must replace manual spreadsheet + WordPress workflows with a system of record that can model users, modules, challenges/sub-badges, progress updates, and multi-site administration.

## 1.2 Core business capabilities that require backend support

The uploaded materials imply the backend must support the following capabilities:

### A. Badge / XP / progress engine
- There are **5 main badges** used across age groups, games, modules, and activities.
- Sub-badges/challenges award points toward one of these 5 main badges.
- Points persist across modules and level up the main badges.
- Current level thresholds are:
  - 0–30 = Bronze
  - 31–70 = Silver
  - 71–120 = Gold
  - 120+ = Platinum
- The organisation would like flexibility to add new future levels such as Emerald, Diamond, Master, and Pro.

### B. Module / challenge management
- The system must support **modules** (course-like structures) lasting around **12–16 weeks**.
- Each module contains multiple sub-badges/challenges.
- Each sub-badge/challenge is associated to one of the 5 main badges.
- Each sub-badge/challenge has a points value and associated Youthwork Skills and Outcomes Framework skills.
- Modules may also have session plans, delivery notes, and lesson resources in the ideal solution.

### C. User / group / centre management
- Users should be split into groups based on their game group and centre.
- The solution needs to work across **multiple groups** and **multiple sites/centres**.
- The ideal solution includes a **main app manager admin**, additional admins per centre, and approval for new modules.

### D. Admin workflows
- Admins need to add and remove groups, users, and modules.
- Admins need to award sub-badges / complete challenges / log progress.
- The ideal solution includes per-centre admins who can add users to approved modules and update user progress.
- The current operational pain is admin burden, so backend workflows should minimise manual repetitive operations.

### E. Profiles and progress views
- User profiles should show name, image, overall badge progress, module progress, and completed modules.
- Leaderboards should compare users by badge scores and completed modules.
- It would be useful to support both centre-specific and global leaderboards.

### F. Authentication / privacy expectations
- The brief explicitly states there should be **no data collection** beyond what is necessary, and users should log in with **username and password**, with **no personal details added**.
- The scope document also notes access to current student data and WordPress login would be required during transition.

### G. Extended / ideal backend capabilities
The “perfect solution” / “mindblowing solution” imply future backend requirements:
- Upload and store lesson/session resources (e.g., PowerPoint/video)
- Evidence upload by users for admin approval
- Main admin manages other admins
- Approval workflow for use of new modules
- Centre leaderboards
- Mini tournaments between centres
- Packaging the app as a SaaS/licensable product for other YMCAs

---

## 1.3 Domain facts extracted from the tracker data

The uploaded spreadsheet samples provide useful backend domain shape:

### Main badge categories
Use these as canonical categories:
1. Game Mastery
2. Teamwork
3. Esports Citizen
4. Personal Development
5. Digital Skills

### Example modules already represented in source data
- Road to Diamond
- Defeat the Ender Dragon

### Example challenge / sub-badge records present in the source
The source data includes challenge-like items such as:
- Skill Evaluation
- Master Plan
- Basic Mechanics
- Duo Snatcher
- Tactical Minds
- Goal Scorer
- Conduct Creator
- Data Logger
- Base Builder
- Nether Ready
- Dragon Prepared
- Team Planner
- Role Player
- Goal Setter
- World Code
- Storyteller
- Dragon Slayer

### Schedule / progression data shape from tracker
The spreadsheet contains examples of:
- module schedules by week
- challenge point values
- per-player points tracking
- aggregate player points per main badge category

That means the backend should expect at least these data families:
- module definition data
- challenge definition data
- schedule data
- awarded progress events / points logs
- per-user aggregated badge totals
- leaderboard aggregation data

---

# 2) Proposed backend design (recommended implementation)

> **Note**: This section is a **recommended backend architecture** derived from the source requirements. It is not directly quoted from the docs; it is the engineering design that best fits the documented needs.

## 2.1 Backend mission

Build a backend that acts as the **system of record** for:
- centres
- groups
- users
- admins / roles
- modules
- challenges/sub-badges
- badge category progress
- activity / award logs
- leaderboards
- uploaded resources/evidence (future)

The backend should prioritize:
- simplicity for non-technical admins
- clear auditability of awards/progress updates
- flexibility to add new modules/levels/centres
- low operational cost
- SaaS-readiness for multi-tenant expansion later

---

## 2.2 First-principles domain model

### Mental model
Treat the system as an **evented progress ledger plus reference data**.

- **Reference/config data**:
  - centres
  - groups
  - users
  - roles
  - badge categories
  - levels
  - modules
  - challenges
  - skills
  - schedules

- **Transactional/event data**:
  - challenge awarded
  - xp awarded
  - module enrollment
  - challenge completion
  - admin approval
  - evidence submitted
  - evidence approved/rejected

- **Derived/read-model data**:
  - user total XP
  - per-badge totals
  - current badge levels
  - module completion percentage
  - leaderboards
  - centre-level aggregates

### Why this model fits
This domain is fundamentally about **recording progress changes over time** and then computing views from those changes. That means a ledger/event-style model gives better auditability than only storing mutable totals.

---

## 2.3 Recommended architecture options

### Option A — Simple CRUD backend with derived columns
**Model**: store current totals directly on user progress rows and write logs separately.

**Pros**:
- simpler to build initially
- fewer moving parts
- good for MVP

**Cons**:
- harder to rebuild totals if bugs happen
- more risk of inconsistency between current totals and logs
- weaker audit/replay model

### Option B — Event ledger + projections (**recommended**)
**Model**: every award/progress change is stored as an immutable event; projections/read models compute totals and leaderboards.

**Pros**:
- excellent audit trail
- easier to fix/recompute derived state
- better fit for approvals and future tournaments
- easier SaaS evolution

**Cons**:
- slightly more implementation complexity
- requires projection/update logic

### Option C — Full event sourcing everywhere
**Model**: all state derived from streams/aggregates.

**Pros**:
- maximum traceability and flexibility

**Cons**:
- overkill for this use case
- too much complexity for likely project scope and budget

### Recommendation
Use **Option B**: a pragmatic ledger/projection design. It gives strong correctness and auditability without going full event-sourcing complexity.

---

## 2.4 Recommended bounded contexts / backend modules

Structure the backend around these logical modules:

1. **Identity & Access**
   - users
   - admins
   - roles
   - credentials
   - session/token issuance

2. **Organisation**
   - centres
   - groups
   - memberships
   - centre-scoped admin privileges

3. **Curriculum**
   - main badges
   - levels
   - modules
   - challenges/sub-badges
   - skills
   - schedules
   - resources

4. **Progress Engine**
   - award events
   - challenge completion
   - per-badge totals
   - per-user XP
   - level calculation rules

5. **Leaderboard**
   - user ranking
   - group ranking
   - centre ranking
   - global ranking

6. **Evidence & Approval** (future-friendly)
   - uploads
   - moderation states
   - approval decisions

7. **Audit / Reporting**
   - admin action history
   - exportable logs
   - reconciliation jobs

---

## 2.5 Recommended relational data model

Below is a practical schema outline.

### 2.5.1 Identity / org tables

#### `centres`
- `centre_id` (PK)
- `name`
- `region`
- `status`
- `created_at`

#### `groups`
- `group_id` (PK)
- `centre_id` (FK)
- `name`
- `game`
- `status`
- `created_at`

#### `users`
- `user_id` (PK)
- `username` (unique)
- `password_hash`
- `display_name`
- `avatar_url` (nullable)
- `centre_id` (FK)
- `group_id` (FK, nullable)
- `role_type` (`student|centre_admin|global_admin`)
- `status`
- `created_at`
- `updated_at`

> If the organisation truly wants minimal personal data, keep this table sparse and avoid storing unnecessary profile information.

### 2.5.2 Badge / level configuration tables

#### `badge_categories`
- `badge_category_id` (PK)
- `slug`
- `name`
- `description`
- `sort_order`

#### `badge_levels`
- `badge_level_id` (PK)
- `badge_category_id` (nullable if global rule)
- `name` (`Bronze`, `Silver`, etc.)
- `min_points_inclusive`
- `max_points_exclusive` (nullable for open-ended)
- `sort_order`
- `is_active`

### 2.5.3 Curriculum tables

#### `modules`
- `module_id` (PK)
- `title`
- `game`
- `description`
- `duration_weeks`
- `status` (`draft|approved|active|archived`)
- `created_by_user_id`
- `approved_by_user_id` (nullable)
- `created_at`
- `updated_at`

#### `skills`
- `skill_id` (PK)
- `name`
- `framework` (e.g. YSOF)

#### `module_challenges`
- `challenge_id` (PK)
- `module_id` (FK)
- `title`
- `description`
- `badge_category_id` (FK)
- `points`
- `sort_order`
- `is_active`

#### `challenge_skills`
- `challenge_id` (FK)
- `skill_id` (FK)
- composite PK

#### `module_schedule_items`
- `schedule_item_id` (PK)
- `module_id` (FK)
- `week_number`
- `session_focus`
- `challenge_id` (nullable)
- `session_plan_url` (nullable)
- `session_slides_url` (nullable)

### 2.5.4 Enrollment / progress tables

#### `user_module_enrollments`
- `enrollment_id` (PK)
- `user_id` (FK)
- `module_id` (FK)
- `assigned_by_user_id`
- `status` (`active|completed|withdrawn`)
- `started_at`
- `completed_at` (nullable)

#### `progress_events`
- `progress_event_id` (PK)
- `user_id` (FK)
- `centre_id` (FK)
- `group_id` (FK, nullable)
- `module_id` (FK, nullable)
- `challenge_id` (FK, nullable)
- `badge_category_id` (FK)
- `event_type` (`challenge_completed|manual_xp_award|badge_award|adjustment|legacy_import`)
- `points_delta`
- `note`
- `created_by_user_id`
- `created_at`
- `source_ref` (nullable idempotency/import reference)

#### `user_badge_totals`
- `user_id` (FK)
- `badge_category_id` (FK)
- `total_points`
- `current_level_name`
- `updated_at`
- composite PK (`user_id`, `badge_category_id`)

#### `user_module_progress`
- `user_id` (FK)
- `module_id` (FK)
- `completed_challenges_count`
- `total_challenges_count`
- `completion_percent`
- `status`
- `updated_at`
- composite PK (`user_id`, `module_id`)

### 2.5.5 Evidence / resource tables (future)

#### `resources`
- `resource_id` (PK)
- `module_id` (FK)
- `resource_type` (`slide|video|plan|note|other`)
- `title`
- `url`
- `uploaded_by_user_id`
- `created_at`

#### `evidence_submissions`
- `evidence_id` (PK)
- `user_id` (FK)
- `module_id` (FK)
- `challenge_id` (FK)
- `file_url`
- `submitted_at`
- `status` (`pending|approved|rejected`)
- `reviewed_by_user_id` (nullable)
- `reviewed_at` (nullable)
- `review_note` (nullable)

### 2.5.6 Audit / admin tables

#### `admin_action_audit`
- `audit_id` (PK)
- `actor_user_id`
- `action_type`
- `target_type`
- `target_id`
- `metadata_json`
- `created_at`

---

## 2.6 Core backend workflows

### Workflow 1 — Award challenge completion
```text
Admin selects student -> selects module/challenge -> backend validates enrollment/challenge ->
writes immutable progress_event -> recomputes or queues projection update ->
updates user_badge_totals + user_module_progress -> returns updated profile summary
```

### Workflow 2 — Manual XP award
```text
Admin selects student -> selects badge category / module context -> enters XP ->
backend writes progress_event(event_type=manual_xp_award) ->
projection updates totals/leaderboard -> audit log entry created
```

### Workflow 3 — Module approval
```text
Centre/global admin drafts module -> global admin reviews ->
status changes draft -> approved -> centres can assign approved module to users/groups
```

### Workflow 4 — Evidence submission (future)
```text
Student uploads evidence -> backend stores metadata + file reference ->
status=pending -> admin approves/rejects -> if approved, award event may be generated
```

### Workflow 5 — Leaderboard refresh
```text
progress_event written -> projection updates aggregate XP/badge totals ->
leaderboard read model refreshed by centre/group/global scope
```

---

## 2.7 API surface (recommended)

Use a clean REST API first. GraphQL is not required here.

### Auth
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/refresh`
- `GET /api/auth/me`

### Centres / groups
- `GET /api/centres`
- `GET /api/centres/{centreId}`
- `GET /api/centres/{centreId}/groups`
- `POST /api/groups`
- `PATCH /api/groups/{groupId}`

### Users
- `GET /api/users/{userId}`
- `GET /api/users?centreId=&groupId=&role=`
- `POST /api/users`
- `PATCH /api/users/{userId}`
- `POST /api/users/{userId}/assign-module`

### Badge config
- `GET /api/badges/categories`
- `GET /api/badges/levels`
- `PUT /api/badges/levels`

### Modules / challenges
- `GET /api/modules`
- `GET /api/modules/{moduleId}`
- `POST /api/modules`
- `PATCH /api/modules/{moduleId}`
- `POST /api/modules/{moduleId}/approve`
- `POST /api/modules/{moduleId}/challenges`
- `PATCH /api/challenges/{challengeId}`
- `GET /api/modules/{moduleId}/schedule`

### Progress / awards
- `POST /api/progress/award-challenge`
- `POST /api/progress/award-xp`
- `POST /api/progress/adjust`
- `GET /api/users/{userId}/progress`
- `GET /api/users/{userId}/activity`

### Leaderboards
- `GET /api/leaderboards/global`
- `GET /api/leaderboards/centres/{centreId}`
- `GET /api/leaderboards/groups/{groupId}`

### Evidence / resources (future)
- `POST /api/modules/{moduleId}/resources`
- `POST /api/evidence`
- `POST /api/evidence/{evidenceId}/approve`
- `POST /api/evidence/{evidenceId}/reject`

---

## 2.8 Backend rules / invariants

Define these invariants explicitly:

1. Every challenge belongs to exactly one module.
2. Every challenge maps to exactly one main badge category.
3. Points awarded by a challenge must match configured points unless an explicit admin override path is used.
4. Badge totals are monotonically derived from progress events unless an explicit adjustment event is created.
5. Leaderboards must be computed from derived totals, not ad hoc client math.
6. Only authorized admins can create modules, assign users, or award progress.
7. Only approved modules can be assigned to users if module approval is enabled.
8. If evidence approval is required, completion should not be final until approval occurs.

---

## 2.9 Correctness model

### Consistency model
- **Primary transactional writes**: strong consistency within the database transaction that records a `progress_event`
- **Derived read models / leaderboards**: near-real-time eventual consistency is acceptable if projections are async

### Delivery semantics
- Use **at-least-once** processing internally for projection updates
- Achieve **effectively-once** outcomes using **idempotency keys / source_ref** on award requests and imports

### Ordering requirements
- Per-user ordering matters for progress projections
- Global ordering is not required

### Time model
- Store all timestamps in UTC
- Use server-generated timestamps for audit integrity

### Idempotency strategy
- For manual awards/imports, require a `source_ref` or generated idempotency key
- Duplicate submit should not double-award XP

---

## 2.10 Security model

### Trust boundaries
- mobile/web client is untrusted
- backend enforces all authorization and points logic
- storage/database are trusted backend infrastructure

### Auth recommendation
- username/password login with secure password hashing (Argon2id or bcrypt with strong work factor)
- issue short-lived access token + refresh token/session cookie

### Authorization model
Use RBAC:
- `student`
- `centre_admin`
- `global_admin`

Optional constraints:
- centre admins only manage users/groups/modules within their centre
- only global admins approve modules for wider usage

### Security requirements
- never trust client-calculated totals
- validate every challenge/category/module relation server-side
- audit every admin award/adjustment
- rate-limit login endpoints
- encrypt sensitive secrets in deployment environment

---

## 2.11 Observability plan

### Logs
Structured logs for:
- login attempts
- award requests
- module creation/approval
- projection update failures
- file upload approval actions

### Metrics
- successful award requests/sec
- failed award requests/sec
- leaderboard query latency
- projection lag
- login failure rate
- pending evidence count

### Traces
Trace key paths:
- login
- award-challenge
- award-xp
- get-user-progress
- leaderboard fetch

### Cardinality warning
Do not put raw usernames or free-form notes into high-cardinality metric labels.

---

## 2.12 Failure modes and mitigations

### Duplicate awards
**Risk**: Admin double-submits a form.
**Mitigation**: idempotency key + unique `source_ref` constraint + safe retry behavior.

### Projection drift
**Risk**: derived totals diverge from event log.
**Mitigation**: periodic rebuild job from `progress_events`.

### Unauthorized centre access
**Risk**: centre admin modifies another centre’s users.
**Mitigation**: enforce centre-scoped authorization checks on every write/read.

### Partial upload / evidence state mismatch
**Risk**: file upload succeeds but metadata write fails (or vice versa).
**Mitigation**: use upload tokens and finalize metadata only after successful storage confirmation.

### Legacy import corruption
**Risk**: spreadsheet migration produces duplicate or malformed awards.
**Mitigation**: import in batches with validation reports, idempotent source references, and reconciliation totals.

---

## 2.13 Migration / import needs from current state

Because the current state includes spreadsheets and a WordPress site, add explicit migration support.

### Import capabilities needed
- import users
- import groups/centres
- import module definitions
- import challenge definitions with points + skills
- import legacy points totals
- import historical award data where available

### Migration strategy
1. import reference data first
2. import legacy badge totals as `legacy_import` events
3. verify user totals vs spreadsheet samples
4. only then allow ongoing admin updates in the new app

---

## 2.14 Suggested tech stack (pragmatic)

This is a recommendation, not a requirement from source docs.

### Backend app
- **TypeScript / Node.js** (NestJS / Fastify) **or** **C# / ASP.NET Core**

### Database
- **PostgreSQL**

### File storage
- S3-compatible object storage / Azure Blob / GCS for future resources/evidence

### Cache (optional)
- Redis for leaderboard caching and session acceleration if needed

### Why this stack
- low cost
- easy developer onboarding
- good support for relational + audit-friendly models
- strong support for future SaaS growth

---

## 2.15 Suggested backlog for backend delivery

### Phase 1 — MVP backend
- auth/login
- centres/groups/users
- main badge config
- modules/challenges
- progress event writes
- user progress projection
- centre/global/group leaderboards
- admin audit trail

### Phase 2 — Usability / scale
- import tooling from spreadsheets
- module approval workflow
- richer admin permissions
- resource attachments
- reporting exports

### Phase 3 — Extended platform
- evidence submission/approval
- centre-vs-centre competition features
- SaaS tenant isolation model
- subscription/licensing support

---

## 2.16 Hand-off summary for a backend engineer / agent

If you are building the backend, optimize for these outcomes first:

1. **Correct progress tracking**
2. **Simple admin award flows**
3. **Reliable leaderboard aggregation**
4. **Auditability of every points change**
5. **Flexibility for modules/challenges/levels to evolve**
6. **Low-cost architecture that can later become multi-tenant SaaS**

The best backend shape here is a **relational system of record plus immutable progress events and derived read models**.
