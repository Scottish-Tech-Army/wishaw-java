# Wishaw Java Backend

Java Spring Boot project for Wishaw YMCA Esports app

---

## Prerequisites

- **Java 17** or later — [Download](https://adoptium.net/)
- **Maven 3.8+** (optional — the project includes a Maven Wrapper)

> You do **not** need to install Maven separately. The included `mvnw` / `mvnw.cmd` wrapper will download the correct version automatically.

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd 51a7db-tfg_hack_wishaw-java
```

### 2. Build the project

**Windows (cmd)**
```cmd
mvnw.cmd clean install
```

**Linux / macOS**
```bash
./mvnw clean install
```

This will:
- Download all Maven dependencies
- Compile the source code
- Run the tests
- Package the application as a JAR in `target/`

> To skip tests during the build, add `-DskipTests`:
> ```cmd
> mvnw.cmd clean install -DskipTests
> ```

### 3. Run the application

**Windows (cmd)**
```cmd
mvnw.cmd spring-boot:run
```

**Linux / macOS**
```bash
./mvnw spring-boot:run
```

Or run the packaged JAR directly:

```cmd
java -jar target/wishaw-java-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**.

---

## Key Endpoints

| Endpoint | Description |
|---|---|
| `http://localhost:8080` | Application root |
| `http://localhost:8080/swagger-ui.html` | Swagger UI (API documentation) |
| `http://localhost:8080/api-docs` | OpenAPI JSON spec |
| `http://localhost:8080/h2-console` | H2 Database console (dev only) |

### H2 Console Connection Details

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## Running Tests

```cmd
mvnw.cmd test
```

Or on Linux / macOS:

```bash
./mvnw test
```

---

## Project Structure

```
src/
├── main/
│   ├── java/org/scottishtecharmy/wishaw_java/
│   │   ├── WishawJavaApplication.java   # Application entry point
│   │   ├── auth/                        # Authentication & JWT
│   │   ├── badge/                       # Badges & Sub-badges
│   │   ├── centre/                      # Centres
│   │   ├── config/                      # Security, CORS, JWT, OpenAPI config
│   │   ├── group/                       # Game groups
│   │   ├── leaderboard/                 # Leaderboard
│   │   ├── level/                       # Levels
│   │   ├── module/                      # Modules
│   │   ├── progress/                    # Progress tracking
│   │   └── user/                        # User management
│   └── resources/
│       ├── application.properties       # App configuration
│       ├── schema.sql                   # Database schema (auto-loaded)
│       └── data.sql                     # Seed data (auto-loaded)
└── test/                                # Unit & integration tests
```

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.5**
- **Spring Data JPA / JDBC**
- **Spring Security** with JWT authentication
- **H2** in-memory database (dev/demo — swap to PostgreSQL for production)
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for Swagger UI
- **Maven** build tool
