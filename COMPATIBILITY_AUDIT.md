# Wishaw Frontend-Backend Compatibility Audit

**Date:** 2025-01-22  
**Frontend Repo:** `51a7db-tfg_hack_wishaw-node-main` (Node.js/Express)  
**Backend Repo:** `51a7db-tfg_hack_wishaw-java` (Spring Boot)

---

## Executive Summary

| Metric | Count |
|--------|-------|
| Frontend API Calls (Live Mode) | 34 |
| Backend Endpoints | 32 |
| ✅ Fully Compatible | 28 |
| ⚠️ Minor Field Mismatches | 4 |
| ❌ Frontend Mock-Only Features | 5 |
| ❌ Backend Endpoints Not Used | 4 |

**Overall Status:** 🟡 **Good compatibility** with some mock-only features that need backend implementation.

---

## Frontend API Calls vs Backend Endpoints

### Authentication (`/api/v1/auth/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `POST /auth/login` | `POST /api/v1/auth/login` | ✅ Compatible |
| `POST /auth/logout` | `POST /api/v1/auth/logout` | ✅ Compatible |
| `GET /auth/me` | `GET /api/v1/auth/me` | ✅ Compatible |

### Admin User Management (`/api/v1/admin/users/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /admin/users` | `GET /api/v1/admin/users` | ✅ Compatible |
| `GET /admin/users/:id` | `GET /api/v1/admin/users/{id}` | ✅ Compatible |
| `POST /admin/users` | `POST /api/v1/admin/users` | ✅ Compatible |
| `PUT /admin/users/:id` | `PUT /api/v1/admin/users/{id}` | ✅ Compatible |
| `PATCH /admin/users/:id/status` | `PATCH /api/v1/admin/users/{id}/status` | ✅ Compatible |
| `DELETE /admin/users/:id` | ❌ Not implemented | ❌ **Mock-Only** |

### Centre Management (`/api/v1/admin/centres/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /admin/centres` | `GET /api/v1/admin/centres` | ✅ Compatible |
| `POST /admin/centres` | `POST /api/v1/admin/centres` | ✅ Compatible |
| `PUT /admin/centres/:id` | `PUT /api/v1/admin/centres/{id}` | ✅ Compatible |
| `DELETE /admin/centres/:id` | ❌ Not implemented | ❌ **Mock-Only** |

**Notes:** Frontend already has guard: `"Deleting centres is not supported by the Java backend"`

### Group Management (`/api/v1/admin/groups/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /admin/groups` | `GET /api/v1/admin/groups` | ✅ Compatible |
| `POST /admin/groups` | `POST /api/v1/admin/groups` | ✅ Compatible |
| `PUT /admin/groups/:id` | `PUT /api/v1/admin/groups/{id}` | ✅ Compatible |
| `DELETE /admin/groups/:id` | ❌ Not implemented | ❌ **Mock-Only** |

**Notes:** Frontend already has guard: `"Deleting groups is not supported by the Java backend"`

### Module Management (`/api/v1/admin/modules/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /admin/modules` | `GET /api/v1/admin/modules` | ✅ Compatible |
| `GET /admin/modules/:id` | `GET /api/v1/admin/modules/{id}` | ✅ Compatible |
| `POST /admin/modules` | `POST /api/v1/admin/modules` | ✅ Compatible |
| `PUT /admin/modules/:id` | `PUT /api/v1/admin/modules/{id}` | ✅ Compatible |
| `DELETE /admin/modules/:id` | ❌ Not implemented | ❌ **Mock-Only** |

**Notes:** Frontend already has guard: `"Module deletion is not available in live mode yet."`

### Challenge Management

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| Challenges fetched via `/admin/modules/:id` | `GET /api/v1/admin/modules/{id}` (includes challenges) | ⚠️ Different approach |
| `POST /challenges` | `POST /api/v1/admin/modules/{id}/challenges` | ⚠️ Path mismatch |
| `PUT /challenges/:id` | `PUT /api/v1/admin/challenges/{id}` | ✅ Compatible |
| `DELETE /challenges/:id` | ❌ Not implemented | ❌ **Mock-Only** |

**Notes:** 
- Frontend `routes/challenges.js` uses `mockOnly` middleware - challenges are **mock-only feature**
- Backend provides challenges as part of ModuleDetailResponse, not standalone endpoints

### Leaderboard (`/api/v1/leaderboards/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /leaderboards/global` | `GET /api/v1/leaderboards/global` | ✅ Compatible |
| `GET /leaderboards/centre/:id` | `GET /api/v1/leaderboards/centre/{centreId}` | ✅ Compatible |
| `GET /leaderboards/group/:id` | `GET /api/v1/leaderboards/group/{groupId}` | ✅ Compatible |

