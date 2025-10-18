# Implementation Plan: Task Group 3 - Mobile UI Implementation

**Assigned implementer:** mobile-ux-designer
**Spec reference:** agent-os/specs/2025-10-18-client-module/spec.md
**Dependencies:** Task Group 2 (API Services and Repositories)
**Status:** Pending Implementation

## Task Breakdown

### 3.0 Complete mobile UI components
This parent task encompasses all screen and component implementation for the client module.

### 3.1 Write 8 focused tests for UI components
**Test Coverage Focus:**
- [ ] Test ClientHomeScreen rendering and user interactions
- [ ] Test ServicesScreen search and category filtering functionality
- [ ] Test CreateRequestScreen form validation
- [ ] Test ServiceDetailScreen negotiation toggle and price range inputs
- [ ] Test RequestsScreen tab switching and status filtering
- [ ] Test ClientProfileScreen data display and role toggle
- [ ] Test PaymentScreen transaction flow
- [ ] Test BottomNavigationBar navigation between all screens

**Test Strategy:** Use Compose testing library to verify critical user interactions.

### 3.2 Create ClientHomeScreen
**Main screen of the client module**

Components to implement:
- **AppTopBar**: User name, email icon, notification icon (reuse HomeScreen.kt pattern)
- **BalanceCard**: Display current balance (e.g., "15,000.00 Bs"), "Recargar" and "Historial" buttons
- **CategoryCarousel**: Horizontal scrolling list of service categories (Plomería, Electricidad, etc.)
- **TopRatedTigersCarousel**: Horizontal list of top professional profiles with ratings
- **PublishRequestButton**: Large action button to navigate to CreateRequestScreen
- **NearbyServicesSection**: List of nearby/recent services with titles, providers, ratings, distance

**Visual Reference:** Follow HomeScreen.kt patterns:
- Color scheme: Primary #E67822 (naranja), background #F0F0F0
- Cards with RoundedCornerShape(12.dp)
- Padding 24.dp for horizontal spacing
- Material Design 3 styling

### 3.3 Create ServicesScreen
**Browse and search services/professionals**

Components:
- **SearchBar**: Text field with search icon for finding services or professionals
- **CategoryFilterChips**: Horizontal scrollable chips for quick filtering by category
- **TabRow**: Two tabs: "Servicios" and "Profesionales"
- **ServicesList** (Tab 1):
  - ServiceCard components showing:
    - Service image/thumbnail
    - Service title
    - Provider name
    - Price range
    - Star rating and review count
    - Distance from user
- **ProfessionalsList** (Tab 2):
  - ProfessionalCard components showing:
    - Profile photo (circular)
    - Name
    - Specialty/profession
    - Location
    - Star rating and review count

**Functionality:**
- Real-time search filtering
- Category filtering
- Tab switching
- Click handlers for navigation to detail screens

### 3.4 Create CreateRequestScreen
**Form for publishing service requests**

Form fields:
- [ ] **Title Field**: OutlinedTextField (required, 5-200 chars)
- [ ] **Description Field**: OutlinedTextField multiline (required, 10-5000 chars)
- [ ] **Category Dropdown**: Dropdown menu for service categories
- [ ] **Location Field**: Text field or location picker button
- [ ] **Urgency Toggle**: Switch for marking service as urgent
- [ ] **Date/Time Picker**: Two fields for date and time selection
- [ ] **Attachment Area**: Area for uploading photos/videos (optional)
- [ ] **Price Range**: Min/Max price fields or RangeSlider (optional)
- [ ] **Publish Button**: Submit form button

**Form Validation:**
- Title: 5-200 characters
- Description: 10-5000 characters
- Category: required selection
- Date: must be in future
- Price: reasonable range

### 3.5 Create ServiceDetailScreen
**View service details and negotiate pricing**

Sections:
- [ ] **Service Header**: Title, description, estimated price range
- [ ] **Details Section**: Category tags, distance, location, date/time, requirements
- [ ] **Map Section**: Simulated map showing service location
- [ ] **NegotiateToggle**: Toggle switch to enter negotiation mode
- [ ] **NegotiationSection** (shown when toggle enabled):
  - Min price field
  - Max price field
  - Notes/description field
- [ ] **ReceivedOffers**: List of offers if viewing own posting (with professional info)
- [ ] **RequestButton**: "Realizar Solicitud" or "Solicitar" button

**Interactions:**
- Toggle negotiation mode on/off
- Submit request/offer
- Navigate to professional detail
- Scroll through offer list

### 3.6 Create RequestsScreen
**Manage user's service requests**

