# Architecture

## Table of Contents
- [System Overview](#system-overview)
- [High-Level Architecture](#high-level-architecture)
- [Backend Component Architecture](#backend-component-architecture)
- [Domain Model](#domain-model)
- [Login and Session Flow](#login-and-session-flow)
- [Authorization and Role Model](#authorization-and-role-model)
- [CSV Import Flow](#csv-import-flow)
- [Progress and Player Read Flow](#progress-and-player-read-flow)
- [Security Behavior Summary](#security-behavior-summary)
- [Local Run Instructions](#local-run-instructions)

## System Overview
This repository contains a Spring Boot backend that exposes session-authenticated REST endpoints under `/api/v1`. Controllers handle authentication, administration, imports, player/parent reads, and leaderboards. Services implement business rules for user management, centres/groups/modules, enrollments, CSV import preview/commit, player progress, and parent read-only access. Persistence is built around JPA entities such as `UserAccount`, `Module`, `Challenge`, `PlayerBadgeProgress`, `ChallengeAward`, and `ImportBatch`.

## High-Level Architecture
```mermaid
flowchart LR
    A[Browser or API Client]
    B[Spring Security Filter Chain]
    C[REST Controllers]
    D[Service Layer]
    E[Spring Data Repositories]
    F[(Configured Database)]

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
```

## Backend Component Architecture
```mermaid
flowchart TD
    subgraph Web[Controllers]
        AuthController
        AdminUserController
        CentreAdminController
        GroupAdminController
        ModuleAdminController
        EnrollmentAdminController
        ImportController
        ProgressAdminController
        PlayerController
        MeController
        ParentController
        LeaderboardController
    end

    subgraph Security[Security]
        SecurityConfig
        CustomUserDetailsService
    end

    subgraph Services[Services]
        AuthService
        AuthenticatedUserService
        AdminUserService
        CentreAdminService
        GroupAdminService
        ModuleAdminService
        EnrollmentAdminService
        CsvImportService
        ImportPreviewService
        PlayerMappingService
        ImportCommitService
        ProgressService
        ParentReadOnlyService
        LeaderboardService
    end

    subgraph Data[Persistence]
        Repositories
        Entities
    end

    SecurityConfig --> CustomUserDetailsService
    SecurityConfig --> Web

    AuthController --> AuthService
    AdminUserController --> AdminUserService
    CentreAdminController --> CentreAdminService
    GroupAdminController --> GroupAdminService
    ModuleAdminController --> ModuleAdminService
    EnrollmentAdminController --> EnrollmentAdminService
    ImportController --> CsvImportService
    ImportController --> ImportPreviewService
    ImportController --> PlayerMappingService
    ImportController --> ImportCommitService
    ProgressAdminController --> ProgressService
    PlayerController --> ProgressService
    MeController --> ProgressService
    ParentController --> ParentReadOnlyService
    LeaderboardController --> LeaderboardService

    AuthService --> AuthenticatedUserService
    ParentReadOnlyService --> ProgressService

    AuthenticatedUserService --> Repositories
    AdminUserService --> Repositories
    CentreAdminService --> Repositories
    GroupAdminService --> Repositories
    ModuleAdminService --> Repositories
    EnrollmentAdminService --> Repositories
    CsvImportService --> Repositories
    ImportPreviewService --> Repositories
    PlayerMappingService --> Repositories
    ImportCommitService --> Repositories
    ProgressService --> Repositories
    ParentReadOnlyService --> Repositories
    LeaderboardService --> Repositories
    Repositories --> Entities
```

## Domain Model
```mermaid
erDiagram
    UserAccount {
        bigint id
        string username
        string passwordHash
        string displayName
        string role
        boolean active
        bigint centreId
        bigint groupId
        string externalRef
    }

    Centre {
        bigint id
        string name
        string code
        boolean active
    }

    Group {
        bigint id
        string name
        string gameName
        string ageBand
        bigint centreId
        boolean active
    }

    Module {
        bigint id
        string name
        string gameName
        string description
        boolean active
        boolean approved
        bigint createdBy
    }

    Challenge {
        bigint id
        bigint moduleId
        bigint badgeCategoryId
        string name
        int points
        int displayOrder
        boolean active
    }

    ChallengeSkillTag {
        bigint id
        bigint challengeId
        string skillName
        int displayOrder
    }

    ModuleScheduleItem {
        bigint id
        bigint moduleId
        int weekNumber
        string sessionFocus
        bigint linkedChallengeId
        int displayOrder
    }

    BadgeCategory {
        bigint id
        string code
        string displayName
        boolean active
    }

    BadgeLevel {
        bigint id
        string name
        int minPoints
        int maxPoints
        int rankOrder
        boolean active
    }

    PlayerBadgeProgress {
        bigint id
        bigint playerId
        bigint badgeCategoryId
        int legacyPoints
        int earnedPoints
        int totalPoints
        string currentLevelName
    }

    ChallengeAward {
        bigint id
        bigint playerId
        bigint moduleId
        bigint challengeId
        bigint badgeCategoryId
        int awardedPoints
        bigint awardedBy
        string sourceType
        string sourceReference
        date awardDate
        bigint importBatchId
    }

    PlayerModuleEnrollment {
        bigint id
        bigint playerId
        bigint moduleId
        bigint groupId
        string status
        datetime enrolledAt
        datetime completedAt
    }

    ImportBatch {
        bigint id
        string fileName
        string importType
        string status
        string checksum
        bigint uploadedBy
        datetime createdAt
        datetime completedAt
    }

    ImportRowAudit {
        bigint id
        bigint importBatchId
        string sourceSection
        int sourceRowNumber
        string status
        string message
        string rawDataJson
    }

    ParentLink {
        bigint id
        bigint parentUserId
        bigint playerUserId
        string relationshipLabel
    }

    Centre ||--o{ UserAccount : has
    Centre ||--o{ Group : owns
    Group ||--o{ UserAccount : groups
    UserAccount ||--o{ Module : creates
    Module ||--o{ Challenge : contains
    Challenge ||--o{ ChallengeSkillTag : tags
    Module ||--o{ ModuleScheduleItem : schedules
    Challenge ||--o{ ModuleScheduleItem : links
    BadgeCategory ||--o{ Challenge : categorizes
    UserAccount ||--o{ PlayerBadgeProgress : tracks
    BadgeCategory ||--o{ PlayerBadgeProgress : for
    UserAccount ||--o{ ChallengeAward : receives
    UserAccount ||--o{ ChallengeAward : awards
    Module ||--o{ ChallengeAward : sourced_from
    Challenge ||--o{ ChallengeAward : sourced_from
    BadgeCategory ||--o{ ChallengeAward : categorizes
    ImportBatch ||--o{ ChallengeAward : imports
    UserAccount ||--o{ PlayerModuleEnrollment : enrolls
    Module ||--o{ PlayerModuleEnrollment : assigns
    Group ||--o{ PlayerModuleEnrollment : scopes
    UserAccount ||--o{ ImportBatch : uploads
    ImportBatch ||--o{ ImportRowAudit : audits
    UserAccount ||--o{ ParentLink : parent
    UserAccount ||--o{ ParentLink : player
```

`BadgeLevel` is intentionally standalone in the schema: progress stores the resolved level name as text, and `ProgressService` applies the active level ranges globally from `minPoints`/`maxPoints`.

## Login and Session Flow
```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant AuthController
    participant AuthService
    participant AuthManager as AuthenticationManager
    participant SecurityContext as Security Context
    participant Session as HTTP Session

    Client->>AuthController: POST /api/v1/auth/login
    AuthController->>AuthService: login(request, httpRequest)
    AuthService->>AuthManager: authenticate(username, password)
    AuthManager-->>AuthService: Authentication
    AuthService->>Session: invalidate existing session if present
    AuthService->>SecurityContext: create context and set Authentication
    AuthService->>Session: create new session
    AuthService->>Session: store SPRING_SECURITY_CONTEXT
    AuthService-->>AuthController: AuthResponse
    AuthController-->>Client: AuthResponse + session cookie

    Client->>AuthController: GET /api/v1/auth/me
    AuthController->>AuthService: me(authentication)
    AuthService-->>AuthController: current user details
    AuthController-->>Client: AuthResponse

    Client->>AuthController: POST /api/v1/auth/logout
    AuthController->>AuthService: logout(request)
    AuthService->>Session: invalidate session if present
    AuthService->>SecurityContext: clear context
    AuthController-->>Client: 204 No Content
```

## Authorization and Role Model
```mermaid
flowchart TD
    A[Incoming request] --> B{Path and method}

    B -->|POST /api/v1/auth/login| P1[permitAll]
    B -->|POST /api/v1/auth/logout| P2[permitAll]
    B -->|GET /openapi.yaml| P3[permitAll]
    B -->|GET /api/v1/auth/me| A1[authenticated]
    B -->|/api/v1/admin/**| A2[ROLE_SUPER_ADMIN or ROLE_CENTRE_ADMIN]
    B -->|/api/v1/parent/**| A3[ROLE_PARENT]
    B -->|/api/v1/me/**| A4[ROLE_PLAYER]
    B -->|GET /api/v1/players/**| A5[ROLE_SUPER_ADMIN or ROLE_CENTRE_ADMIN]
    B -->|GET /api/v1/leaderboards/**| A6[any authenticated role]
    B -->|/h2-console/**| H2[permit or deny based on spring.h2.console.enabled]
    B -->|anything else| A7[authenticated]
```

Important behavior from the reviewed code:

- `SecurityConfig` gives both `SUPER_ADMIN` and `CENTRE_ADMIN` access to every `/api/v1/admin/**` endpoint. The backend does **not** currently restrict centre creation, user management, module management, enrollment management, or import endpoints to super-admin only.
- `ProgressService` adds the main service-level scope check: `SUPER_ADMIN` can manage any player, `CENTRE_ADMIN` can only manage players in the same centre, and other roles are rejected.
- `ParentReadOnlyService` verifies a `ParentLink` before allowing `/api/v1/parent/players/{playerId}/profile` or `/progress`.
- Player self-service reads are exposed through `/api/v1/me/**`, not through a separate player-only write API.
- Leaderboards are available to any authenticated user, regardless of role.

## CSV Import Flow
```mermaid
flowchart TD
    A["POST /api/v1/admin/import/csv/upload"] --> B{Same file checksum already exists?}
    B -->|Yes| C[Return existing preview for stored batch]
    B -->|No| D[Create ImportBatch with status UPLOADED]
    D --> E[Parse CSV rows]
    E --> F[Validate required username and badgeCategoryCode]
    F --> G[Mark row OK, WARNING, or ERROR]
    G --> H[Persist ImportRowAudit for each row]
    H --> I[Store ImportBatchState in summaryJson]
    I --> J[Set batch status PREVIEW_READY]
    J --> K[Return ImportPreviewResponse]

    K --> L["POST /api/v1/admin/import/<batchId>/map-players"]
    L --> M[Map missing usernames to existing PLAYER account IDs]
    M --> N[Remove resolved names from unmappedPlayers]

    N --> O["POST /api/v1/admin/import/<batchId>/commit"]
    O --> P{Any unmapped players left?}
    P -->|Yes| Q[Reject commit]
    P -->|No| R[Process each non-ERROR row]
    R --> S[Add legacyPoints to PlayerBadgeProgress if supplied]
    R --> T[Create ChallengeAward with sourceType CSV_IMPORT if challengePoints supplied]
    S --> U[Recalculate affected player progress]
    T --> U
    U --> V[Mark batch COMMITTED and set completedAt]
```

What the import code actually does:

- Upload preview is checksum-based, so re-uploading the exact same file returns the existing batch preview instead of creating a duplicate batch.
- Unknown usernames are treated as `WARNING`, not `ERROR`, because they can be resolved through explicit mapping.
- Mapping only accepts existing `PLAYER` accounts; the import flow does not auto-create users.
- Commit skips rows marked `ERROR`.
- Commit can create challenge-award records from `challengePoints`, update legacy points from `legacyPoints`, or do both for the same row.
- Imported awards are created directly from CSV row data; `ImportCommitService` does not populate `challenge` or `module` on those `ChallengeAward` records.

## Progress and Player Read Flow
```mermaid
sequenceDiagram
    autonumber
    participant Admin as ProgressAdminController
    participant ProgressService
    participant Repo as Repositories

    Admin->>ProgressService: setLegacyPoints(...) or awardChallenge(...)
    ProgressService->>Repo: load actor, player, and category/challenge
    ProgressService->>ProgressService: enforce SUPER_ADMIN or same-centre CENTRE_ADMIN

    alt setLegacyPoints
        ProgressService->>Repo: get or create PlayerBadgeProgress(player, category)
        ProgressService->>Repo: update legacyPoints
    else awardChallenge
        ProgressService->>Repo: create ChallengeAward using challenge module/category/points
    end

    ProgressService->>Repo: recalculate all active badge-category progress for the player
    ProgressService->>Repo: sum earned challenge-award points by category
    ProgressService->>Repo: resolve currentLevelName from active BadgeLevel ranges
    ProgressService-->>Admin: BadgeProgressResponse
```

```mermaid
sequenceDiagram
    autonumber
    participant Player as MeController
    participant AdminReader as PlayerController
    participant Parent as ParentController
    participant ParentReadOnlyService
    participant ProgressService

    Player->>ProgressService: getCurrentPlayerProfile / getCurrentPlayerProgress
    ProgressService-->>Player: own profile/progress

    AdminReader->>ProgressService: getPlayerProfile / getPlayerProgress
    ProgressService->>ProgressService: enforce SUPER_ADMIN or same-centre CENTRE_ADMIN
    ProgressService-->>AdminReader: target player profile/progress

    Parent->>ParentReadOnlyService: getLinkedPlayers / getLinkedPlayerProfile / getLinkedPlayerProgress
    ParentReadOnlyService->>ParentReadOnlyService: verify ParentLink
    ParentReadOnlyService->>ProgressService: delegate linked player read
    ProgressService-->>ParentReadOnlyService: player profile/progress
    ParentReadOnlyService-->>Parent: read-only linked player data
```

Additional notes:

- `PlayerBadgeProgress` is unique per `(player, badgeCategory)`.
- Profile/progress reads initialize missing progress rows for every active badge category before returning data.
- `totalPoints` is derived on save as `legacyPoints + earnedPoints`.
- `earnedPoints` comes from summing `ChallengeAward.awardedPoints` by player and badge category.
- `currentLevelName` is recalculated from active `BadgeLevel` ranges ordered by `rankOrder`.
- The duplicate-handling logic in `ProgressService` only protects concurrent creation of `PlayerBadgeProgress` rows; it does not deduplicate challenge awards.

## Security Behavior Summary
- Authentication is session-based and uses Spring Security's `AuthenticationManager`, `DaoAuthenticationProvider`, `CustomUserDetailsService`, and `BCryptPasswordEncoder`.
- Login explicitly invalidates any existing HTTP session before creating a fresh authenticated session.
- Logout invalidates the current session if it exists and clears the `SecurityContextHolder`.
- CSRF is disabled in `SecurityConfig`.
- Unauthenticated protected requests receive HTTP `401 Unauthorized` via `HttpStatusEntryPoint`.
- CORS uses Spring's default configuration hooks (`cors(Customizer.withDefaults())`).
- `/h2-console/**` is denied unless `spring.h2.console.enabled` resolves to `true`; when enabled, frame options are relaxed to `sameOrigin`.
- Because `SecurityConfig` protects by path, and `ProgressService`/`ParentReadOnlyService` add important business checks, both layers matter for correct authorization.

## Local Run Instructions
1. From the repository root, start the backend with `.\mvnw.cmd spring-boot:run`. If the wrapper is unavailable in your environment, use `mvn spring-boot:run`.
2. The Spring Boot app listens on port `8080` by default.
3. Use `POST /api/v1/auth/login` to create a session, then send the session cookie on later API requests.
4. `GET /openapi.yaml` is public and can be used to inspect the API surface locally.
5. Leave `APP_H2_CONSOLE_ENABLED` unset (or `false`) for normal local work.
6. If you need the H2 console, set `APP_H2_CONSOLE_ENABLED=true` before startup; that enables `/h2-console/**`.
