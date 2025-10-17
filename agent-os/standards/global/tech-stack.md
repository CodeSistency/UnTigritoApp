## Tech stack

Define your technical stack below. This serves as a reference for all team members and helps maintain consistency across the project.

### Mobile App (Android)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt
- **Async Programming:** Kotlin Coroutines + Flow
- **Build System:** Gradle with Kotlin DSL

### Backend
- **Language:** Kotlin (Ktor) or TypeScript (NestJS)
- **Database:** PostgreSQL
- **ORM:** Exposed (Kotlin) or Prisma (TypeScript)
- **Hosting:** Render/Fly.io or GCP/AWS Lightsail

### Mobile App Libraries
- **Networking:** Ktor Client or Retrofit + OkHttp
- **Serialization:** Kotlinx Serialization or Moshi
- **Persistence:** Room (local cache)
- **Navigation:** Jetpack Navigation Compose
- **Notifications:** Firebase Cloud Messaging (FCM)
- **Image Loading:** Coil
- **Analytics:** Firebase Analytics

### Testing & Quality
- **Unit Testing:** JUnit, MockK
- **UI Testing:** Espresso
- **Flow Testing:** Turbine
- **Linting:** ktlint, detekt

### CI/CD & Infrastructure
- **CI/CD:** GitHub Actions
- **App Distribution:** Google Play Console
- **Monitoring:** Firebase Crashlytics
- **Feature Flags:** Firebase Remote Config

### Third-Party Services
- **Authentication:** JWT-based sessions, Google Sign-In
- **File Storage:** S3-compatible (Cloudflare R2/Backblaze)
- **Push Notifications:** Firebase Cloud Messaging
