# Technical Specification - Project Setup

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Navigation**: Compose Navigation
- **Version Management**: TOML (libs.versions.toml)

### Jetpack Libraries (Minimum for Setup)
- `androidx.compose.*` (UI, Material, Runtime)
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.navigation:navigation-compose`
- `com.google.dagger:hilt-android`

## Project Structure

### Root Level Structure
```
[root]/
├── gradle/
│   └── libs.versions.toml  <-- CENTRALIZED DEPENDENCY MANAGEMENT
├── app/
│   ├── build.gradle.kts
│   └── src/main/java/com/thecodefather/untigrito/
└── settings.gradle.kts
```

### App Module Structure (Clean Architecture)
```
app/src/main/java/com/thecodefather/untigrito/
├── App.kt                    <-- Application class for DI (@HiltAndroidApp)
├── di/                       <-- Dependency Injection Modules
│   ├── AppModule.kt         <-- Main DI module (Singleton providers)
│   └── DataModule.kt        <-- Data layer DI module
├── presentation/             <-- UI Layer
│   ├── feature_name/
│   │   ├── components/      <-- Reusable Composables
│   │   ├── screens/         <-- Main Feature Screens
│   │   ├── FeatureContract.kt <-- State/Event definitions (MVI)
│   │   └── FeatureViewModel.kt <-- Presentation Logic
│   └── navigation/          <-- Compose Navigation
│       └── FeatureNavHost.kt
├── domain/                   <-- Business Logic Layer
│   ├── model/               <-- Core entities (data classes)
│   ├── repository/          <-- Repository interfaces
│   └── usecase/             <-- Business logic (Get, Save, etc.)
└── data/                    <-- Data Implementation Layer
    ├── repository/          <-- Repository implementations
    └── datasource/          <-- Data sources (local, remote)
        ├── local/           <-- Room DAOs, local storage
        └── remote/          <-- API services, network
```

## Implementation Details

### 1. Dependency Management (libs.versions.toml)
- Centralize all library versions and names
- Ensure consistency across all modules
- Enable easy version updates

### 2. Dependency Injection (Hilt)
- Configure `@HiltAndroidApp` in Application class
- Create `@Module` classes for different layers
- Provide singletons for core dependencies
- Enable injection in ViewModels and Composables

### 3. Clean Architecture Implementation
- **Presentation Layer**: Composables + ViewModels
- **Domain Layer**: Use cases + Repository interfaces
- **Data Layer**: Repository implementations + Data sources
- Strict dependency rules (outer layers depend on inner layers)

### 4. Navigation Setup
- Configure Compose Navigation
- Define navigation graphs
- Set up route definitions
- Implement navigation between screens

### 5. Theme Configuration
- Set up Compose Theme
- Configure typography, colors, and shapes
- Create consistent design system

## Quality Assurance

### Testing Strategy
- **Domain Layer**: Pure unit tests
- **Presentation Layer**: ViewModel unit tests
- **UI Layer**: Instrumented tests and preview tests
- **Data Layer**: Repository and data source tests

### Code Quality
- Follow Kotlin coding conventions
- Implement proper error handling
- Use appropriate design patterns
- Maintain clean separation of concerns

## Success Criteria

### Technical Requirements
- [ ] Project builds successfully
- [ ] All dependencies resolved
- [ ] Clean Architecture properly implemented
- [ ] DI container configured
- [ ] Navigation structure established
- [ ] Theme system configured

### Performance Requirements
- Build time < 30 seconds for clean builds
- Zero compilation errors
- Proper memory management
- Efficient dependency injection

### Maintainability Requirements
- Clear project structure
- Consistent code organization
- Proper documentation
- Easy to add new features
