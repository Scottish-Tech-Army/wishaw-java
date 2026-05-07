# GitHub Copilot Instruction File - Backend Module

## Purpose
Build the backend for a digital badging web application for Wishaw YMCA Esports Academy.
The backend must be designed so that a separate React frontend can integrate with it later with minimal changes.

This instruction file is intentionally practical and strict. Follow it closely.

---

## Project Context
The charity currently tracks player progress through spreadsheets and an outdated WordPress site.
The new backend must support:
- multiple centres/sites
- groups by game and centre
- players with username/password login
- 5 main badge categories
- modules with challenges/sub-badges
- cumulative badge progress across modules
- leaderboards
- admin management
- import of previous tracker data from CSV exports
- parent read-only login as the only stretch goal included in scope

The application must be easy for non-technical admins to use and easy for a React frontend to consume later.

---

## Non-Negotiable Constraints
1. Use **Java + Spring Boot**.
2. Keep external libraries to a minimum.
3. **Lombok is allowed** and may be used to reduce boilerplate.
4. Do **not** use MapStruct, Apache POI, OpenCSV, ModelMapper, or any non-essential third-party utility library.
5. Use only core Spring Boot starters, Java standard library, and Lombok wherever possible.
6. Use **H2 in-memory database** for now.
7. Structure the code so that switching later to PostgreSQL/MySQL/Azure SQL requires minimal effort.
8. Expose clean REST APIs under `/api/v1/...`.
9. Backend and frontend will run as **two separate IntelliJ modules/windows**, so the backend must include:
   - predictable JSON contracts
   - CORS configuration placeholder
   - DTO-based responses
   - no server-side HTML rendering
10. Keep comments in important places to help future frontend integration.
11. Only include the following stretch goal: **Parent read-only login**.

---

## Recommended Dependencies Only
Use only these starters/libraries unless absolutely required:
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `com.h2database:h2`
- `org.projectlombok:lombok`
- `spring-boot-starter-test`

Avoid anything else unless there is a very strong reason.

---

## Java / Spring Assumptions
- Java 17 or above
- Spring Boot 3.x
- REST-first design
- JSON request/response payloads only

---

## Business Model to Implement

### Main Badge Categories
These should be seeded as configurable records or enums:
- GAME_MASTERY
- TEAMWORK
- ESPORTS_CITIZEN
- PERSONAL_DEVELOPMENT
- DIGITAL_SKILLS

### Badge Levels
Support configurable level thresholds.
Seed initial levels:
- Bronze: 0-30
- Silver: 31-70
- Gold: 71-120
- Platinum: 121+

Design the model so more levels can be added later without code changes.

### Core Concepts
Implement entities for:
- Centre
- Group
- UserAccount
- Role
- ParentLink (for parent read-only access)
- BadgeCategory
- BadgeLevel
- Module
- Challenge
- ChallengeSkillTag
- ModuleScheduleItem
- PlayerModuleEnrollment
- PlayerBadgeProgress
- ChallengeAward
- ImportBatch
- ImportRowAudit

---

## Scope to Implement in Backend

### Must Have
1. Authentication and authorization
2. Admin user management
3. Centre management
4. Group management
5. Player management
6. Parent read-only login
7. Module management
8. Challenge/sub-badge management
9. Module schedule management
10. Award progress to players
11. Calculate cumulative badge totals
12. Calculate badge levels
13. Player profile API
14. Leaderboard API
15. CSV import API for legacy progress
16. Import preview + commit flow
17. Basic validation and error handling

### Explicitly Out of Scope for Now
- evidence upload
- mini tournaments
- content upload storage
- advanced notifications
- certificate generation
- websocket live updates

---

## Package Structure
Use a clear package structure like this:

```text
com.wymca.badges
  config
  controller
  dto
    request
    response
  entity
  enums
  exception
  mapper
  repository
  security
  service
    auth
    admin
    player
    importer
    leaderboard
  util
```

Do not make the package structure overly clever.
Keep it readable.

---

## Lombok Usage Guidance
Lombok is allowed. Use it where it keeps the code cleaner, but avoid making the code hard to understand.

Recommended usage:
- `@Getter`
- `@Setter`
- `@NoArgsConstructor`
- `@AllArgsConstructor`
- `@Builder`
- `@RequiredArgsConstructor`

