# Wishaw Frontend-Backend Gaps & TODO List

**Generated:** 2025-01-22  
**Source:** COMPATIBILITY_AUDIT.md analysis

---

## Executive Summary

The frontend and backend have **~82% compatibility** (28/34 endpoints fully working). The gaps fall into three categories:

| Category | Count | Impact |
|----------|-------|--------|
| Missing DELETE operations | 5 | Users can't delete resources via UI |
| Mock-only features (challenges, schedule) | 2 | Features work but don't persist to backend |
| Unused backend endpoints | 4 | Existing functionality not exposed |

**Critical Gap:** No DELETE operations exist in the backend. Users can CREATE and UPDATE but never DELETE.

**Key Insight:** Challenges and Schedule features use mock-only middleware because the backend nests them under modules rather than providing standalone endpoints.

---

## Prioritized TODO List

### P1 - Critical (Block core workflows)

| ID | Gap | Impact | Fix Approach | Effort |
|----|-----|--------|--------------|--------|
| P1-1 | **Challenge CRUD is mock-only** | Admins can't manage challenges that persist | Frontend: adapt to use `POST /admin/modules/{id}/challenges` and `PUT /admin/challenges/{id}` | Medium |
| P1-2 | **Schedule management is mock-only** | Session schedules don't persist | Frontend: adapt to module-based schedule endpoints OR Backend: add standalone `/schedule` endpoints | Medium |

### P2 - Important (Limit admin capabilities)

| ID | Gap | Impact | Fix Approach | Effort |
|----|-----|--------|--------------|--------|
| P2-1 | **DELETE /admin/users/{id}** missing | Can't remove users from system | Backend: Add UserController.deleteUser() | Small |
| P2-2 | **DELETE /admin/centres/{id}** missing | Can't remove centres | Backend: Add CentreController.deleteCentre() | Small |
| P2-3 | **DELETE /admin/groups/{id}** missing | Can't remove groups | Backend: Add GroupController.deleteGroup() | Small |
| P2-4 | **DELETE /admin/modules/{id}** missing | Can't remove modules | Backend: Add ModuleController.deleteModule() | Small |
| P2-5 | **DELETE /admin/challenges/{id}** missing | Can't remove challenges | Backend: Add ChallengeController.deleteChallenge() | Small |
| P2-6 | **Parent links list/delete mock-only** | Can't view or remove parent-child links | Backend: Add GET/DELETE parent links endpoints | Small |

### P3 - Nice-to-Have (Enhancements)

| ID | Gap | Impact | Fix Approach | Effort |
|----|-----|--------|--------------|--------|
| P3-1 | **Enrollments not used** | Player-module enrollment tracking not in UI | Frontend: Add enrollment management UI | Medium |
| P3-2 | **Legacy points endpoint unused** | Migration feature not exposed | Frontend: Add legacy points import option | Small |
| P3-3 | **Avatar/image fields missing** | Backend doesn't store player images | Backend: Add fields OR document as frontend-only | Small |

---

## Detailed Gap Analysis

### GAP-1: Challenge Management (Mock-Only)

**What's Broken:**  
- `routes/challenges.js` uses `mockOnly` middleware
- All challenge CRUD operations go to mock store, not backend
- Backend has challenges nested under modules (`GET /admin/modules/{id}` returns challenges)

**Impact:**  
- Challenge edits don't persist after server restart
- No real challenge data in production

**Suggested Fix:**
```javascript
// Frontend: challenges.js - adapt POST to use module-nested endpoint
// Instead of: POST /challenges
// Use: POST /admin/modules/{moduleId}/challenges

// For updates, backend already has:
// PUT /admin/challenges/{id}
```

**Effort:** Medium (need to refactor frontend challenge service)

---

### GAP-2: Schedule Management (Mock-Only)

**What's Broken:**  
- `routes/schedule.js` uses `mockOnly` middleware
- Backend provides schedule via `/admin/modules/{id}/schedule-items`
- Frontend expects standalone `/schedule` endpoints

**Impact:**  
- Schedule data doesn't persist
- No production schedule functionality

**Suggested Fix (Option A - Frontend adaptation):**
```javascript
// Adapt frontend to:
// - POST /admin/modules/{id}/schedule-items
// - PUT /admin/schedule-items/{id}
```

**Suggested Fix (Option B - Backend standalone):**
```java
// Add to backend:
// GET /api/v1/admin/schedule
// POST /api/v1/admin/schedule
// PUT /api/v1/admin/schedule/{id}
// DELETE /api/v1/admin/schedule/{id}
```

**Effort:** Medium

---

### GAP-3: DELETE Operations Missing

**What's Broken:**  
Backend lacks DELETE endpoints for:
- Users, Centres, Groups, Modules, Challenges

**Impact:**  
- Admins can't clean up test data
- Can't remove inactive resources
- Frontend shows delete buttons that don't work (mock-only)

