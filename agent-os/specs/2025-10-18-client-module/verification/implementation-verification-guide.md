# Implementation Verification Guide

**Assigned verifier:** android-verifier
**Spec reference:** agent-os/specs/2025-10-18-client-module/spec.md
**Date:** 2025-10-18
**Status:** Pending Verification

## Overview

This guide instructs the android-verifier to verify the implementation of all 4 task groups for the Client Module.

All task groups should be verified by the `android-verifier` as defined in:
- `agent-os/specs/2025-10-18-client-module/planning/task-assignments.yml`

## Task Groups to Verify

### Task Group 1: Data Models and Local Storage
**Implemented by:** android-developer
**Reference:** `agent-os/specs/2025-10-18-client-module/implementation/1_task-group-1_data-layer.md`

**Verification Checklist:**
- [ ] Verify 6 focused tests written in 1.1 and all tests pass
- [ ] Verify User, ServicePosting, Professional, Request, Transaction models created
- [ ] Verify Room database entities properly annotated
- [ ] Verify DAOs created for each entity
- [ ] Verify foreign key relationships set up correctly
- [ ] Verify local cache implementation in repositories
- [ ] Verify database indexes added for performance
- [ ] Verify Room database class extends RoomDatabase
- [ ] Verify no errors when running 1.1 tests only

**Acceptance:** All 6 tests pass, models valid, Room operations work, cache implemented

---

### Task Group 2: API Services and Repositories
**Implemented by:** android-developer
**Reference:** `agent-os/specs/2025-10-18-client-module/implementation/2_task-group-2_api-integration.md`

**Verification Checklist:**
- [ ] Verify 8 focused tests written in 2.1 and all tests pass
- [ ] Verify AuthApiService created with 6 endpoints
- [ ] Verify UserApiService created with profile endpoints
- [ ] Verify ProfessionalApiService with list, search, details endpoints
- [ ] Verify ServicePostingApiService with CRUD operations
- [ ] Verify RequestApiService for offer management
- [ ] Verify PaymentApiService for balance and transactions
- [ ] Verify ClientRepository interface and implementation
- [ ] Verify error handling and retry logic implemented
- [ ] Verify token refresh interceptor added
- [ ] Verify offline cache fallback working
- [ ] Verify no errors when running 2.1 tests only

**Acceptance:** All 8 tests pass, all API services integrated, repository pattern working, error handling functional

---

### Task Group 3: Mobile UI Implementation
**Implemented by:** mobile-ux-designer
**Reference:** `agent-os/specs/2025-10-18-client-module/implementation/3_task-group-3_mobile-ui.md`

**Verification Checklist:**
- [ ] Verify 8 focused tests written in 3.1 and all tests pass
- [ ] Verify ClientHomeScreen implemented with all components (balance, categories, professionals, services)
- [ ] Verify ServicesScreen with search, filters, tabs (Services/Professionals)
- [ ] Verify CreateRequestScreen with form validation
- [ ] Verify ServiceDetailScreen with negotiation toggle and offer logic
- [ ] Verify RequestsScreen with 3 tabs (Abiertas, En Curso, Historial)
- [ ] Verify ClientProfileScreen with user info and verification
- [ ] Verify PaymentScreen with balance and transaction history
- [ ] Verify BottomNavigationBar with 4 tabs (Inicio, Servicios, Solicitudes, Perfil)
- [ ] Verify navigation between all screens working correctly
- [ ] Verify Material Design 3 styling applied (color #E67822, cards with 12dp corners)
- [ ] Verify LoadingIndicator used for API calls
- [ ] Verify ErrorSnackbar used for error messages
- [ ] Verify form validation working correctly
- [ ] Verify no errors when running 3.1 tests only

**Acceptance:** All 8 tests pass, all screens render, navigation works, Material Design compliance, loading/error states

---

### Task Group 4: Test Review & Integration Testing
**Implemented by:** testing-engineer
**Reference:** `agent-os/specs/2025-10-18-client-module/implementation/4_task-group-4_testing.md`

**Verification Checklist:**
- [ ] Verify testing-engineer reviewed tests from Task Groups 1, 2, 3
- [ ] Verify no more than 10 additional tests written in 4.3
- [ ] Verify all ~32 tests run successfully (6+8+8+up to 10)
- [ ] Verify critical client workflows covered (create request, payment, navigation)
- [ ] Verify end-to-end flows tested
- [ ] Verify error scenarios handled
- [ ] Verify offline cache fallback tested
- [ ] Verify form validation tested
- [ ] Verify no errors when running feature-specific tests only
- [ ] Verify test documentation complete

**Acceptance:** Total tests ~32, all pass, critical workflows covered, no comprehensive test suite run

---

## Verification Process

### Step 1: Pre-Verification Check
1. Verify the spec path exists: `agent-os/specs/2025-10-18-client-module/`
2. Verify all implementation files exist:
   - `spec.md`
   - `tasks.md` (with checkmarks updated)
   - `implementation/1_*.md`
   - `implementation/2_*.md`
   - `implementation/3_*.md`
   - `implementation/4_*.md`

### Step 2: Verify Each Task Group
For each task group in order:
1. Read the implementation guide document
2. Run the focused tests written in that task group (only those tests)
3. Verify all acceptance criteria met
4. Document any issues found
5. Verify tasks marked complete in `tasks.md`

### Step 3: Integration Testing
1. Verify all 4 task groups work together
2. Run complete client module feature tests (~32 total)
3. Verify navigation flows across all screens
4. Verify data flows from models → repositories → API → UI
5. Verify offline mode works correctly

### Step 4: Documentation
1. Create verification report in: `verification/android-verifier-verification.md`
2. Document all issues found (if any)
3. Document test execution results
4. Provide recommendations for fixes

---

## Verification Criteria

### Overall Pass Requirements
- ✅ All ~32 tests pass
- ✅ All 4 task groups complete
- ✅ Acceptance criteria met for each group
- ✅ No errors during test execution
- ✅ Navigation works between all screens
- ✅ Material Design 3 compliance
- ✅ Data flows correctly through layers

### Documentation Requirements
- ✅ All implementation sub-tasks marked complete
- ✅ Verification report generated
- ✅ Any issues documented with severity

---

## Test Execution Commands

For each task group, run ONLY its tests:

**Task Group 1:**
```bash
./gradlew test -Dkotlin.tests.filter="*DataModelsAndRepositoriesTest"
```

**Task Group 2:**
```bash
./gradlew test -Dkotlin.tests.filter="*ApiServicesTest"
```

**Task Group 3:**
```bash
./gradlew test -Dkotlin.tests.filter="*ClientModuleUITest"
```

**All Client Module Tests:**
```bash
./gradlew test -Dkotlin.tests.filter="*ClientModule*"
```

---

## Known Issues / Considerations

1. **No Real Backend:** Tests may use mock API responses or local test data
2. **No Real Payments:** Payment integration is UI only (no real payment processing)
3. **Simplified Maps:** Service location maps are simulated
4. **Mock Professional Data:** Professional profiles use sample data in initial tests

---

## Next Steps After Verification

1. If all verifications pass:
   - Update tasks.md to mark all complete
   - Create successful verification report
   - Proceed to Phase 4 (Final Verification)

2. If issues found:
   - Document issues clearly
   - Request fixes from appropriate implementers
   - Re-verify after fixes

---

## Report Location

Verification report should be saved at:
`agent-os/specs/2025-10-18-client-module/verification/android-verifier-verification.md`
