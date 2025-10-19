# UnTigritoApp - Android Project Setup

A modern Android application for connecting Venezuelan households and small businesses with verified, skilled local professionals.

## 📋 Project Setup Status

✅ **Foundation Setup Complete**
- Version management via TOML
- Build system configured
- Dependency injection with Hilt
- Clean Architecture implemented
- Jetpack Compose UI setup
- Navigation configured
- Testing infrastructure ready

## 🏗️ Architecture

### Clean Architecture Layers

```
presentation/ → domain/ → data/
```

- **Presentation Layer**: UI components (Composables), ViewModels, and navigation
- **Domain Layer**: Pure Kotlin business logic, use cases, and repository interfaces
- **Data Layer**: Repository implementations, data sources (local/remote)

### Key Components

- **Jetpack Compose**: Modern declarative UI framework
- **Hilt**: Dependency injection framework
- **Room**: Local database with SQLite
- **Retrofit**: HTTP client for API communication
- **Kotlin Flows**: Reactive data streams

## 📦 Project Structure

```
app/src/main/java/com/thecodefather/untigrito/
├── App.kt                           # Application class with @HiltAndroidApp
├── MainActivity.kt                  # Entry point activity
├── di/                              # Dependency injection modules
│   ├── AppModule.kt                # Core app dependencies
│   └── DataModule.kt               # Data layer dependencies
├── domain/                          # Domain layer (pure Kotlin)
│   ├── model/                      # Domain entities
│   │   └── User.kt
│   ├── repository/                 # Repository interfaces
│   │   └── UserRepository.kt
│   └── usecase/                    # Use cases
│       └── GetUserUseCase.kt
├── data/                           # Data layer
│   ├── repository/                 # Repository implementations
│   │   └── UserRepositoryImpl.kt
│   └── datasource/                 # Data sources
│       ├── local/                  # Local database
│       │   ├── UserDao.kt
│       │   └── UserEntity.kt
│       └── remote/                 # API
│           ├── UserApiService.kt
│           └── UserDto.kt
├── presentation/                   # Presentation layer
│   ├── theme/                      # UI theme
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── screens/                    # Screen implementations
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   └── splash/
│   │       ├── SplashScreen.kt
│   │       └── SplashViewModel.kt
│   ├── navigation/                 # Navigation setup
│   │   └── AppNavigation.kt
│   └── components/                 # Reusable components
│       └── common/
│           └── AppButton.kt
└── ui/                             # UI configuration
    └── theme/                      # Theme files
```

## 🚀 Quick Start

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17+
- Android SDK 34+
- Kotlin 1.9.20+

### Build the Project

```bash
# Clone the repository
git clone <repository-url>

# Navigate to project directory
cd UnTigrito

# Build the project
./gradlew build

# Run on emulator/device
./gradlew installDebug
```

### Project Structure Overview

**Version Management**
- All library versions centralized in `gradle/libs.versions.toml`
- No hardcoded versions in build files
- Organized by functionality (Compose, Lifecycle, Hilt, Testing, etc.)

**Dependency Injection**
- Hilt for automatic dependency injection
- App-level singletons in `AppModule.kt`
- Data-layer dependencies in `DataModule.kt`
- View model injection in UI classes

**Clean Architecture**
- Strict separation of concerns
- Domain layer: Pure Kotlin, no Android dependencies
- Data layer: Repository pattern for data access
- Presentation layer: MVVM pattern with StateFlow

**Reactive Programming**
- Kotlin Flows for reactive data streams
- StateFlow for UI state management
- Coroutines for asynchronous operations

## 🧪 Testing

### Unit Tests
Location: `app/src/test/`
- JUnit for test framework
- MockK for mocking
- Turbine for Flow testing

Example:
```kotlin
@Test
fun testInitialState() = runTest {
    val state = viewModel.uiState.value
    assertEquals("Welcome to UnTigritoApp", state.data)
}
```

### Instrumented Tests
Location: `app/src/androidTest/`
- Compose testing framework
- UI component testing
- Integration testing

