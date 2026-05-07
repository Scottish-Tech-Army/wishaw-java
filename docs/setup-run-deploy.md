# Wishaw Platform Setup, Run, and Deploy

This guide is the fastest way for a new developer or deployer to get the Wishaw YMCA Esports platform running.

It covers:

- local setup
- local run with seeded demo data
- local run with PostgreSQL
- production builds
- backend container and Kubernetes deployment
- frontend static deployment

## 1. What Is In Scope

The platform is split into two sibling repositories:

- `51a7db-tfg_hack_wishaw-java` - Spring Boot API
- `51a7db-tfg_hack_wishaw-react` - React/Vite frontend

Local development uses two processes:

- backend on `http://localhost:8080`
- frontend on `http://127.0.0.1:3000`

## 2. Prerequisites

Install these before you start:

- Git
- Java 17 or newer
- Maven 3.9 or newer
- Node.js 20 or newer
- npm 10 or newer

For deployment you also need:

- Docker, if you want to build the backend image locally
- kubectl, if you want to deploy the API to Kubernetes
- PostgreSQL, for persistent local or deployed environments

## 3. Local Development Setup

Open two terminals.

If both repos sit under the same parent folder, start each command from that parent folder.

### Backend Terminal

```powershell
cd .\51a7db-tfg_hack_wishaw-java
mvn spring-boot:run
```

What this does:

- starts the API on port `8080`
- uses the default `h2` profile
- creates the schema in an in-memory H2 database
- seeds demo data automatically

### Frontend Terminal

```powershell
cd .\51a7db-tfg_hack_wishaw-react
npm ci
npm run dev
```

What this does:

- installs the frontend dependencies
- starts Vite on `http://127.0.0.1:3000`
- proxies `/api` to `http://localhost:8080`

The frontend already includes local development defaults in `.env.local`, so you usually do not need to change anything for local use.

## 4. Local Verification Checklist

Open these URLs after both services are running:

- frontend: `http://127.0.0.1:3000`
- backend health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`

Use these demo accounts:

- admin: `admin@wymca.org` / `admin123`
- player: `player1@wymca.org` / `player123`
- player: `player2@wymca.org` / `player123`

H2 console settings:

- JDBC URL: `jdbc:h2:mem:wishaw;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- User Name: `sa`
- Password: leave blank

## 5. Run The Backend With PostgreSQL

Use this when you need persistent local data.

### Create a Database

Create a PostgreSQL database named `wishaw` and a user that can connect to it.

### Set Environment Variables

PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE='postgres'
$env:DB_URL='jdbc:postgresql://localhost:5432/wishaw'
$env:DB_USERNAME='wishaw'
$env:DB_PASSWORD='wishaw'
$env:JWT_SECRET='replace-with-a-strong-secret-at-least-32-characters'
```

Then run:

```powershell
mvn spring-boot:run
```

Notes:

- the `postgres` profile uses `ddl-auto=update`
- local seeding stays enabled unless you explicitly set `APP_SEED_ENABLED=false`
- the frontend can keep using the same `npm run dev` command

## 6. Backend Environment Variables

These are the main runtime variables for the API:

| Variable | Purpose | Default |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Selects `h2`, `postgres`, or `k8s` | `h2` |
| `SERVER_PORT` | Backend port | `8080` |
| `UI_ORIGIN` | Allowed frontend origin for CORS | `http://localhost:3000` |
| `JWT_SECRET` | JWT signing secret | insecure local fallback |
| `JWT_ACCESS_TOKEN_MINUTES` | Access token lifetime | `60` |
| `JWT_REFRESH_TOKEN_DAYS` | Refresh token lifetime | `7` |
| `APP_SEED_ENABLED` | Enable demo data seeding | `true` locally, `false` in `k8s` profile |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/wishaw` in `postgres` profile |
| `DB_USERNAME` | PostgreSQL username | `wishaw` in `postgres` profile |
| `DB_PASSWORD` | PostgreSQL password | `wishaw` in `postgres` profile |

## 7. Production Build

### Backend

From the backend repo:

```powershell
mvn clean package
```

This creates:

- `target/wishaw-java-0.0.1-SNAPSHOT.jar`

Important:

- stop any running jar before running `clean package`
- on Windows, rebuilding while the jar is running can leave stale or locked files in `target`

### Frontend

From the frontend repo:

```powershell
# from the frontend repo
$env:VITE_API_BASE_URL='https://your-api-host.example.com/api'
$env:VITE_USE_MOCKS='false'
npm ci
npm run build
```

This creates:

- `dist/`

The `dist/` folder is the production-ready static site.

## 8. Backend Container Build

The backend repo now includes a `Dockerfile` so the Kubernetes manifests can be used directly.

### Build The Jar First

```powershell
# from the backend repo
mvn clean package
```

### Build The Image

```powershell
docker build -t wishaw-java:latest .
```

### Optional Tag For A Registry

```powershell
docker tag wishaw-java:latest your-registry.example.com/wishaw-java:latest
docker push your-registry.example.com/wishaw-java:latest
```

If you push to a registry, update `k8s/deployment.yaml` to point to that image.

## 9. Deploy The Backend To Kubernetes

The backend repo already contains these manifests:

- `k8s/configmap.yaml`
- `k8s/secret.example.yaml`
- `k8s/deployment.yaml`
- `k8s/service.yaml`
- `k8s/ingress.yaml`
- `k8s/hpa.yaml`

### Step 1: Review Runtime Values

Default Kubernetes assumptions in the current manifests:

- profile: `k8s`
- internal app port: `8080`
- service port: `80`
- ingress host: `wishaw-api.local`
- database URL from config map: `jdbc:postgresql://postgres:5432/wishaw`

