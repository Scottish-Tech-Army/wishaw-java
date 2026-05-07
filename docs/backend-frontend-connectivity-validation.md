# Backend-Frontend Connectivity Validation Guide

This guide shows how to verify, demonstrate, and document that backend and frontend connectivity is healthy for the Wishaw platform.

It is designed for local development where:

- Backend runs on `http://localhost:8080`
- Frontend runs on `http://localhost:3000` or `http://localhost:5173`
- Authentication is session-cookie based (`JSESSIONID`)

Use this document when you want confidence that:

- CORS is configured correctly
- Login/logout/session behavior works through the frontend
- Role-based routes behave correctly
- The UI is reading real backend data rather than stale/mock data

## 1. Success Criteria (Definition of Done)

Connectivity is considered healthy only when all checks below pass:

1. Backend starts cleanly and serves `openapi.yaml`.
2. Frontend starts and can call backend endpoints without CORS failures.
3. Login from frontend creates a valid server session.
4. Frontend can fetch authenticated data after login.
5. Logout invalidates the session and protected calls fail after logout.
6. Role-restricted routes return expected `403` when using the wrong role.
7. Evidence (logs, network traces, screenshots, command output) is captured.

## 2. Repository Facts Used By This Guide

These facts come from current backend code and config:

- Backend port: `8080`
- Login endpoint: `POST /api/v1/auth/login`
- Auth check endpoint: `GET /api/v1/auth/me`
- Logout endpoint: `POST /api/v1/auth/logout`
- Player profile endpoint: `GET /api/v1/me/profile`
- Parent players endpoint: `GET /api/v1/parent/players`
- Leaderboard endpoint: `GET /api/v1/leaderboards/global`
- Allowed CORS origins by default: `http://localhost:3000,http://localhost:5173`
- CORS credentials are enabled (`allowCredentials=true`)

Seeded local users:

- `superadmin` / `admin123` (SUPER_ADMIN)
- `centreadmin` / `admin123` (CENTRE_ADMIN)
- `player1` / `player123` (PLAYER)
- `parent1` / `parent123` (PARENT)

## 3. Pre-Flight Checks

Run these before testing connectivity.

### 3.1 Runtime and toolchain

- Java is installed (`java -version`)
- Maven is installed (`mvn -version`)
- You are in repository root

### 3.2 Backend CORS configuration

Confirm the backend allows your frontend origin in `src/main/resources/application.properties`:

```properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

If your frontend runs elsewhere (for example `http://localhost:4200`), add it before testing.

### 3.3 Frontend credentials behavior

Because auth is session-cookie based, frontend HTTP client calls must include credentials.

- Fetch API: `credentials: "include"`
- Axios: `withCredentials: true`

Without this, login may appear to succeed but later requests will return `401`.

## 4. Start Backend and Frontend

### 4.1 Start backend (workstation-safe command)

```bash
mvn -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -DskipTests spring-boot:run
```

Expected backend signals:

- Spring Boot started on port `8080`
- Seed data loaded successfully

### 4.2 Start frontend

Run from your frontend repository (for this project, usually sibling repo `51a7db-tfg_hack_wishaw-node-main`).

Typical commands (choose what your frontend uses):

```bash
npm install
npm run dev
```

Expected frontend signals:

- Dev server starts on `3000` or `5173`
- Browser loads the login page

### 4.3 PowerShell notes for curl

In Windows PowerShell, `curl` may map to `Invoke-WebRequest` alias rather than `curl.exe`.

For command examples in this guide, prefer `curl.exe` in PowerShell:

```powershell
curl.exe -i http://localhost:8080/openapi.yaml
```

## 5. Layered Connectivity Test Plan

Run tests from simple to deep. Do not skip layers.

### Layer A: Backend reachability

From terminal:

```bash
curl -i http://localhost:8080/openapi.yaml
```

Pass condition:

- `HTTP/1.1 200`
- OpenAPI YAML content is returned

### Layer B: CORS preflight behavior

Test preflight for frontend origin `http://localhost:5173`:

```bash
curl -i -X OPTIONS http://localhost:8080/api/v1/auth/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST"
```

Pass condition:

- Response includes `Access-Control-Allow-Origin: http://localhost:5173`
- Response includes `Access-Control-Allow-Credentials: true`

If this fails, frontend cannot reliably authenticate.

PowerShell variant:

```powershell
curl.exe -i -X OPTIONS http://localhost:8080/api/v1/auth/login ^
  -H "Origin: http://localhost:5173" ^
  -H "Access-Control-Request-Method: POST"
```

### Layer C: Session lifecycle via API (ground truth)

1. Login as player and store cookies:

```bash
curl -i -c player-cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","password":"player123"}' \
  http://localhost:8080/api/v1/auth/login
```

2. Read current user:

```bash
curl -i -b player-cookies.txt http://localhost:8080/api/v1/auth/me
```

3. Read player profile:

```bash
curl -i -b player-cookies.txt http://localhost:8080/api/v1/me/profile
```

4. Logout:

```bash
curl -i -b player-cookies.txt -X POST http://localhost:8080/api/v1/auth/logout
```

5. Re-check protected route with same cookie jar:

