# Wishaw YMCA Esports Academy - Backend API

A Java Spring Boot REST API backend for the **Wishaw YMCA Esports Academy** Progressive Web App. This system replaces manual spreadsheet tracking with an automated gamified badge and XP progression system for youth esports participants.

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Core language |
| Spring Boot | 3.5.11 | Web framework |
| Spring Security | - | Authentication & authorization |
| Spring Data JPA | - | Database ORM |
| MySQL | 8.x | Relational database |
| JWT (jjwt) | 0.12.6 | Token-based authentication |
| Lombok | - | Boilerplate reduction |
| Maven | - | Build & dependency management |

## Getting Started

### Prerequisites

- JDK 17+
- MySQL 8.x (or use Docker)
- Maven 3.8+

### Database Setup

```sql
CREATE DATABASE wishaw_db;
```

### Configuration

The default configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/wishaw_db
spring.datasource.username=root
spring.datasource.password=root
server.port=8080
```

### Running the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

### Running with Docker

```bash
docker-compose up -d
```

## Database Schema

### Entity Relationship Overview

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Centre    │────<│   Student   │>────│    Team     │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ┌──────────────┐ ┌─────────┐ ┌──────────────────┐
     │StudentSubBadge│ │ XpEvent │ │EvidenceSubmission│
     └──────────────┘ └─────────┘ └──────────────────┘
              │
              ▼
     ┌──────────────┐     ┌─────────────┐
     │   SubBadge   │────>│  MainBadge  │
     └──────────────┘     └─────────────┘
              │
              ▼
     ┌──────────────┐     ┌─────────────┐     ┌─────────────┐
     │    Module    │────<│   Session   │────<│  Resource   │
     └──────────────┘     └─────────────┘     └─────────────┘
```

### Core Entities

| Entity | Description |
|--------|-------------|
| **Student** | User account (student or admin role) with XP, level, and profile data |
| **Centre** | Physical location/hub where students are based |
| **Team** | Group of students (competitive team) |
| **MainBadge** | Top-level badge category (e.g., "Communication", "Leadership") |
| **SubBadge** | Individual badge within a main badge, worth XP |
| **BadgeLevel** | Progression levels (Bronze, Silver, Gold, etc.) based on XP thresholds |
| **Module** | Educational module containing sessions and linked sub-badges |
| **Session** | Individual session/lesson within a module |
| **Resource** | Uploaded file/resource attached to a session |
| **StudentSubBadge** | Join table tracking which sub-badges each student has earned |
| **XpEvent** | Audit log of XP awards with timestamps |
| **EvidenceSubmission** | Student-submitted evidence for badge claims (pending admin review) |

## API Reference

Base URL: `/api/v1`

### Authentication (`/auth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/login` | Login with username/password, returns JWT | No |
| DELETE | `/auth/session` | Logout (invalidate session) | Yes |
| POST | `/auth/forgot-username` | Retrieve username via email | No |
| POST | `/auth/forgot-password` | Get password hint | No |

### Student Endpoints (`/students`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/students/{studentId}/dashboard` | Dashboard summary (XP, level, recent activity) | Yes |
| GET | `/students/{studentId}/badges` | Full badge catalogue with progress | Yes |
| GET | `/students/{studentId}/modules` | Module progress list | Yes |
| GET | `/students/{studentId}/profile` | Student profile details | Yes |
| PATCH | `/students/{studentId}/profile` | Update profile (bio, gamertag, etc.) | Yes |
| POST | `/students/{studentId}/change-password` | Change password | Yes |
| POST | `/students/{studentId}/avatar` | Upload avatar (multipart) | Yes |
| GET | `/students/{studentId}/evidence` | List evidence submissions | Yes |
| POST | `/students/{studentId}/evidence` | Submit evidence (multipart) | Yes |
| GET | `/students/by-username/{username}/public-profile` | Public profile view | No |
| GET | `/students/by-username/{username}/badges/summary` | Public badge summary | No |

### Teams (`/teams`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/teams` | List all teams with summary info | Yes |
| GET | `/teams/{teamSlug}` | Team detail with all members | Yes |

