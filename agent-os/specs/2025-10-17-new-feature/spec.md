# UnTigritoApp - Project Setup Specification

## Overview

This specification defines the initial setup and project structure for the UnTigritoApp Android application, establishing a solid foundation that promotes maintainability, scalability, and testability through modern Android development practices, Clean Architecture patterns, and dependency injection.

## Project Context

**Application**: UnTigritoApp - Mobile marketplace connecting Venezuelan households and small businesses with verified, skilled local professionals for informal jobs ("tigritos").

**Current Phase**: Phase 1 - V1 MVP (Weeks 5-16)
**Target Platform**: Android (Kotlin + Jetpack Compose)
**Architecture**: Clean Architecture + MVVM

## Objectives

### Primary Objectives
1. Establish a maintainable, scalable, and testable project foundation
2. Implement Clean Architecture with proper separation of concerns
3. Configure modern Android development stack with Jetpack Compose
4. Set up dependency injection for efficient dependency management
5. Create consistent project structure for future development

### Success Criteria
- Project builds successfully without errors
- All dependencies properly configured and resolved
- Clean Architecture layers properly separated
- DI container properly configured
- Navigation structure established
- Build time under 30 seconds for clean builds
- Zero compilation errors

## Technical Requirements

### Technology Stack

#### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Navigation**: Compose Navigation
- **Version Management**: TOML (libs.versions.toml)

#### Required Libraries (Minimum Setup)
```toml
# Jetpack Compose
androidx.compose.ui:ui
androidx.compose.ui:ui-tooling-preview
androidx.compose.material3:material3
androidx.compose.runtime:runtime

# Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose

# Navigation
androidx.navigation:navigation-compose

# Dependency Injection
com.google.dagger:hilt-android
com.google.dagger:hilt-compiler
androidx.hilt:hilt-navigation-compose
```

### Project Structure

#### Root Level Structure
```
UnTigrito/
├── gradle/
│   └── libs.versions.toml          # Centralized dependency management
├── app/
│   ├── build.gradle.kts            # App-level build configuration
│   └── src/main/java/com/thecodefather/untigrito/
└── settings.gradle.kts             # Project settings
```

#### App Module Structure (Clean Architecture)
```
app/src/main/java/com/thecodefather/untigrito/
├── App.kt                          # Application class (@HiltAndroidApp)
├── di/                             # Dependency Injection Modules
│   ├── AppModule.kt               # Main DI module (Singleton providers)
│   └── DataModule.kt              # Data layer DI module
├── presentation/                   # UI Layer (Presentation)
│   ├── theme/                     # Compose Theme System
│   │   ├── Color.kt               # Color definitions
│   │   ├── Theme.kt               # Main theme configuration
│   │   └── Type.kt                # Typography definitions
│   ├── navigation/                # Navigation Configuration
│   │   └── AppNavigation.kt       # Main navigation graph
│   ├── screens/                   # Screen Composables
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   └── splash/
│   │       ├── SplashScreen.kt
│   │       └── SplashViewModel.kt
│   └── components/                # Reusable UI Components
│       ├── common/
│       └── ui/
├── domain/                        # Business Logic Layer
│   ├── model/                     # Core entities (data classes)
│   │   └── User.kt
│   ├── repository/                # Repository interfaces
│   │   └── UserRepository.kt
│   └── usecase/                   # Business logic (Use Cases)
│       └── GetUserUseCase.kt
└── data/                          # Data Implementation Layer
    ├── repository/                # Repository implementations
    │   └── UserRepositoryImpl.kt
    └── datasource/                # Data sources
        ├── local/                 # Local data sources
        │   └── UserDao.kt
        └── remote/                # Remote data sources
            └── UserApiService.kt
```

## Implementation Details

### 1. Dependency Management Setup

#### libs.versions.toml Configuration
```toml
[versions]
compose = "1.5.4"
hilt = "2.48"
navigation = "2.7.5"
lifecycle = "2.7.0"

[libraries]
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui", version.ref = "compose" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling-preview", version.ref = "compose" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3", version = "1.1.2" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.1.0" }

[plugins]
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 2. Dependency Injection (Hilt) Setup

#### Application Class
```kotlin
@HiltAndroidApp
class UnTigritoApp : Application()
```

#### DI Modules
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Singleton providers for core dependencies
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // Data layer dependencies
}
```

### 3. Clean Architecture Implementation

#### Layer Dependencies
- **Presentation** → **Domain** (ViewModels depend on Use Cases)
- **Domain** → **Data** (Use Cases depend on Repository interfaces)
- **Data** → **Domain** (Repository implementations implement Domain interfaces)

#### Dependency Rules
- Outer layers can depend on inner layers
- Inner layers cannot depend on outer layers
- Domain layer is pure Kotlin (no Android dependencies)

### 4. Navigation Setup

#### Navigation Graph
```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}
```

### 5. Theme Configuration

#### Color System
```kotlin
val Primary = Color(0xFF6200EE)
val PrimaryVariant = Color(0xFF3700B3)
val Secondary = Color(0xFF03DAC6)
val Background = Color(0xFFFFFBFE)
val Surface = Color(0xFFFFFBFE)
val Error = Color(0xFFB00020)
```

#### Typography
```kotlin
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    // ... other text styles
)
```

## Quality Assurance

### Testing Strategy
- **Domain Layer**: Pure unit tests (no Android dependencies)
- **Presentation Layer**: ViewModel unit tests with MockK
- **UI Layer**: Instrumented tests and Compose preview tests
- **Data Layer**: Repository and data source tests

### Code Quality Standards
- Follow Kotlin coding conventions
- Implement proper error handling with Result types
- Use appropriate design patterns (Repository, Use Case, MVVM)
- Maintain clean separation of concerns
- Write comprehensive documentation

### Performance Requirements
- Build time < 30 seconds for clean builds
- Zero compilation errors
- Proper memory management
- Efficient dependency injection
- Fast navigation transitions

## Security Considerations

### Data Protection
- Use EncryptedSharedPreferences for sensitive data
- Implement proper data validation
- Follow Android security best practices

### Network Security
- Use HTTPS for all network communications
- Implement certificate pinning (future phases)
- Validate all incoming data

## Future Considerations

### Modularization
- Structure supports easy conversion to multi-module project
- Clear boundaries between features
- Independent testing of modules

### Scalability
- Clean Architecture supports easy addition of new features
- DI container can be extended with new modules
- Navigation structure supports complex app flows

## Dependencies and Constraints

### External Dependencies
- Android SDK 24+ (API level 24)
- Kotlin 1.9.0+
- Gradle 8.0+
- Android Studio Arctic Fox+

### Internal Constraints
- Must follow established coding standards
- Must maintain Clean Architecture principles
- Must use Hilt for dependency injection
- Must use Jetpack Compose for UI

## Acceptance Criteria

### Functional Requirements
- [ ] Project builds successfully without errors
- [ ] All dependencies resolved and properly configured
- [ ] Clean Architecture layers properly implemented
- [ ] DI container configured and working
- [ ] Navigation structure established
- [ ] Theme system configured and applied

### Non-Functional Requirements
- [ ] Build time under 30 seconds for clean builds
- [ ] Zero compilation errors
- [ ] Proper memory management
- [ ] Code follows established patterns
- [ ] Documentation covers project structure

### Quality Requirements
- [ ] All tests pass (when implemented)
- [ ] Code coverage baseline established
- [ ] Linting rules configured and enforced
- [ ] Performance benchmarks met