Be careful with:
- `@Data` on JPA entities
- `@ToString` on entities with relationships
- `@EqualsAndHashCode` on JPA entities with lazy associations

For JPA entities, prefer explicit and safe Lombok usage rather than overly broad annotations.

---

## Entity Design Guidance

### 1. Centre
Fields:
- id
- name
- code
- active
- createdAt
- updatedAt

### 2. Group
Fields:
- id
- name
- gameName
- ageBand
- centre
- active
- createdAt
- updatedAt

### 3. UserAccount
Fields:
- id
- username
- passwordHash
- displayName
- role
- active
- centre (nullable depending on role)
- group (nullable for admins)
- externalRef (for import mapping)
- createdAt
- updatedAt

Roles:
- SUPER_ADMIN
- CENTRE_ADMIN
- PLAYER
- PARENT

### 4. ParentLink
Purpose:
Link a parent account to one or more player accounts with read-only access.

Fields:
- id
- parentUser
- playerUser
- relationshipLabel (optional)

### 5. BadgeCategory
Fields:
- id
- code
- displayName
- description
- active

### 6. BadgeLevel
Fields:
- id
- name
- minPoints
- maxPoints (nullable for open-ended upper bound)
- rankOrder
- active

### 7. Module
Fields:
- id
- name
- gameName
- description
- active
- approved
- createdBy
- createdAt
- updatedAt

### 8. Challenge
Use the word `Challenge` in code, even if the UI later shows “sub-badge”.

Fields:
- id
- module
- badgeCategory
- name
- description
- points
- displayOrder
- active

### 9. ChallengeSkillTag
Fields:
- id
- challenge
- skillName
- displayOrder

### 10. ModuleScheduleItem
Fields:
- id
- module
- weekNumber
- sessionFocus
- linkedChallenge (nullable)
- sessionPlanUrl
- sessionSlidesUrl
- displayOrder

### 11. PlayerModuleEnrollment
Fields:
- id
- player
- module
- group
- status
- enrolledAt
- completedAt

Enrollment status:
- ASSIGNED
- IN_PROGRESS
- COMPLETED
- ARCHIVED

### 12. PlayerBadgeProgress
Fields:
- id
- player
- badgeCategory
- legacyPoints
- earnedPoints
- totalPoints
- currentLevelName
- updatedAt

Important:
- `totalPoints = legacyPoints + earnedPoints`
- store totals for fast reads, but always support recalculation from awards

### 13. ChallengeAward
Fields:
- id
- player
- module
- challenge
- badgeCategory
- awardedPoints
- awardedBy
- sourceType
- sourceReference
- awardDate
- importBatch (nullable)
- notes

Source types:
- MANUAL_ADMIN
- CSV_IMPORT
- SYSTEM_SEED

### 14. ImportBatch
Fields:
- id
- fileName
- importType
- status
- checksum
- uploadedBy
- createdAt
- completedAt
- summaryJson

Statuses:
- UPLOADED
- PREVIEW_READY
- COMMITTED
- FAILED

### 15. ImportRowAudit
Fields:
- id
- importBatch
- sourceSection
- sourceRowNumber
- status
- message
- rawDataJson

---

## Security Rules
Use Spring Security with simple role-based authorization.

### Required Access Rules
- SUPER_ADMIN: full access
- CENTRE_ADMIN: centre-scoped admin actions
- PLAYER: read own profile only
- PARENT: read linked player profiles only

### Authentication
Use username + password.
For the MVP, session-based auth or simple stateless token auth are both acceptable.
Choose the simpler approach that is easiest to integrate with React later.

Recommended practical path:
- implement REST login endpoint
- return a simple auth response object
- keep security layer modular so token-based auth can be added later if needed

### Passwords
Always store hashed passwords.
Use Spring Security password encoder.

### Frontend Integration Comments
Add comments like this near auth-related DTOs/controllers:

```java
// FRONTEND_INTEGRATION: React login screen will post username/password here.
// FRONTEND_INTEGRATION: Keep response shape stable for future frontend session handling.
```

---

## API Design Rules
1. Use `/api/v1/...`
2. Use DTOs for all requests and responses
3. Never expose entities directly
4. Return consistent response shape for errors
5. Use pagination only where useful later; not necessary everywhere in the first iteration
6. Keep naming simple and predictable