### Leaderboard (`/leaderboard`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/leaderboard` | Paginated leaderboard | Yes |

Query params: `period` (ALL_TIME, THIS_WEEK, THIS_MONTH), `sortBy` (XP, BADGES), `page`, `size`

### Admin Endpoints (`/admin`) - Requires ROLE_ADMIN

#### Dashboard & Activity
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/dashboard` | Admin dashboard stats |
| GET | `/admin/activity` | Recent activity feed |

#### Badge Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/badges` | Full badge catalogue (admin view) |
| PUT | `/admin/badge-levels` | Update badge level thresholds |

#### Module Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/modules` | List all modules |
| GET | `/admin/modules/{moduleId}` | Get module details |
| POST | `/admin/modules` | Create new module |
| PUT | `/admin/modules/{moduleId}` | Update module |
| DELETE | `/admin/modules/{moduleId}` | Archive module |

#### Sub-Badge Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/modules/{moduleId}/sub-badges` | Add sub-badge to module |
| PUT | `/admin/modules/{moduleId}/sub-badges/{subBadgeId}` | Update sub-badge |
| DELETE | `/admin/modules/{moduleId}/sub-badges/{subBadgeId}` | Remove sub-badge |
| PUT | `/admin/modules/{moduleId}/sub-badges/reorder` | Reorder sub-badges |

#### Session Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/modules/{moduleId}/sessions` | Add session |
| PUT | `/admin/modules/{moduleId}/sessions/{sessionId}` | Update session |
| DELETE | `/admin/modules/{moduleId}/sessions/{sessionId}` | Remove session |

#### Resource Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/modules/{moduleId}/sessions/{sessionId}/resources` | Upload resource (multipart) |
| DELETE | `/admin/modules/{moduleId}/sessions/{sessionId}/resources/{resourceId}` | Remove resource |

#### Group Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/groups` | List all groups (teams) |

#### User Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/users` | List all student users |
| GET | `/admin/users/{studentId}` | Get user details |
| POST | `/admin/users` | Create new user |
| POST | `/admin/users/{studentId}/award-xp` | Award XP to user |
| POST | `/admin/users/{studentId}/award-badge/{subBadgeId}` | Award sub-badge to user |

#### Evidence Review
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/evidence` | List pending evidence submissions |
| GET | `/admin/evidence/all` | List all evidence submissions |
| POST | `/admin/evidence/{submissionId}/approve` | Approve evidence |
| POST | `/admin/evidence/{submissionId}/reject` | Reject evidence |

## Authentication

The API uses **JWT (JSON Web Tokens)** for authentication.

### Login Flow

1. POST to `/api/v1/auth/login` with `{ "username": "...", "password": "..." }`
2. Receive response with JWT token and user details
3. Include token in subsequent requests: `Authorization: Bearer <token>`

### Token Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `app.jwt.expiration-ms` | 86400000 | Token validity (24 hours) |

### Role-Based Access

- **ROLE_STUDENT**: Access to student endpoints
- **ROLE_ADMIN**: Access to all endpoints including `/admin/*`

## Project Structure

```
src/main/java/org/scottishtecharmy/wishaw_java/
├── WishawJavaApplication.java      # Main application entry
├── config/
│   ├── DataSeeder.java             # Initial data seeding
│   └── SecurityConfig.java         # Security configuration
├── controller/
│   ├── AdminController.java        # Admin endpoints
│   ├── AuthController.java         # Authentication
│   ├── LeaderboardController.java  # Leaderboard
│   ├── StudentController.java      # Student endpoints
│   └── TeamController.java         # Team endpoints
├── dto/                            # Data Transfer Objects
├── model/                          # JPA Entities
├── repository/                     # Spring Data repositories
├── security/                       # JWT & security utilities
└── service/                        # Business logic services
```

## Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package -DskipTests
java -jar target/wishaw-java-0.0.1-SNAPSHOT.jar
```

## License

This project was developed for Wishaw YMCA by Scottish Tech Army volunteers.