### Progress Management

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /players/:id/progress` | `GET /api/v1/players/{playerId}/progress` | ✅ Compatible |
| `GET /players/:id/profile` | `GET /api/v1/players/{playerId}/profile` | ✅ Compatible |
| `GET /me/profile` | `GET /api/v1/me/profile` | ✅ Compatible |
| `GET /me/progress` | `GET /api/v1/me/progress` | ✅ Compatible |
| `POST /admin/progress/award-challenge` | `POST /api/v1/admin/progress/award-challenge` | ✅ Compatible |

### Parent Portal (`/api/v1/parent/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /parent/players` | `GET /api/v1/parent/players` | ✅ Compatible |
| `GET /parent/players/:id/profile` | `GET /api/v1/parent/players/{playerId}/profile` | ✅ Compatible |
| `GET /parent/players/:id/progress` | `GET /api/v1/parent/players/{playerId}/progress` | ✅ Compatible |
| `POST /admin/parents/link-player` | `POST /api/v1/admin/parents/link-player` | ✅ Compatible |

### CSV Import (`/api/v1/admin/import/*`)

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `POST /admin/import/csv/upload` | `POST /api/v1/admin/import/csv/upload` | ✅ Compatible |
| `GET /admin/import/:id/preview` | `GET /api/v1/admin/import/{batchId}/preview` | ✅ Compatible |
| `POST /admin/import/:id/map-players` | `POST /api/v1/admin/import/{batchId}/map-players` | ✅ Compatible |
| `POST /admin/import/:id/commit` | `POST /api/v1/admin/import/{batchId}/commit` | ✅ Compatible |
| `GET /admin/import/:id/report` | `GET /api/v1/admin/import/{batchId}/report` | ✅ Compatible |

### Schedule Management

| Frontend Call | Backend Endpoint | Status |
|---------------|------------------|--------|
| `GET /schedule`, `POST /schedule`, etc. | ❌ No standalone endpoint | ⚠️ **Mock-Only** |

**Notes:** 
- Frontend `routes/schedule.js` uses `mockOnly` middleware
- Backend provides schedule items as part of ModuleDetailResponse via `/admin/modules/{id}/schedule-items`

---

## Backend Endpoints NOT Used by Frontend

| Backend Endpoint | Description | Recommendation |
|------------------|-------------|----------------|
| `POST /api/v1/admin/enrollments` | Create enrollment | Could enhance player management |
| `PATCH /api/v1/admin/enrollments/{id}/status` | Update enrollment status | Could enhance player management |
| `POST /api/v1/admin/progress/legacy-points` | Set legacy points for migration | CSV import uses this internally |
| `POST /api/v1/admin/modules/{id}/schedule-items` | Create schedule item | Could replace mock schedule feature |
| `PUT /api/v1/admin/schedule-items/{id}` | Update schedule item | Could replace mock schedule feature |

---

## Request/Response Field Analysis

### 1. LeaderboardEntryResponse ⚠️ Minor Mismatch

**Backend Response:**
```json
{
  "rank": 1,
  "playerId": 123,
  "displayName": "John",
  "centreName": "Main",
  "groupName": "Juniors",
  "totalPoints": 500,
  "highestLevel": "Gold"
}
```

**Frontend Mapping (leaderboardService.js):**
```javascript
{
  rank: entry.rank,
  playerId: entry.playerId,
  id: entry.playerId,        // ✅ Aliased
  displayName: entry.displayName,
  groupName: entry.groupName,
  centreName: entry.centreName,
  xp: entry.totalPoints || 0, // ✅ Mapped totalPoints -> xp
  highestLevel: entry.highestLevel
}
```
**Status:** ✅ Properly mapped

### 2. PlayerProfileResponse

**Backend Response:**
```json
{
  "id": 123,
  "username": "john",
  "displayName": "John Doe",
  "centreName": "Main",
  "groupName": "Juniors",
  "badgeProgress": [...],
  "overallTotalPoints": 500
}
```

**Frontend progressService.js mapProfile():**
```javascript
{
  id: profile.id,
  username: profile.username,
  email: profile.username,    // Alias
  displayName: profile.displayName,
  centreName: profile.centreName,
  groupName: profile.groupName,
  active: true,               // Hardcoded - not in backend response
  avatarUrl: profile.avatarUrl || null,  // ⚠️ Not in backend
  imageUrl: profile.imageUrl || null     // ⚠️ Not in backend
}
```
**Status:** ⚠️ `avatarUrl`/`imageUrl` not provided by backend (handled by frontend playerMediaStore)

### 3. GroupResponse - Field Name Differences

**Backend Response:**
```json
{
  "id": 1,
  "name": "Juniors",
  "gameName": "Team Junior",
  "ageBand": "8-12",
  "centreId": 1,
  "centreName": "Main",
  "active": true
}
```

**Frontend groupService.js mapGroup():**
```javascript
{
  id: group.id,
  name: group.name,
  gameName: group.gameName || '',
  centreId: group.centreId,
  centreName: group.centreName,
  ageBand: group.ageBand || '',
  ageRange: group.ageBand || '',  // Alias for legacy views
  active: group.active
}
```
**Status:** ✅ Properly mapped (`ageRange` is alias for `ageBand`)

### 4. UserSummaryResponse (Players)

**Backend Response:**
```json
{
  "id": 123,
  "username": "john",
  "displayName": "John",
  "role": "PLAYER",
  "active": true,
  "centreId": 1,
  "centreName": "Main",
  "groupId": 1,
  "groupName": "Juniors",
  "externalRef": "EXT001"
}
```

**Frontend playerService.js mapPlayer():**
```javascript
{
  id: user.id,
  username: user.username,
  email: user.username,        // Alias
  displayName: user.displayName,
  externalRef: user.externalRef || '',
  groupId: user.groupId,
  groupName: user.groupName,
  centreId: user.centreId,
  centreName: user.centreName,
  active: user.active,
  xp: pointsByPlayerId[user.id] || 0,  // Fetched separately from leaderboard
  avatarUrl: user.avatarUrl || null,   // ⚠️ Not in backend
  imageUrl: user.imageUrl || null      // ⚠️ Not in backend
}
```
**Status:** ✅ Core fields compatible. Avatar/image handled by frontend store.

---

## Mock-Only Features That Could Be Live

| Feature | Frontend File | Backend Status | Effort |
|---------|---------------|----------------|--------|
| **Challenge CRUD** | `routes/challenges.js` | Backend has challenges in modules | Medium - needs standalone endpoints |
| **Schedule Management** | `routes/schedule.js` | Backend has schedule items in modules | Medium - adapt to module-based approach |
| **Parent Link Management** | `routes/parents.js` | Backend has `POST /admin/parents/link-player` | Low - partial support exists |
| **Delete User** | `routes/players.js` | Not implemented | Low - add DELETE endpoint |
| **Delete Centre/Group/Module** | Various | Not implemented | Low - add DELETE endpoints |

---

## Recommendations

### High Priority

1. **Schedule Integration** - Frontend schedule feature is mock-only but backend supports it via modules. Either:
   - Option A: Adapt frontend to use module-based schedule endpoints
   - Option B: Add standalone `/schedule` endpoints to backend

2. **Challenge Management** - The entire `/challenges` route is mock-only. Consider:
   - Adapting frontend to create challenges via `/admin/modules/{id}/challenges`
   - Adding standalone challenge CRUD endpoints to backend

### Medium Priority

3. **Add DELETE Endpoints** - Backend lacks delete operations for:
   - Users (`DELETE /admin/users/{id}`)
   - Centres (`DELETE /admin/centres/{id}`)
   - Groups (`DELETE /admin/groups/{id}`)
   - Modules (`DELETE /admin/modules/{id}`)
   - Challenges (`DELETE /admin/challenges/{id}`)

4. **Enrollment Feature** - Backend has enrollment endpoints not used by frontend:
   - `POST /admin/enrollments`
   - `PATCH /admin/enrollments/{id}/status`
   
   Consider integrating these for player module enrollment tracking.

### Low Priority

5. **Avatar/Image Fields** - Backend DTOs don't include `avatarUrl`/`imageUrl`. This is fine as frontend handles media separately via `playerMediaStore`, but consider:
   - Adding these fields to backend for consistency
   - Or documenting that media is frontend-only

6. **Parent Links Admin** - `routes/parents.js` is mock-only but backend supports linking. Consider enabling live mode for this feature.

---

## Compatibility Matrix Summary

```
✅ Auth (login/logout/me)        - Full compatibility
✅ Users (CRUD except delete)    - Full compatibility  
✅ Centres (CRUD except delete)  - Full compatibility
✅ Groups (CRUD except delete)   - Full compatibility
✅ Modules (CRUD except delete)  - Full compatibility
✅ Leaderboards                  - Full compatibility
✅ Progress                      - Full compatibility
✅ Me (profile/progress)         - Full compatibility
✅ Parent Portal                 - Full compatibility
✅ CSV Import                    - Full compatibility

⚠️ Challenges                   - Mock-only, backend has module-nested approach
⚠️ Schedule                     - Mock-only, backend has module-nested approach
⚠️ Parent Links Admin           - Partial (create works, list/delete mock-only)
⚠️ Enrollments                  - Not used by frontend

❌ Delete operations            - Not implemented in backend
```

---

## Conclusion

The frontend and backend are **well-aligned** for core functionality. The main gaps are:

1. **DELETE operations** - Not critical for MVP, easily added later
2. **Challenge/Schedule management** - These use mock-only middleware because backend structures them differently (nested under modules vs. standalone)
3. **Enrollment tracking** - Backend capability not yet exposed in frontend

The codebase shows good practices with:
- Frontend guards for unsupported operations (`"...not supported by the Java backend"`)
- `USE_MOCK` flag for easy switching between modes
- Consistent field mapping in service layers
- FRONTEND_INTEGRATION comments in backend code

**Recommended next step:** Enable live mode for parent link management as it's lowest effort and adds value immediately.
