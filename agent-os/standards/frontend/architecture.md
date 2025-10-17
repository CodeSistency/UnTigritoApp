## Android Clean Architecture best practices

- **Module Separation**: Organize code into clear modules (core, feature-*, app) following Clean Architecture principles
- **Dependency Direction**: Dependencies should point inward (UI → Domain ← Data)
- **Use Cases**: Implement business logic in use cases (interactors) that are framework-independent
- **Repository Pattern**: Use repositories to abstract data sources and provide a clean API to the domain layer
- **MVVM Pattern**: Use ViewModel + StateFlow/SharedFlow for UI state management
- **Dependency Injection**: Use Hilt for dependency injection with clear scopes and qualifiers
- **Error Handling**: Implement Result<T> sealed classes for error handling across all layers
- **Testing**: Each layer should be independently testable with clear interfaces
- **Single Source of Truth**: State should flow unidirectionally from data sources to UI
- **Separation of Concerns**: Keep UI logic, business logic, and data access separate