### Step 2: Create A Real Secret Manifest

Copy `k8s/secret.example.yaml` to a real secret file and replace the placeholders.

Minimum values to change:

- `JWT_SECRET`
- `DB_USERNAME`
- `DB_PASSWORD`

### Step 3: If Needed, Update The Config Map

Edit `k8s/configmap.yaml` if your frontend URL or database host differs.

Important fields:

- `UI_ORIGIN`
- `DB_URL`

### Step 4: Set The Image Name

Edit `k8s/deployment.yaml` if your registry image is not `wishaw-java:latest`.

### Step 5: Apply The Manifests

```powershell
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

If your secret file has a different name, use that instead of `k8s/secret.yaml`.

### Step 6: Verify The Deployment

```powershell
kubectl get pods
kubectl get svc
kubectl get ingress
kubectl logs deployment/wishaw-java
```

Check these endpoints after ingress or port-forwarding is available:

- `/actuator/health`
- `/swagger-ui.html`

## 10. Deploy The Frontend

The frontend is a static Vite build. The current repo does not include a frontend Kubernetes manifest or container image, so the simplest deployment path is static hosting.

Recommended process:

1. Set `VITE_API_BASE_URL` to your real public backend URL ending in `/api`.
2. Run `npm run build`.
3. Publish the generated `dist/` folder to your hosting platform.

Good targets include:

- Nginx
- Azure Static Web Apps
- Netlify
- Vercel
- S3 plus CloudFront

Important:

- set the backend `UI_ORIGIN` to the real frontend URL
- keep `VITE_USE_MOCKS=false` in deployed environments

## 11. Troubleshooting

### Frontend Shows "Failed to fetch"

Check these first:

- backend is running on `http://localhost:8080`
- frontend is running on `http://127.0.0.1:3000`
- local frontend is still using `/api` and proxying to the backend

### Swagger Works But UI Login Fails

Usually one of these is wrong:

- frontend started before backend and the backend is now on a different port
- `VITE_DEV_PROXY_TARGET` was changed from `http://localhost:8080`
- browser is calling the API directly instead of through `/api`

### Maven Package Fails Because The Jar Is Locked

Stop the running jar or `spring-boot:run` process, then rerun:

```powershell
mvn clean package
```

### H2 Console Cannot Connect

Make sure the backend is still running with the `h2` profile and use:

- JDBC URL: `jdbc:h2:mem:wishaw;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- username: `sa`
- empty password

### Kubernetes Pods Start But Fail Readiness

Check:

- the image in `k8s/deployment.yaml` exists and is pullable
- `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are valid
- `JWT_SECRET` is set
- PostgreSQL is reachable from the cluster

## 12. Useful Related Docs

- `README.md`
- `docs/local-db-access.md`
- `docs/bruno-curl-reference.md`
- `docs/spreadsheet-to-h2-migration.md`