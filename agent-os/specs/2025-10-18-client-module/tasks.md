# Task Breakdown: Módulo de Cliente

## Overview
Total Tasks: 4 grupos principales
Assigned roles: android-developer, mobile-ux-designer, testing-engineer

## Task List

### Data Layer

#### Task Group 1: Data Models and Local Storage
**Assigned implementer:** android-developer
**Dependencies:** None

- [ ] 1.0 Complete data layer for client module
  - [ ] 1.1 Write 6 focused tests for data models and repositories
    - Test User model with client-specific fields
    - Test ServicePosting model with categories and states
    - Test Professional model with ratings and specialties
    - Test Request model with status transitions
    - Test Transaction model for payment history
    - Test repository pattern with local cache
  - [ ] 1.2 Create User model with client fields
    - Fields: id, name, email, phone, role, isVerified, isIDVerified, balance, locationLat, locationLng, locationAddress
    - Extend existing User model from auth module
    - Add client-specific properties
  - [ ] 1.3 Create ServicePosting model
    - Fields: id, clientId, title, description, category, budget, deadline, status, location, locationLat, locationLng
    - Status enum: OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    - Category enum: PLOMERIA, ELECTRICIDAD, ALBANILERIA, LIMPIEZA, MUDANZA
  - [ ] 1.4 Create Professional model
    - Fields: id, userId, bio, rating, totalReviews, yearsOfExperience, specialties, hourlyRate, isVerified
    - Extend existing Professional model
    - Add rating and review calculations
  - [ ] 1.5 Create Request model
    - Fields: id, clientId, servicePostingId, status, proposedPrice, description, estimatedDuration
    - Status enum: PENDING, ACCEPTED, REJECTED, CANCELLED
    - Link to ServicePosting and Professional
  - [ ] 1.6 Create Transaction model
    - Fields: id, userId, type, amount, description, status, createdAt
    - Type enum: RECHARGE, WITHDRAWAL, PAYMENT
    - Status enum: PENDING, COMPLETED, FAILED
  - [ ] 1.7 Set up Room database entities
    - Create DAOs for each model
    - Add indexes for performance
    - Set up relationships between entities
  - [ ] 1.8 Implement local cache with Room
    - Cache user profile data
    - Cache service postings
    - Cache professional profiles
    - Cache request history
  - [ ] 1.9 Ensure data layer tests pass
    - Run ONLY the 6 tests written in 1.1
    - Verify Room database operations
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 6 tests written in 1.1 pass
- All models have proper validation
- Room database operations work correctly
- Local cache functionality implemented

### API Integration Layer

#### Task Group 2: API Services and Repositories
**Assigned implementer:** android-developer
**Dependencies:** Task Group 1

- [ ] 2.0 Complete API integration layer
  - [ ] 2.1 Write 8 focused tests for API services
    - Test authentication endpoints (login, register, logout)
    - Test user profile endpoints (get, update)
    - Test professionals endpoints (list, search, details)
    - Test service postings endpoints (create, list, update)
    - Test requests endpoints (create, list, update status)
    - Test payment endpoints (balance, transactions)
    - Test error handling and network failures
    - Test token refresh and authentication flow
  - [ ] 2.2 Create AuthApiService
    - Endpoints: login, register, logout, refresh, forgot-password, reset-password
    - Use existing AuthRepository pattern
    - Handle JWT tokens and refresh logic
  - [ ] 2.3 Create UserApiService
    - Endpoints: get profile, update profile, get stats
    - Follow existing UserApiService pattern
    - Add client-specific endpoints
  - [ ] 2.4 Create ProfessionalApiService
    - Endpoints: list professionals, search, get details, get stats
    - Implement filtering and pagination
    - Handle search and location-based queries
  - [ ] 2.5 Create ServicePostingApiService
    - Endpoints: create posting, list postings, get details, update status
    - Handle file uploads for attachments
    - Implement category filtering
  - [ ] 2.6 Create RequestApiService
    - Endpoints: create request, list requests, update status
    - Handle request lifecycle management
    - Implement status transitions
  - [ ] 2.7 Create PaymentApiService
    - Endpoints: get balance, get transactions, create transaction
    - Handle payment confirmation flow
    - Implement transaction history
  - [ ] 2.8 Implement repository pattern
    - Create ClientRepository interface
    - Implement ClientRepositoryImpl with API and local cache
    - Handle offline/online data synchronization
  - [ ] 2.9 Add error handling and retry logic
    - Network error handling
    - Token refresh on 401 errors
    - Retry logic for failed requests
  - [ ] 2.10 Ensure API integration tests pass
    - Run ONLY the 8 tests written in 2.1
    - Verify API communication works
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 8 tests written in 2.1 pass
- All API endpoints integrated successfully
- Repository pattern implemented with cache
- Error handling and retry logic working

### UI Components and Screens

#### Task Group 3: Mobile UI Implementation
**Assigned implementer:** mobile-ux-designer
**Dependencies:** Task Group 2