```bash
curl -i -b player-cookies.txt http://localhost:8080/api/v1/me/profile
```

Pass conditions:

- Login returns `200` and sets session cookie
- Authenticated reads return `200`
- Logout returns `204`
- Protected read after logout returns `401` or `403`

PowerShell variant (session lifecycle sample):

```powershell
curl.exe -i -c player-cookies.txt -H "Content-Type: application/json" -d '{"username":"player1","password":"player123"}' http://localhost:8080/api/v1/auth/login
curl.exe -i -b player-cookies.txt http://localhost:8080/api/v1/auth/me
curl.exe -i -b player-cookies.txt http://localhost:8080/api/v1/me/profile
curl.exe -i -b player-cookies.txt -X POST http://localhost:8080/api/v1/auth/logout
curl.exe -i -b player-cookies.txt http://localhost:8080/api/v1/me/profile
```

### Layer D: Role guard checks (security sanity)

Use each seeded account and verify behavior:

1. `player1` can call `/api/v1/me/profile`.
2. `player1` cannot call `/api/v1/parent/players` (expect `403`).
3. `parent1` can call `/api/v1/parent/players`.
4. `parent1` cannot call `/api/v1/me/profile` (expect `403`).
5. Authenticated users can call `/api/v1/leaderboards/global`.

These checks prove backend auth and route mapping remain consistent with frontend expectations.

### Layer E: Frontend live flow checks

In browser DevTools (Network tab), verify:

1. Login request to `/api/v1/auth/login` succeeds.
2. `Set-Cookie` is returned on login.
3. Subsequent UI API calls include cookie (browser-managed).
4. No CORS errors appear in Console.
5. Role-specific screens fetch and render real data.
6. After logout, protected page refresh redirects or shows unauthorized state.

If UI shows data but Network tab has no backend calls, you are likely seeing mock or cached state.

## 6. End-to-End Showcase Script (Demo Runbook)

Use this sequence for a team demo or sign-off session.

1. Show backend terminal logs and successful startup.
2. Open `http://localhost:8080/openapi.yaml` to prove backend is reachable.
3. Open frontend app.
4. Log in as `player1` and open profile page.
5. In DevTools, show profile API response payload from backend.
6. Log out and show protected page now fails/redirects.
7. Log in as `parent1`, show linked players list.
8. Attempt a forbidden action/screen for wrong role and show `403` behavior.
9. Optionally show `curl` ground-truth responses for one positive and one negative case.

This proves connectivity, auth continuity, and authorization boundaries.

## 7. Evidence Capture Template

Collect and attach the following artifacts per run:

1. Timestamp and tester name.
2. Backend commit hash.
3. Frontend commit hash.
4. Backend startup log snippet.
5. Frontend startup log snippet.
6. Screenshot of successful login and one protected page.
7. Screenshot of Network tab request/response headers for login.
8. Screenshot or text of one expected `403` role-denied call.
9. `curl` outputs for:
   - `/openapi.yaml`
   - `/api/v1/auth/me` after login
   - protected route after logout

Suggested result summary format:

```text
Run ID: 2026-03-30-local-connectivity-01
Backend Commit: <hash>
Frontend Commit: <hash>
Backend URL: http://localhost:8080
Frontend URL: http://localhost:5173
Result: PASS
Notes: No CORS failures. Session persisted across profile and leaderboard calls. Logout correctly invalidated session.
```

## 8. Troubleshooting Decision Table

| Symptom | Likely Cause | How To Confirm | Fix |
| --- | --- | --- | --- |
| Login works but next call is `401` | Frontend not sending credentials | Check request config in frontend and DevTools request details | Enable `credentials: "include"` or `withCredentials: true` |
| Browser CORS error | Frontend origin not allowed | Check `Origin` and CORS headers in failed response | Add frontend origin to `app.cors.allowed-origins` |
| Preflight fails | OPTIONS blocked or misconfigured CORS | Run Layer B preflight curl command | Verify `CorsConfig` and allowed methods/headers |
| `403` on expected route | Wrong user role | Call `/api/v1/auth/me` and inspect role | Login with the correct seeded user |
| Frontend shows stale data | Cached/mock data path | Network tab shows no live backend call | Disable mock mode or force real API path |
| Backend unreachable from frontend | Wrong backend base URL | Inspect frontend environment config | Set frontend API base URL to `http://localhost:8080` |

## 9. Optional Automation Baseline

To reduce manual effort, add a small automated smoke suite (Postman/Newman, Playwright API tests, or similar) that runs these minimum assertions:

1. Backend OpenAPI reachable.
2. Login sets session cookie.
3. Authenticated call succeeds.
4. Logout invalidates session.
5. One role-allowed and one role-denied route behave as expected.

Manual checks are still valuable for browser cookie/CORS behavior and UI rendering, but these automated checks catch regressions quickly.

## 10. Final Go/No-Go Checklist

Mark each item before declaring connectivity healthy:

- Backend running and healthy
- Frontend running and using correct API base URL
- CORS preflight passes from active frontend origin
- Login works in browser
- Session persists across protected requests
- Logout invalidates protected access
- Role boundaries enforced (`403` where expected)
- Evidence captured and attached

If any item fails, treat connectivity as not yet verified.