### Suggested Response Wrapper
Either:
- use plain DTOs for success and a common error payload for failure
or
- use a simple envelope only if consistently applied everywhere

Do not over-engineer.

### Error Payload Example Shape
```json
{
  "timestamp": "2026-03-30T10:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Module name is required",
  "path": "/api/v1/modules"
}
```

---

## APIs to Build

### Auth APIs
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout` (if session-based)
- `GET /api/v1/auth/me`

### User/Admin APIs
- `POST /api/v1/admin/users`
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/users/{id}`
- `PUT /api/v1/admin/users/{id}`
- `PATCH /api/v1/admin/users/{id}/status`
- `POST /api/v1/admin/parents/link-player`

### Centre APIs
- `POST /api/v1/admin/centres`
- `GET /api/v1/admin/centres`
- `PUT /api/v1/admin/centres/{id}`

### Group APIs
- `POST /api/v1/admin/groups`
- `GET /api/v1/admin/groups`
- `PUT /api/v1/admin/groups/{id}`

### Module APIs
- `POST /api/v1/admin/modules`
- `GET /api/v1/admin/modules`
- `GET /api/v1/admin/modules/{id}`
- `PUT /api/v1/admin/modules/{id}`
- `POST /api/v1/admin/modules/{id}/challenges`
- `PUT /api/v1/admin/challenges/{id}`
- `POST /api/v1/admin/modules/{id}/schedule-items`
- `PUT /api/v1/admin/schedule-items/{id}`

### Enrollment APIs
- `POST /api/v1/admin/enrollments`
- `PATCH /api/v1/admin/enrollments/{id}/status`

### Progress APIs
- `POST /api/v1/admin/progress/legacy-points`
- `POST /api/v1/admin/progress/award-challenge`
- `GET /api/v1/players/{playerId}/profile`
- `GET /api/v1/players/{playerId}/progress`
- `GET /api/v1/me/profile`
- `GET /api/v1/me/progress`

### Leaderboard APIs
- `GET /api/v1/leaderboards/global`
- `GET /api/v1/leaderboards/centre/{centreId}`
- `GET /api/v1/leaderboards/group/{groupId}`

### Import APIs
- `POST /api/v1/admin/import/csv/upload`
- `POST /api/v1/admin/import/{batchId}/map-players`
- `GET /api/v1/admin/import/{batchId}/preview`
- `POST /api/v1/admin/import/{batchId}/commit`
- `GET /api/v1/admin/import/{batchId}/report`

### Parent APIs
- `GET /api/v1/parent/players`
- `GET /api/v1/parent/players/{playerId}/profile`
- `GET /api/v1/parent/players/{playerId}/progress`

---

## CSV Import Requirements
The charity has legacy tracker data in spreadsheet-style tabular files.
For now, implement CSV import support rather than Excel-specific parsing.
Assume admins can export sheets to CSV before upload.

### Important Principle
Do not import only final totals.
Import:
1. legacy points per badge
2. challenge awards per player
3. then calculate totals

### CSV Types to Support
Design the import service to support these logical CSV types:
- module definition CSV
- module schedule CSV
- points tracker CSV
- summary validation CSV

### Import Workflow
Implement this flow:
1. upload CSV file(s)
2. create ImportBatch
3. parse into preview model
4. map CSV player columns to existing users or create new players
5. validate preview
6. commit import
7. store audit rows
8. recalculate player totals

### CSV Parsing Constraint
Because external dependencies are restricted:
- implement a small internal CSV parser utility using Java standard library
- support commas, quoted values, empty cells, and line-by-line parsing carefully
- keep parser isolated in `util`
- document limitations if you keep parsing intentionally simple

### Import Service Design
Create a clear service boundary:
- `CsvImportService`
- `ImportPreviewService`
- `ImportCommitService`
- `PlayerMappingService`

### Idempotency Requirement
Prevent duplicate imports.
Use checksum + source references where possible.
Do not create duplicate challenge awards if the same CSV is committed twice.

### Frontend Integration Comments
Add comments like:

```java
// FRONTEND_INTEGRATION: React import wizard will call preview first, then player mapping, then commit.
// FRONTEND_INTEGRATION: Keep preview DTO explicit so UI can show validation issues before final commit.
```

---

## Progress Calculation Rules

### Badge Progress Formula
For each player and badge category:
- totalPoints = legacyPoints + sum(all awarded challenge points for that badge)

