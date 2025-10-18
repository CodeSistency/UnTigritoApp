# 📋 HANDOFF GUIDE - Client Module Implementation

## 🎯 Purpose
This guide helps the next developer/team quickly understand what has been built and how to continue the implementation.

---

## 🚀 QUICK START (5 minutes)

### 1. Clone & Setup
```bash
cd /home/lenovo/Alegria/UnTigritoApp
git pull origin victor/diseno
```

### 2. Read These Files (in order)
1. `agent-os/specs/2025-10-18-client-module/spec.md` (Overview)
2. `agent-os/specs/2025-10-18-client-module/FINAL-REPORT.md` (What's done)
3. `agent-os/specs/2025-10-18-client-module/tasks.md` (What's left)

### 3. Understand the Architecture
```
Domain Models (5)
    ↓
Room Entities (4)
    ↓
DAOs (4) + Mappers
    ↓
Repository Pattern
    ↓
API Service (18 endpoints)
    ↓
UI Screens (to build)
```

---

## 📂 File Locations Reference

### Core Domain Models
```
app/src/main/java/com/thecodefather/untigrito/domain/model/
├── ClientUser.kt
├── ServicePosting.kt
├── ClientRequest.kt
├── Transaction.kt
└── Professional.kt
```

### Database Layer
```
app/src/main/java/com/thecodefather/untigrito/data/database/
├── entity/
│   ├── ClientUserEntity.kt
│   ├── ServicePostingEntity.kt
│   ├── ClientRequestEntity.kt
│   └── TransactionEntity.kt
└── dao/
    ├── ClientUserDao.kt
    ├── ServicePostingDao.kt
    ├── ClientRequestDao.kt
    └── TransactionDao.kt
```

### Repository Pattern
```
app/src/main/java/com/thecodefather/untigrito/
├── domain/repository/
│   └── ClientRepository.kt (interface)
└── data/repository/
    └── ClientRepositoryImpl.kt (implementation)
```

### API Integration
```
app/src/main/java/com/thecodefather/untigrito/data/datasource/remote/
├── ClientApiService.kt (18 endpoints)
└── AuthInterceptor.kt (token management)
```

### UI Components (Started)
```
app/src/main/java/com/thecodefather/untigrito/presentation/
├── navigation/
│   └── ClientNavigation.kt
└── components/
    └── ClientBottomNavBar.kt
```

### Tests
```
app/src/main/java/com/thecodefather/untigrito/
├── ClientRepositoryTest.kt (6 tests - data layer)
└── ClientApiServiceTest.kt (8 tests - API layer)
```

---

## 🔑 Key Concepts to Understand

### 1. Repository Pattern
The `ClientRepository` interface defines the contract for all data operations. `ClientRepositoryImpl` provides the actual implementation that:
- Interacts with Room DAOs for local storage
- Makes API calls via `ClientApiService`
- Maps between domain models and database entities
- Handles caching and offline scenarios

### 2. Flow & Coroutines
All data queries return `Flow<T>` for reactive updates:
```kotlin
// Example: Get user by ID
fun getUserById(userId: String): Flow<ClientUser?>
```

### 3. Domain Models vs Entities
- **Domain Models** (e.g., `ClientUser.kt`): Used throughout the app
- **Entities** (e.g., `ClientUserEntity.kt`): Only for Room database storage
- **Mappers**: Convert between them automatically in `ClientRepositoryImpl`

### 4. API Error Handling
The `AuthInterceptor` automatically:
- Injects JWT token in every request
- Handles 401 (unauthorized) by refreshing the token
- Clears tokens on refresh failure (logs out user)

---

## 📝 Naming Conventions Used

### Classes
- **Models**: `ClientUser`, `ServicePosting` (clean names)
- **Entities**: `ClientUserEntity`, `ServicePostingEntity` (with `Entity` suffix)
- **DAOs**: `ClientUserDao`, `ServicePostingDao` (with `Dao` suffix)
- **API Service**: `ClientApiService` (single consolidated service)
- **Screens**: `ClientHomeScreen`, `ServicesScreen` (with `Screen` suffix)

### Files
- Kotlin files: PascalCase (e.g., `ClientUser.kt`)
- Test files: `[Class]Test.kt` pattern
- Documentation: kebab-case (e.g., `FINAL-REPORT.md`)

---

## 🛠 How to Continue Development

### IMMEDIATE NEXT STEPS (Task Group 3: Mobile UI)

#### Step 1: Set Up ViewModel Base Class
Create a reusable `BaseClientViewModel` with common patterns:
```kotlin
class BaseClientViewModel(
    protected val repository: ClientRepository
) : ViewModel() {
    protected val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    
    protected val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
}
```

#### Step 2: Implement Screens in This Order
1. **ClientHomeScreen** (simplest - static content first)
2. **ServicesScreen** (with search/filters)
3. **ServiceDetailScreen** (with data binding)
4. **CreateRequestScreen** (with form validation)
5. **RequestsScreen** (with status filtering)
6. **ClientProfileScreen** (with user actions)
7. **PaymentScreen** (with transaction history)

#### Step 3: Testing Strategy
- Write at least 2 tests per screen
- Focus on critical paths (happy path + error states)
- Total: 8 UI tests for Task Group 3

### Testing the Existing Code
```bash
# Run all tests
./gradlew test

# Run specific test file
./gradlew test --tests "*ClientRepositoryTest"
./gradlew test --tests "*ClientApiServiceTest"
```

---

## 🔍 Code Reading Guide

### Understanding the Data Flow
```
1. UI (Screen) calls ViewModel
2. ViewModel calls Repository method
3. Repository:
   - Calls local DAO (first)
   - Calls API Service (if needed)
   - Maps response to domain model
   - Saves to database
   - Emits via Flow
4. UI collects Flow and updates
```

### Example: Getting User Profile
```kotlin
// In ViewModel
viewModelScope.launch {
    repository.getUserById(userId)
        .collect { user ->
            _user.value = user
        }
}

// In Repository
override fun getUserById(userId: String): Flow<ClientUser?> {
    return clientUserDao.getUserById(userId)
        .map { it?.toModel() }
}

// In DAO
@Query("SELECT * FROM client_users WHERE id = :userId")
fun getUserById(userId: String): Flow<ClientUserEntity?>
```

---

## ⚠️ Important Implementation Notes

### 1. State Management
- Use `StateFlow` for UI state (selected tab, input, etc.)
- Use `Flow` for data queries (read-only streams)
- Always use `viewModelScope.launch` in ViewModels

### 2. Compose Best Practices
- Keep composables small and reusable
- Extract state to ViewModels
- Use `remember` for local state only
- Preview every screen with `@Preview`

### 3. Navigation
- Use `NavController.navigate()` to move between screens
- Pass data via Navigation routes (already set up)
- Handle back navigation properly

### 4. Image Loading
- Use Coil for loading images: `AsyncImage(model = url, ...)`
- Cache is automatic
- Handle loading/error states

### 5. Forms & Validation
- Create separate `FormState` data classes
- Validate before submission
- Show inline error messages
- Disable submit button during loading

---

## 🧪 Testing Patterns to Follow

### Unit Test Template
```kotlin
@Test
fun testSomething() = runBlocking {
    // Given
    val input = // ...
    
    // When
    val result = // ...
    
    // Then
    assertEquals(expected, result)
}
```

### Mocking Repository
```kotlin
val mockRepository = mockk<ClientRepository>()
coEvery { mockRepository.getUserById(any()) } returns flowOf(user)
```

---

## 📚 Reference Documentation

### Spring API Documentation
Location: `app/docs/back.md`  
Contains: All 18 endpoints with request/response schemas

### Architecture Standards
Location: `agent-os/standards/`  
- `backend/`: API & database standards
- `frontend/`: UI & component standards
- `global/`: Coding style, conventions, error handling

### Tech Stack Details
Location: `agent-os/product/tech-stack.md`
Location: `agent-os/standards/global/tech-stack.md`

---

## 🐛 Debugging Tips

### Enable Debug Logging
```kotlin
// In AuthInterceptor
private fun log(message: String) {
    Log.d("AuthInterceptor", message)
}
```

### Check Local Database
```bash
# Use Android Studio Database Inspector
# View → Tool Windows → Database Inspector
```

### Network Inspection
```bash
# Add interceptor for request/response logging
HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
```

---

## ✅ Pre-Implementation Checklist

Before starting a new screen, verify:

- [ ] All domain models are understood
- [ ] Repository methods for this screen are known
- [ ] API endpoints used are verified in `ClientApiService.kt`
- [ ] Test file is created (e.g., `[ScreenName]Test.kt`)
- [ ] ViewModel is sketched out
- [ ] Navigation route is added in `ClientNavigation.kt`
- [ ] Compose preview is prepared

---

## 🚨 Common Pitfalls to Avoid

1. **Forgetting .collect() on Flows**
   ```kotlin
   // ❌ WRONG - Flow not collected
   repository.getUserById(id)
   
   // ✅ CORRECT - Collecting the Flow
   repository.getUserById(id).collect { user -> ... }
   ```

2. **Updating state outside ViewModels**
   ```kotlin
   // ❌ WRONG - State mutation in Compose
   var user by remember { mutableStateOf(...) }
   user = newUser // Can cause recomposition issues
   
   // ✅ CORRECT - Use ViewModel
   val user by viewModel.user.collectAsState()
   ```

3. **Not handling null safety**
   ```kotlin
   // ❌ WRONG - NPE risk
   val name = user.name.uppercase()
   
   // ✅ CORRECT - Null-safe
   val name = user?.name?.uppercase() ?: "Unknown"
   ```

4. **Forgetting to inject dependencies**
   ```kotlin
   // ❌ WRONG - Manual instantiation
   val viewModel = ClientHomeViewModel(repository)
   
   // ✅ CORRECT - Hilt injection
   @HiltViewModel
   class ClientHomeViewModel @Inject constructor(
       private val repository: ClientRepository
   ) : ViewModel()
   ```

---

## 📞 Questions & Support

### Where to Find Answers
1. **Architecture questions**: Read `Clean Architecture + MVVM` in `spec.md`
2. **API questions**: Check `ClientApiService.kt` and `app/docs/back.md`
3. **Kotlin/Compose questions**: Reference existing screens like `HomeScreen.kt`
4. **Testing questions**: Look at existing test files (`ClientRepositoryTest.kt`, etc.)

### Code Review Checklist
Before merging:
- [ ] Tests pass locally
- [ ] No linter warnings
- [ ] Follows naming conventions
- [ ] Has proper documentation/comments
- [ ] Handles error cases
- [ ] Uses proper null safety

---

## 📊 Progress Tracking

Update this as you complete screens:
```
Task Group 3: Mobile UI
├─ [ ] ClientHomeScreen (target: 150 lines, 2 tests)
├─ [ ] ServicesScreen (target: 200 lines, 2 tests)
├─ [ ] CreateRequestScreen (target: 250 lines, 2 tests)
├─ [ ] ServiceDetailScreen (target: 200 lines, 2 tests)
├─ [ ] RequestsScreen (target: 200 lines, 2 tests)
├─ [ ] ClientProfileScreen (target: 180 lines, 2 tests)
└─ [ ] PaymentScreen (target: 150 lines, 2 tests)

Total Target: ~1,330 lines + 14 tests
```

---

## 🎓 Final Recommendations

1. **Start Small**: Build one screen completely before starting the next
2. **Test First**: Write test outline before implementation
3. **Use Previews**: Preview screens frequently during development
4. **Reference Code**: Copy patterns from `HomeScreen.kt`
5. **Ask Questions**: Review spec and documentation first
6. **Commit Often**: Small, logical commits are easier to review
7. **Document**: Add comments for non-obvious logic

---

**Last Updated:** October 18, 2025  
**Status:** Ready for UI Implementation Phase  
**Next Developer:** [Your Name]

---