Example:
```kotlin
@Test
fun homeScreen_displaysWelcomeText() {
    composeTestRule.setContent {
        UnTigritoTheme {
            HomeScreen(viewModel = HomeViewModel())
        }
    }
    composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
}
```

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew testDebugUnitTest connectedDebugAndroidTest
```

## 📚 Libraries & Frameworks

### UI & Composition
- **Jetpack Compose 2023.10.01**: Modern declarative UI
- **Material Design 3**: Latest Material design system
- **Jetpack Navigation**: In-app navigation

### Architecture
- **Hilt 2.48**: Dependency injection
- **ViewModel (Lifecycle 2.6.2)**: UI state management
- **Kotlin Coroutines**: Asynchronous programming

### Data
- **Room 2.6.1**: Local database
- **Retrofit 2.9.0**: HTTP client
- **OkHttp 4.11.0**: HTTP interceptor
- **Kotlinx Serialization 1.6.0**: JSON serialization
- **Supabase 2.0.0**: Backend as a Service (BaaS)
- **Ktor Client 2.3.6**: HTTP client para Supabase

### Image & Media
- **Coil 2.4.0**: Image loading

### Logging & Debugging
- **Timber 5.0.1**: Logging facade
- **Logcat**: Android logging

### Testing
- **JUnit 4.13.2**: Unit testing framework
- **MockK 1.13.8**: Kotlin mocking library
- **Turbine 1.0.0**: Flow testing
- **Espresso 3.5.1**: UI testing
- **Compose Test**: Compose UI testing

## 🎨 Theming

### Material Design 3

The app uses Material Design 3 with:
- Primary: Deep Purple (#6200EE)
- Secondary: Teal (#03DAC6)
- Error: Red (#B00020)
- Support for light and dark themes
- Dynamic color on Android 12+

### Theme Files
- `Color.kt`: Color palette definitions
- `Type.kt`: Typography scale
- `Theme.kt`: Theme composition

## 🔥 Supabase Integration

### Backend as a Service (BaaS)

El proyecto integra **Supabase** como backend alternativo/complementario, proporcionando:

- **Autenticación**: Email/password, OAuth (Google), MFA
- **Base de datos PostgreSQL**: Con Postgrest para consultas REST
- **Tiempo Real**: WebSockets para actualizaciones en vivo
- **Almacenamiento**: Bucket storage para archivos e imágenes
- **Row Level Security (RLS)**: Seguridad a nivel de fila

### Configuración Rápida

1. **Copia el archivo de ejemplo**:
   ```bash
   cp local.properties.example local.properties
   ```

2. **Obtén tus credenciales**:
   - Ve a [https://app.supabase.com](https://app.supabase.com)
   - Crea un proyecto o selecciona uno existente
   - Ve a `Settings` → `API`
   - Copia la URL y la clave anónima

3. **Configura en `local.properties`**:
   ```properties
   supabase.url=https://tu-proyecto.supabase.co
   supabase.anonKey=tu_clave_anon_key
   ```

4. **Ejecuta los scripts SQL**:
   - Abre el SQL Editor en Supabase
   - Ejecuta el contenido de `app/docs/supabase-setup.sql`

### Documentación

- **Guía completa**: `app/docs/SUPABASE_INTEGRATION.md`
- **Scripts SQL**: `app/docs/supabase-setup.sql`
- **Ejemplos de uso**: `SupabaseExampleViewModel.kt`

### Servicios Disponibles

```kotlin
// Autenticación
@Inject lateinit var supabaseAuth: SupabaseAuthService

// Base de datos
@Inject lateinit var supabaseDb: SupabaseDatabaseService

// Módulos directos
@Inject lateinit var auth: Auth
@Inject lateinit var postgrest: Postgrest
@Inject lateinit var storage: Storage
@Inject lateinit var realtime: Realtime
```

### Ejemplo de Uso

```kotlin
viewModelScope.launch {
    // Login
    supabaseAuth.signInWithEmail(email, password)
        .onSuccess { user ->
            println("Usuario: ${user?.email}")
        }
    
    // Consultar datos
    supabaseDb.getAll<Service>("services")
        .onSuccess { services ->
            _services.value = services
        }
}
```

## 🔒 Security

- EncryptedSharedPreferences for sensitive data
- TLS for network communications
- Certificate pinning (future phases)
- Input validation
- **Supabase RLS**: Row Level Security configurado
- **JWT tokens**: Manejo seguro de tokens de autenticación

## 📝 Code Style & Standards

The project follows:
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Development Best Practices](https://developer.android.com/)
- [Google Android Security Guide](https://developer.android.com/privacy-and-security)

### Key Standards
- **Clean Architecture**: Strict layer separation
- **MVVM Pattern**: ViewModel state management
- **Repository Pattern**: Data access abstraction
- **Dependency Injection**: Hilt for DI
- **Coroutines**: Async/reactive programming
- **Compose**: Modern UI framework

## 🚦 Build Variants

### Debug
- Debuggable with logging
- Timber logging enabled
- BuildConfig.DEBUG checks

### Release
- ProGuard minification
- Optimizations enabled
- No debug logging

## 📊 Performance Targets

- **Build Time**: < 30 seconds for clean builds
- **Code Coverage**: > 80% target
- **Linting Score**: > 90% target
- **Documentation**: > 70% coverage

## 🔄 Gradle Build Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run lint checks
./gradlew lint

# Format code with ktlint
./gradlew ktlintFormat

# Run all tests
./gradlew test connectedAndroidTest

# Generate build report
./gradlew assembleDebug --profile
```

## 🔗 Additional Resources

- **Agent-OS Standards**: See `agent-os/standards/` for coding standards
- **Product Documentation**: See `agent-os/product/` for mission and roadmap
- **Specification**: See `agent-os/specs/2025-10-17-new-feature/` for detailed spec

## 📋 Implementation Checklist

- [x] Project foundation setup (Gradle, version catalog)
- [x] Dependency injection (Hilt configuration)
- [x] Clean Architecture layers
- [x] Domain layer (Models, Repositories, Use Cases)
- [x] Data layer (Implementations, Data Sources)
- [x] Presentation layer (Composables, ViewModels)
- [x] UI Theme (Material Design 3)
- [x] Navigation (Compose Navigation)
- [x] Reusable Components
- [x] Testing infrastructure
- [x] Build configuration
- [x] **Supabase Integration** (Auth, Database, Realtime, Storage)

## 🤝 Contributing

When adding new features:
1. Follow Clean Architecture principles
2. Use Hilt for dependency injection
3. Implement proper state management with ViewModels
4. Write unit and UI tests
5. Follow the established code patterns
6. Update documentation

## 📄 License

This project is part of the UnTigritoApp ecosystem.

---

**Project Setup Version**: 1.0.0  
**Last Updated**: 2025-10-17  
**Status**: ✅ Ready for Development