**Frontend Guards Already In Place:**
```javascript
// routes/players.js
"Deleting users is not supported by the Java backend"

// routes/centres.js  
"Deleting centres is not supported by the Java backend"

// routes/groups.js
"Deleting groups is not supported by the Java backend"
```

**Suggested Fix (Backend):**
```java
// Each controller needs:
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

**Effort:** Small per endpoint (5 endpoints = ~2-3 hours total)

---

### GAP-4: Parent Links Admin (Partial Support)

**What's Broken:**  
- `POST /admin/parents/link-player` works in live mode
- List and delete operations are mock-only
- Parents can't see their linked players via admin UI

**Impact:**  
- Can link players to parents
- Can't list existing links or remove them

**Suggested Fix:**
```java
// Backend needs:
GET /api/v1/admin/parents/{parentId}/links
DELETE /api/v1/admin/parents/links/{linkId}
```

**Effort:** Small

---

### GAP-5: Unused Backend Endpoints

**What's Available But Not Used:**

| Endpoint | Purpose | Why Unused |
|----------|---------|------------|
| `POST /admin/enrollments` | Enroll player in module | Frontend manages differently |
| `PATCH /admin/enrollments/{id}/status` | Update enrollment | Not exposed |
| `POST /admin/progress/legacy-points` | Migration import | CSV import uses directly |
| Schedule item endpoints | Per-module scheduling | Frontend uses mock |

**Effort:** Medium to integrate into frontend

---

## Quick Wins Implemented ✅

### ✅ QW-1: Challenge Create/Update Now Uses Correct Endpoints

**Files Modified:**
- `services/challengeService.js` - Fixed endpoint paths
- `routes/challenges.js` - Removed mockOnly middleware, added delete guard

**Changes Made:**
```javascript
// CREATE: Now uses module-nested endpoint
// Before: POST /challenges
// After:  POST /admin/modules/{moduleId}/challenges

// UPDATE: Now uses correct admin path
// Before: PUT /challenges/:id
// After:  PUT /admin/challenges/:id

// DELETE: Added guard (backend doesn't support)
if (!apiConfig.USE_MOCK) {
  return res.redirect('/challenges?error=Challenge deletion is not supported by the Java backend');
}
```

**Impact:** Challenge creation and updates now work in live mode. Delete shows appropriate error message.

### ✅ QW-2: Frontend Delete Guards Already In Place

The frontend already has proper guards for unsupported DELETE operations:
- `routes/players.js` - User deletion guard
- `routes/centres.js` - Centre deletion guard
- `routes/groups.js` - Group deletion guard
- `routes/modules.js` - Module deletion guard

**Status:** Already in place, no changes needed.

### ✅ QW-3: Parent Link Create Already Works

The `POST /admin/parents/link-player` endpoint is properly configured in live mode.

**Status:** Already working.

---

## Implementation Roadmap

### Phase 1: Enable Existing Backend Features (1-2 days)
- [ ] P2-6: Add parent links list/delete endpoints (backend)
- [ ] Update frontend parent routes to use live mode for list

### Phase 2: Add DELETE Operations (2-3 days)
- [ ] P2-1 through P2-5: Add DELETE endpoints to backend controllers
- [ ] Test with frontend - guards should auto-enable live mode

### Phase 3: Challenge/Schedule Integration (3-5 days)
- [ ] P1-1: Refactor frontend challenge service to use module-nested endpoints
- [ ] P1-2: Choose approach (A or B) and implement schedule integration

### Phase 4: Enhancements (Optional)
- [ ] P3-1: Add enrollment management UI
- [ ] P3-2: Expose legacy points import in UI
- [ ] P3-3: Add avatar/image to backend DTOs

---

## Files to Modify

### Frontend
- `routes/challenges.js` - Remove mockOnly, adapt to module endpoints
- `routes/schedule.js` - Remove mockOnly, adapt to module endpoints
- `routes/parents.js` - Enable live mode for list operation
- `services/challengeService.js` - New endpoint mappings
- `services/scheduleService.js` - New endpoint mappings

### Backend
- `UserController.java` - Add deleteUser()
- `CentreController.java` - Add deleteCentre()
- `GroupController.java` - Add deleteGroup()
- `ModuleController.java` - Add deleteModule()
- `ChallengeController.java` - Add deleteChallenge()
- `ParentController.java` - Add getLinks(), deleteLink()

---

## Notes

1. **Mock Mode is a Feature:** The `USE_MOCK` flag allows development without backend. Don't remove this capability.

2. **Cascade Deletes:** When implementing DELETE, consider:
   - Deleting a centre: What happens to its groups?
   - Deleting a group: What happens to its players?
   - Deleting a module: What happens to its challenges?

3. **Soft Delete Option:** Consider soft deletes (set `active=false`) instead of hard deletes for data integrity.