### Level Calculation
Resolve level by checking configured thresholds.

### Recalculation
Provide a dedicated service method such as:
- `recalculatePlayerProgress(Long playerId)`
- `recalculateAllPlayerProgress()`

Call recalculation after:
- legacy points update
- challenge award
- import commit

---

## DTO Design Guidance
Use clear DTOs, for example:

### Requests
- `CreateUserRequest`
- `UpdateUserRequest`
- `CreateCentreRequest`
- `CreateGroupRequest`
- `CreateModuleRequest`
- `CreateChallengeRequest`
- `AwardChallengeRequest`
- `SetLegacyPointsRequest`
- `ImportPlayerMappingRequest`

### Responses
- `AuthResponse`
- `UserSummaryResponse`
- `PlayerProfileResponse`
- `BadgeProgressResponse`
- `LeaderboardEntryResponse`
- `ImportPreviewResponse`
- `ImportCommitResponse`
- `ApiErrorResponse`

Add explicit fields instead of generic maps wherever possible.

---

## Placeholder Comments for Frontend Integration
Add targeted comments in important classes.
Use a consistent marker: `FRONTEND_INTEGRATION`.

Examples:

```java
// FRONTEND_INTEGRATION: This DTO is used by the React player profile page.
// FRONTEND_INTEGRATION: Do not rename JSON fields without updating frontend contract.
// FRONTEND_INTEGRATION: UI will show category totals, current level, and module progress from this response.
```

Also add comments in:
- auth controller
- profile controller
- leaderboard controller
- import preview controller
- CORS config

---

## CORS and Environment Notes
Add a dedicated config class for CORS.

For now:
- allow localhost frontend origin through properties
- keep it configurable via application properties

Example placeholder expectation:

```java
// FRONTEND_INTEGRATION: set allowed origins for local React dev server here.
```

Use `application.yml` or `application.properties` with clear placeholders for:
- server port
- H2 config
- allowed frontend origin
- seed admin credentials (for local dev only)

---

## Seed Data
Create startup seed data for local development:
- badge categories
- badge levels
- one super admin
- one centre admin
- one centre
- one group
- one player
- one parent account linked to the sample player
- one sample module with a few challenges

This will help backend and frontend development happen independently.

---

## Validation Rules
Add bean validation for request DTOs.
Examples:
- required names must not be blank
- points must be positive or zero as appropriate
- usernames must be unique
- badge ranges must not overlap
- challenge name should be unique within a module

Handle validation errors clearly.

---

## Logging and Error Handling
Implement:
- global exception handler
- meaningful service-level exceptions
- warning logs for invalid import rows
- error logs for failed commits

Do not add heavy logging frameworks beyond what Spring Boot already provides.

---

## Testing Requirements
Write unit/integration tests for at least:
- auth login success/failure
- progress recalculation
- legacy points update
- challenge award flow
- parent read-only authorization
- CSV preview parsing
- CSV commit idempotency

Use Spring Boot test support only.
Keep tests readable.

---

## Implementation Sequence
Build in this order:
1. project skeleton and configuration
2. entities + repositories
3. seed data
4. security + auth
5. admin CRUD APIs
6. progress calculation service
7. player profile APIs
8. leaderboard APIs
9. parent read-only APIs
10. CSV import preview + commit
11. tests
12. final cleanup and comments for frontend integration

---

## Coding Style Rules
- keep methods reasonably small
- prefer explicit code over magic abstractions
- no unnecessary generics-heavy frameworks
- no reflection-based mapping helpers
- no premature microservice split
- keep names domain-friendly and readable

---

## Definition of Done for Backend
The backend is complete for MVP when:
1. it starts successfully with H2
2. seeded users can log in
3. admins can manage centres, groups, players, modules, and challenges
4. progress can be awarded manually
5. player profile and leaderboard APIs return correct data
6. parent account can view only linked player data
7. CSV import can preview and commit legacy progress safely
8. APIs are easy for a separate React frontend to consume
9. frontend placeholder comments exist in key integration points

---

## Final Instruction to Copilot
Generate the backend incrementally.
Start with:
1. project structure
2. entities
3. repositories
4. seed data
5. security and auth

After that, continue with controllers/services in the implementation order described above.
Keep the design clean, minimal, and frontend-friendly.