Tab structure:
- **Abiertas Tab**: Open requests waiting for offers
  - Shows number of received offers
  - "Revisar ofertas" button for each request
  - Edit and Delete actions
- **En Curso Tab**: Active/in-progress requests
  - Shows current status
  - "Finalizar" button
  - "Cancelar" option
- **Historial Tab**: Completed or cancelled requests
  - Shows completion status
  - "Ver Historial de Pago" link for completed
  - Delete option

**RequestCard Components:**
- Request title
- Location
- Price range
- Status indicator
- Action buttons (context-dependent)

### 3.7 Create ClientProfileScreen
**User profile and account settings**

Sections:
- [ ] **ProfileHeader**:
  - Profile image (circular)
  - User name
  - Verification status icon
  - Edit profile link
- [ ] **UserInfo**: Email, phone, address (if verified)
- [ ] **RoleToggle**: Switch to toggle "Soy un profesional" mode
- [ ] **VerificationSection**: 
  - Verification status display
  - "Verifícate!" CTA banner if not verified
- [ ] **HistoryLink**: "Historial de Servicios" navigation link
- [ ] **LogoutButton**: Logout action

**Functionality:**
- Display user data from profile
- Toggle professional mode
- Navigate to verification flow
- Navigate to service history
- Logout handling

### 3.8 Create PaymentScreen
**Manage balance and payments**

Sections:
- [ ] **BalanceDisplay**: Current balance in Bs
- [ ] **ActionButtons**: "Recargar" and "Retirar" buttons
- [ ] **TransactionHistory**: List of recent transactions (recharges, withdrawals, payments)
- [ ] **RechargeMethodsList**: 
  - "Cuenta UnTigrito"
  - "Transferencia"
  - "Pago Móvil"
- [ ] **WithdrawalMethodsList**: Similar methods for withdrawals
- [ ] **BankDetailsCard** (when method selected):
  - Display bank account details
  - Teléfono, RIF, Banco fields
  - "Copiar todo" button
  - "Ya pagué" confirmation button

**Flow:**
- Select recharge/withdrawal method
- Show bank details
- Copy functionality
- Confirmation screen

### 3.9 Implement navigation structure
**Complete navigation graph**

- [ ] Create ClientNavGraph with all screens as destinations
- [ ] Implement BottomNavigationBar with 4 tabs:
  - Inicio (Home icon)
  - Servicios (Service icon)
  - Solicitudes (Requests icon)
  - Perfil (Profile icon)
- [ ] Set up NavHost with proper routing
- [ ] Implement deep linking support
- [ ] Handle back navigation properly

### 3.10 Apply Material Design 3 styling
**Consistent design throughout**

- [ ] Use #E67822 as primary color throughout
- [ ] Apply RoundedCornerShape(12.dp) to all cards
- [ ] Use Material Design 3 typography (titleLarge, bodyMedium, etc.)
- [ ] Implement proper spacing (24.dp horizontal padding)
- [ ] Use Material3 components (Button, TextField, Card, etc.)
- [ ] Ensure color accessibility (sufficient contrast)
- [ ] Implement consistent button styles

### 3.11 Add loading states and error handling
**Robust user feedback**

- [ ] Add LoadingIndicator component for API calls
- [ ] Display ErrorSnackbar for error messages
- [ ] Implement empty states for empty lists
- [ ] Show success feedback for completed actions
- [ ] Handle network errors gracefully
- [ ] Implement retry mechanisms

### 3.12 Ensure UI component tests pass
- [ ] Run ONLY the 8 tests written in 3.1
- [ ] Verify all screens render without crashes
- [ ] Verify navigation between screens works
- [ ] Verify form submissions and validations
- [ ] Do NOT run the entire test suite at this stage

## Acceptance Criteria
- ✅ The 8 tests written in 3.1 pass successfully
- ✅ All screens render correctly with proper layouts
- ✅ Navigation works between all 4 tabs
- ✅ Material Design 3 compliance throughout
- ✅ Loading states and error handling implemented
- ✅ Form validations working correctly
- ✅ Task Group 3 sub-tasks marked complete in tasks.md

## Implementation Notes
- Use Jetpack Compose for all UI
- Follow Clean Architecture with ViewModels for state management
- Use StateFlow for reactive state updates
- Implement proper accessibility (contentDescription, semantics)
- Use Coil for image loading where needed
- Follow project's existing composable patterns

## Dependencies
- Requires Task Group 2 (API services) to be complete
- No dependencies for Task Group 4, but can proceed in parallel

## Visual Reference
Reference HomeScreen.kt for:
- Color scheme and styling
- Card layouts and components
- Typography and spacing
- Navigation patterns
- BottomNavigationBar implementation