- [ ] 3.0 Complete mobile UI components
  - [ ] 3.1 Write 8 focused tests for UI components
    - Test ClientHomeScreen rendering and interactions
    - Test ServicesScreen search and filtering
    - Test CreateRequestScreen form validation
    - Test ServiceDetailScreen negotiation flow
    - Test RequestsScreen state management
    - Test ClientProfileScreen user data display
    - Test PaymentScreen transaction flow
    - Test navigation between screens
  - [ ] 3.2 Create ClientHomeScreen
    - Balance card with saldo display
    - Category carousel with icons
    - Top rated professionals carousel
    - Service request button
    - Nearby services list
    - Follow HomeScreen.kt visual patterns
  - [ ] 3.3 Create ServicesScreen
    - Search bar with filters
    - Category filter chips
    - TabRow for Services/Professionals
    - Service cards with ratings
    - Professional cards with specialties
    - Implement search and filtering logic
  - [ ] 3.4 Create CreateRequestScreen
    - Title and description fields
    - Category dropdown
    - Location picker
    - Urgency toggle
    - Date/time pickers
    - File attachment area
    - Price range slider
    - Form validation and submission
  - [ ] 3.5 Create ServiceDetailScreen
    - Service information display
    - Price estimation
    - Tags and categories
    - Location details
    - Negotiation toggle
    - Price range inputs
    - Request button
    - Received offers list
  - [ ] 3.6 Create RequestsScreen
    - TabRow for Abiertas/En Curso/Historial
    - Request cards with status
    - Action buttons (Edit, Delete, Finalize, Cancel)
    - Offers count display
    - Status-based filtering
  - [ ] 3.7 Create ClientProfileScreen
    - User information display
    - Profile image
    - Contact information
    - Verification status
    - Role toggle (Client/Professional)
    - Service history link
    - Logout option
    - Verification banner
  - [ ] 3.8 Create PaymentScreen
    - Balance display
    - Transaction history
    - Recharge methods
    - Withdrawal methods
    - Bank details display
    - Payment confirmation
    - Copy bank details functionality
  - [ ] 3.9 Implement navigation structure
    - BottomNavigationBar with 4 tabs
    - NavHost with all client screens
    - Navigation routes and parameters
    - Deep linking support
  - [ ] 3.10 Apply Material Design 3 styling
    - Use existing color scheme (#E67822)
    - Implement card layouts with rounded corners
    - Add proper spacing and typography
    - Ensure accessibility compliance
  - [ ] 3.11 Add loading states and error handling
    - Loading indicators for API calls
    - Error messages with retry options
    - Empty states for lists
    - Success feedback for actions
  - [ ] 3.12 Ensure UI component tests pass
    - Run ONLY the 8 tests written in 3.1
    - Verify critical user interactions work
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 8 tests written in 3.1 pass
- All screens render correctly
- Navigation works between all screens
- Material Design 3 compliance
- Loading states and error handling implemented

### Testing and Integration

#### Task Group 4: Test Review & Integration Testing
**Assigned implementer:** testing-engineer
**Dependencies:** Task Groups 1-3

- [ ] 4.0 Review existing tests and fill critical gaps
  - [ ] 4.1 Review tests from Task Groups 1-3
    - Review the 6 tests written by android-developer (Task 1.1)
    - Review the 8 tests written by android-developer (Task 2.1)
    - Review the 8 tests written by mobile-ux-designer (Task 3.1)
    - Total existing tests: 22 tests
  - [ ] 4.2 Analyze test coverage gaps for client module
    - Identify critical user workflows lacking test coverage
    - Focus on end-to-end client flows
    - Prioritize integration points between screens
    - Identify missing edge cases for forms and navigation
  - [ ] 4.3 Write up to 10 additional strategic tests maximum
    - Add maximum of 10 new tests to fill identified critical gaps
    - Focus on complete user journeys (login → home → create request → manage requests)
    - Test navigation flows between all screens
    - Test form submissions and validations
    - Test API integration with offline scenarios
    - Test payment flow end-to-end
    - Test role switching functionality
    - Test error recovery scenarios
    - Test data persistence across app restarts
    - Test performance with large lists
    - Test accessibility features
  - [ ] 4.4 Run feature-specific tests only
    - Run ONLY tests related to client module (tests from 1.1, 2.1, 3.1, and 4.3)
    - Expected total: approximately 32 tests maximum
    - Do NOT run the entire application test suite
    - Verify critical client workflows pass

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 32 tests total)
- Critical client user workflows are covered
- No more than 10 additional tests added by testing-engineer
- Testing focused exclusively on client module requirements

## Execution Order

Recommended implementation sequence:
1. Data Layer (Task Group 1) - Models and local storage
2. API Integration Layer (Task Group 2) - Services and repositories  
3. Mobile UI Implementation (Task Group 3) - Screens and components
4. Test Review & Integration Testing (Task Group 4) - Comprehensive testing

## Notes
- All UI components should follow the visual patterns established in HomeScreen.kt
- API integration should use existing patterns from auth module
- Navigation should be consistent with existing app structure
- Material Design 3 compliance required throughout
- Focus on mobile-first responsive design
- Ensure accessibility features are implemented
