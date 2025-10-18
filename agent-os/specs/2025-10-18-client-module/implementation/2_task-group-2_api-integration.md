# Implementation Plan: Task Group 2 - API Services and Repositories

**Assigned implementer:** android-developer
**Spec reference:** agent-os/specs/2025-10-18-client-module/spec.md
**Dependencies:** Task Group 1 (Data Models and Local Storage)
**Status:** Pending Implementation

## Task Breakdown

### 2.0 Complete API integration layer
This parent task encompasses all API service creation and repository implementation.

### 2.1 Write 8 focused tests for API services
**Test Coverage Focus:**
- [ ] Test authentication endpoints (login, register, logout)
- [ ] Test user profile endpoints (get profile, update profile)
- [ ] Test professionals endpoints (list, search, get details)
- [ ] Test service postings endpoints (create, list, update status)
- [ ] Test requests endpoints (create, list, update status)
- [ ] Test payment endpoints (get balance, get transactions)
- [ ] Test error handling and network failures (network timeout, 401/403)
- [ ] Test token refresh and automatic re-authentication flow

**Test Strategy:** Focus on critical happy paths and key error scenarios.

### 2.2 Create AuthApiService
**Interface for authentication operations**

Retrofit/Ktor interface with endpoints:
- `POST /api/auth/login` - Login with email/phone and password
- `POST /api/auth/register` - Register new user
- `POST /api/auth/logout` - Logout current user
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token

**Implementation Notes:**
- Use existing AuthRepository pattern from auth module
- Handle JWT token storage in EncryptedSharedPreferences
- Implement automatic token refresh on 401 responses
- Handle token expiration gracefully

### 2.3 Create UserApiService
**Interface for user profile operations**

Retrofit/Ktor interface with endpoints:
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile/update` - Update user profile
- `GET /api/users/[id]/stats` - Get user statistics

**Reference existing UserApiService pattern** and extend with client-specific endpoints.

### 2.4 Create ProfessionalApiService
**Interface for professional queries**

Retrofit/Ktor interface with endpoints:
- `GET /api/professionals/list` - Get paginated list of professionals (with filters)
- `GET /api/professionals/[id]` - Get professional details by ID
- `GET /api/professionals/[id]/stats` - Get professional statistics
- `GET /api/professionals/search` - Search professionals by text query

**Implementation:** Include filtering by:
- Specialty
- Min rating
- Max hourly rate
- Min experience
- Location (lat/lng with radius)

### 2.5 Create ServicePostingApiService
**Interface for service posting operations**

Retrofit/Ktor interface with endpoints:
- `GET /api/services/postings/list` - Get paginated list of postings (with filters)
- `POST /api/services/postings/create` - Create new service posting
- `GET /api/services/postings/[id]` - Get posting details
- `PUT /api/services/postings/[id]` - Update posting status

**Implementation:** Include support for:
- File uploads for attachments
- Category filtering
- Status filtering
- Location-based queries

### 2.6 Create RequestApiService
**Interface for request/offer management**

Retrofit/Ktor interface with endpoints:
- `POST /api/services/offers/create` - Create offer on a posting
- `GET /api/services/postings/[id]/offers` - Get offers for a posting
- `PUT /api/services/offers/[id]/status` - Update offer status (ACCEPT/REJECT)

**Implementation:** Track request lifecycle and offer negotiations.

### 2.7 Create PaymentApiService
**Interface for payment operations**

Retrofit/Ktor interface with endpoints:
- `GET /api/users/profile` - Get user balance
- Retrieve transaction history
- Endpoints for confirmation of manual payments (to be determined)

**Note:** Real payment processing is out of scope; only information display and confirmation.

### 2.8 Implement repository pattern
**Create abstraction layer for data access**

- [ ] Create `ClientRepository` interface defining all data access contracts
- [ ] Implement `ClientRepositoryImpl` with:
  - Combined access to local Room cache and remote API
  - Fallback to local cache when offline
  - Background sync of data
  - Error handling and retry logic
- [ ] Implement repository methods:
  - Get user profile (API → cache if offline)
  - Get professionals list (API → cache)
  - Create service posting (API first, then cache)
  - Get requests/offers (API → cache)
  - Get balance and transactions (API)

### 2.9 Add error handling and retry logic
**Robust network request handling**

- [ ] Implement NetworkException handling
- [ ] Add automatic token refresh interceptor (401 responses)
- [ ] Implement exponential backoff retry for failed requests
- [ ] Handle timeout errors gracefully
- [ ] Provide user-friendly error messages
- [ ] Log API errors for debugging

### 2.10 Ensure API integration tests pass
- [ ] Run ONLY the 8 tests written in 2.1
- [ ] Verify API service methods return expected data
- [ ] Verify repository cache behavior works correctly
- [ ] Verify error handling scenarios (network, auth, server errors)
- [ ] Do NOT run the entire test suite at this stage

## Acceptance Criteria
- ✅ The 8 tests written in 2.1 pass successfully
- ✅ All API services integrated with Retrofit or Ktor
- ✅ Repository pattern fully implemented
- ✅ Error handling and retry logic working
- ✅ Automatic token refresh implemented
- ✅ Offline cache fallback working
- ✅ Task Group 2 sub-tasks marked complete in tasks.md

## Implementation Notes
- Use Retrofit with OkHttp for HTTP client (or Ktor Client if preferred)
- Implement Kotlin Serialization or Moshi for JSON serialization
- Add Hilt for dependency injection
- Use Result<T> sealed class for error handling (consistent with project)
- Implement proper logging with Timber or similar
- Follow existing API patterns from the project

## Dependencies
- Requires Task Group 1 to be complete (database models and local storage)
- Required by Task Group 3 (UI will depend on these services)

## Next Steps
After completion, Task Group 3 (Mobile UI) depends on these API services being available.
