# UnTigritoApp — Tech Stack (Android‑first, Clean Architecture)

## Mobile App (Android)
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: Clean Architecture + MVVM
- DI: Hilt
- Async: Kotlin Coroutines + Flow
- Networking: Ktor Client or Retrofit + OkHttp (decide at implementation)
- Serialization: Kotlinx Serialization (preferred) or Moshi
- Persistence: Room (for local cache)
- Navigation: Jetpack Navigation Compose
- Notifications: Firebase Cloud Messaging (FCM)
- Image: Coil
- Testing: JUnit, MockK, Turbine, Espresso


## Observability & Quality
- Analytics: Firebase Analytics + custom events
- Crash reporting: Firebase Crashlytics
- Logging: Kotlin logging façade with Logcat
- Feature flags: Firebase Remote Config or LaunchDarkly (later)
- CI/CD: GitHub Actions (build, lint, unit tests); Play Console tracks

## Security & Compliance
- Secure storage: EncryptedSharedPreferences / Jetpack Security
- Network: TLS, certificate pinning (later phases)
- PII: Minimize collection; implement account deletion/export

## Environments
- Dev, Staging, Prod with environment‑based configs

## Module Breakdown (App)
- core: models, result types, network/persistence abstractions
- feature-auth: sign‑up/login, role selection
- feature-jobs: posting, browsing, details
- feature-profile: professional profiles and services
- feature-chat: in‑app messaging
- feature-reviews: ratings & reviews


