# Implementation Plan: Task Group 1 - Data Models and Local Storage

**Assigned implementer:** android-developer
**Spec reference:** agent-os/specs/2025-10-18-client-module/spec.md
**Status:** Pending Implementation

## Task Breakdown

### 1.0 Complete data layer for client module
This is the parent task that encompasses all subtasks below.

### 1.1 Write 6 focused tests for data models and repositories
**Test Coverage Focus:**
- [ ] Test User model with client-specific fields (extension of existing User model)
- [ ] Test ServicePosting model with categories and state transitions
- [ ] Test Professional model with ratings and specialties calculations
- [ ] Test Request model with status transitions (PENDING → ACCEPTED → COMPLETED)
- [ ] Test Transaction model for payment history
- [ ] Test repository pattern with local Room cache functionality

**Test Strategy:** Create focused unit tests that verify core model behaviors without exhaustive edge case coverage.

### 1.2 Create User model with client fields
**Extend existing User model from auth module**

Fields to add:
- `role: String` (CLIENT, PROFESSIONAL)
- `isVerified: Boolean`
- `isIDVerified: Boolean`
- `balance: Double`
- `locationLat: Double?`
- `locationLng: Double?`
- `locationAddress: String?`

**Reference:** Extend pattern from auth module's existing User model

### 1.3 Create ServicePosting model
**New entity for service requests**

Fields:
- `id: String` (primary key)
- `clientId: String` (foreign key to User)
- `title: String` (5-200 chars)
- `description: String` (10-5000 chars)
- `category: String` (enum: PLOMERIA, ELECTRICIDAD, ALBANILERIA, LIMPIEZA, MUDANZA)
- `budget: Double`
- `deadline: String?` (ISO datetime)
- `status: String` (enum: OPEN, IN_PROGRESS, COMPLETED, CANCELLED)
- `location: String?`
- `locationLat: Double?`
- `locationLng: Double?`
- `createdAt: String`
- `updatedAt: String`

### 1.4 Create Professional model
**Extend existing Professional model**

Fields to add/enhance:
- `rating: Double?` (0-5 scale)
- `totalReviews: Int?`
- `yearsOfExperience: Int?`
- `specialties: List<String>`
- `hourlyRate: Double?`
- `isVerified: Boolean`
- `bio: String?` (max 1000 chars)

### 1.5 Create Request model
**Represents offers made on ServicePostings**

Fields:
- `id: String` (primary key)
- `clientId: String` (foreign key)
- `servicePostingId: String` (foreign key to ServicePosting)
- `professionalId: String` (foreign key to Professional)
- `status: String` (enum: PENDING, ACCEPTED, REJECTED, CANCELLED)
- `proposedPrice: Double`
- `description: String` (10-2000 chars)
- `estimatedDuration: Int` (minutes)
- `createdAt: String`
- `updatedAt: String`

### 1.6 Create Transaction model
**Payment history tracking**

Fields:
- `id: String` (primary key)
- `userId: String` (foreign key)
- `type: String` (enum: RECHARGE, WITHDRAWAL, PAYMENT)
- `amount: Double`
- `description: String`
- `status: String` (enum: PENDING, COMPLETED, FAILED)
- `createdAt: String`

### 1.7 Set up Room database entities
**Create database layer**

- [ ] Add Room Entity annotations to all models
- [ ] Create DAOs for each entity (UserDao, ServicePostingDao, etc.)
- [ ] Add database indexes on frequently queried fields (clientId, status, createdAt)
- [ ] Define foreign key relationships
- [ ] Create main database class extending RoomDatabase

### 1.8 Implement local cache with Room
**Create repository pattern for data access**

- [ ] Implement UserRepository interface (get, update, cache management)
- [ ] Implement ServicePostingRepository (cache service postings locally)
- [ ] Implement ProfessionalRepository (cache professional profiles)
- [ ] Implement RequestRepository (cache request history)
- [ ] Add sync logic between local cache and API (initial sync on app start)

### 1.9 Ensure data layer tests pass
- [ ] Run ONLY the 6 tests written in 1.1
- [ ] Verify all Room database operations complete successfully
- [ ] Verify repository methods return expected data from cache
- [ ] Do NOT run the entire test suite at this stage

## Acceptance Criteria
- ✅ The 6 tests written in 1.1 pass successfully
- ✅ All models implement proper validation
- ✅ Room database operations work correctly
- ✅ Local cache functionality implemented and tested
- ✅ Repository pattern established for data access
- ✅ Task Group 1 sub-tasks marked complete in tasks.md

## Implementation Notes
- Follow existing Room patterns from the project if available
- Use coroutines for async database operations
- Ensure models align with API response schemas from back.md documentation
- Clean Architecture pattern: keep entities separate from API DTOs
- Use Hilt for dependency injection of repositories

## Next Steps
After completion, Task Group 2 (API Services) depends on this layer being complete.
