# Bruno Curl Reference

Use these commands as copy-paste inputs for Bruno's curl import.

## Base Variables

```bash
BASE_URL=http://localhost:8080
API_BASE=$BASE_URL/api

PLAYER_EMAIL=player1@wymca.org
PLAYER_PASSWORD=player123

ADMIN_EMAIL=admin@wymca.org
ADMIN_PASSWORD=admin123
```

## Auth

### Player Login

```bash
curl -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "player1@wymca.org",
    "password": "player123"
  }'
```

### Admin Login

```bash
curl -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@wymca.org",
    "password": "admin123"
  }'
```

### Register

```bash
curl -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "new.player@wishaw.local",
    "password": "Password123!",
    "displayName": "New Player",
    "firstName": "New",
    "lastName": "Player"
  }'
```

### Refresh Token

```bash
curl -X POST "$API_BASE/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

### Current Session

```bash
curl -X GET "$API_BASE/auth/me" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Logout

```bash
curl -X POST "$API_BASE/auth/logout" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Profile

### Get Profile

```bash
curl -X GET "$API_BASE/profile" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Update Profile

```bash
curl -X PUT "$API_BASE/profile" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "displayName": "Player One",
    "firstName": "Alex",
    "lastName": "Smith",
    "bio": "Fortnite fanatic",
    "photoUrl": null,
    "overlayTemplate": null,
    "privacy": {
      "showInPublicList": true,
      "allowSocialSharing": true
    }
  }'
```

### Upload Profile Photo

```bash
curl -X POST "$API_BASE/profile/photo" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -F "photo=@C:/path/to/avatar.png"
```

### Set Overlay

```bash
curl -X POST "$API_BASE/profile/photo/overlay" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "template": "winner-frame"
  }'
```

## Catalogue

### Centres

```bash
curl -X GET "$API_BASE/centres" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Groups

```bash
curl -X GET "$API_BASE/groups?centreId=c1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Main Badges

```bash
curl -X GET "$API_BASE/badges/main" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Sub Badges

```bash
curl -X GET "$API_BASE/badges/sub?moduleId=m1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### User Badge Progress

```bash
curl -X GET "$API_BASE/badges/progress/u2" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Award Sub Badge

```bash
curl -X POST "$API_BASE/badges/award" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "u2",
    "subBadgeId": "sb1"
  }'
```

### Modules

```bash
curl -X GET "$API_BASE/modules" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Module Detail

```bash
curl -X GET "$API_BASE/modules/m1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Sports and Tournaments

### Sports

```bash
curl -X GET "$API_BASE/sports" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Create Sport

```bash
curl -X POST "$API_BASE/sports" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rocket League",
    "icon": "gamepad-2",
    "description": "Team-based football with cars.",
    "scoreFields": [
      { "key": "homeGoals", "label": "Home goals", "type": "number" },
      { "key": "awayGoals", "label": "Away goals", "type": "number" }
    ],
    "rankingPoints": {
      "win": 3,
      "draw": 1,
      "loss": 0
    }
  }'
```

### List Tournaments

```bash
curl -X GET "$API_BASE/tournaments" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Tournament Detail

```bash
curl -X GET "$API_BASE/tournaments/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Create Tournament

```bash
curl -X POST "$API_BASE/tournaments" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring Split Finals",
    "sportId": "s1",
    "description": "Centre-wide Rocket League finals.",
    "rules": "Best of three",
    "venue": "Wishaw YMCA Arena",
    "type": "TEAM",
    "status": "DRAFT",
    "startDate": "2026-04-20T18:00:00Z",
    "endDate": "2026-04-20T21:00:00Z",
    "regStartDate": "2026-04-01T09:00:00Z",
    "regEndDate": "2026-04-18T23:59:59Z",
    "capacity": 16,
    "teamMinSize": 3,
    "teamMaxSize": 5,
    "pointsWin": 3,
    "pointsDraw": 1,
    "pointsLoss": 0
  }'
```

### Join Tournament

```bash
curl -X POST "$API_BASE/tournaments/t1/join" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Leave Tournament

```bash
curl -X DELETE "$API_BASE/tournaments/t1/leave" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Tournament Participants

```bash
curl -X GET "$API_BASE/tournaments/t1/participants" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Matches and Teams

### Tournament Matches

```bash
curl -X GET "$API_BASE/matches/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Match Detail

```bash
curl -X GET "$API_BASE/matches/match1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Submit Score

```bash
curl -X POST "$API_BASE/matches/match1/score" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "winnerId": "u2",
    "fields": {
      "home": { "goals": 3 },
      "away": { "goals": 1 }
    },
    "summary": "Player One wins 3-1"
  }'
```

### Match Attendance

```bash
curl -X POST "$API_BASE/matches/match1/attendance" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "records": [
      { "userId": "u2", "attendance": "PRESENT" },
      { "userId": "u3", "attendance": "LATE" }
    ]
  }'
```

### Teams for Tournament

```bash
curl -X GET "$API_BASE/teams/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Create Team

```bash
curl -X POST "$API_BASE/teams" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wishaw Wolves",
    "tournamentId": "t1"
  }'
```

## Leaderboard and Stats

### Global Leaderboard

```bash
curl -X GET "$API_BASE/leaderboard/global" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Tournament Leaderboard

```bash
curl -X GET "$API_BASE/leaderboard/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Badge Catalogue

```bash
curl -X GET "$API_BASE/leaderboard/badges" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Assign Leaderboard Badge

```bash
curl -X POST "$API_BASE/leaderboard/badges/assign" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "badgeId": "lb1",
    "userId": "u2"
  }'
```

### Calories Log

```bash
curl -X POST "$API_BASE/leaderboard/calories" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "u2",
    "sportName": "Rocket League",
    "calories": 180
  }'
```

### Player Stats

```bash
curl -X GET "$API_BASE/stats/player/u2" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Admin Dashboard

```bash
curl -X GET "$API_BASE/stats/admin/dashboard" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>"
```

## Notifications

### List Notifications

```bash
curl -X GET "$API_BASE/notifications" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Mark Notification Read

```bash
curl -X PUT "$API_BASE/notifications/n1/read" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Mark All Notifications Read

```bash
curl -X PUT "$API_BASE/notifications/read-all" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Create Announcement

```bash
curl -X POST "$API_BASE/notifications/announcements" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Bracket published",
    "message": "Semi-final fixtures are now live.",
    "tournamentId": "t1"
  }'
```

### Tournament Announcements

```bash
curl -X GET "$API_BASE/notifications/announcements/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Share Data

```bash
curl -X GET "$API_BASE/notifications/share/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Gallery

```bash
curl -X GET "$API_BASE/notifications/gallery/tournament/t1" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Swagger and Health

### Health

```bash
curl -X GET "$BASE_URL/actuator/health"
```

### OpenAPI JSON

```bash
curl -X GET "$BASE_URL/v3/api-docs"
```

### Swagger UI

```bash
curl -X GET "$BASE_URL/swagger-ui.html"
```

## Notes

```bash
# Versioned routes are also supported.
# Replace /api/... with /api/v1/... if you want the versioned contract.

# For Bruno:
# 1. Create an environment with BASE_URL, API_BASE, ACCESS_TOKEN, ADMIN_ACCESS_TOKEN.
# 2. Import any command above via Bruno's curl importer.
# 3. Capture the login response tokens and store them as environment variables.
```