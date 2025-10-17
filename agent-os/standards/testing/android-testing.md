## Android-specific testing guidelines

### Unit Testing
- **JUnit 5**: Use JUnit 5 for unit tests with proper test lifecycle management
- **MockK**: Use MockK for mocking Kotlin classes and functions
- **Test Structure**: Follow Given-When-Then pattern for clear test organization
- **Test Data**: Create test data builders and factories for consistent test data
- **Coroutines Testing**: Use runTest() for testing coroutines and suspend functions

### UI Testing
- **Espresso**: Use Espresso for UI interaction testing
- **Compose Testing**: Use Compose testing utilities for testing composables
- **Idling Resources**: Handle async operations properly in UI tests
- **Test Rules**: Use ActivityTestRule or ComposeTestRule for UI test setup
- **Screenshots**: Take screenshots for visual regression testing when needed

### Integration Testing
- **Repository Testing**: Test repository implementations with in-memory databases
- **API Testing**: Use MockWebServer for testing API integrations
- **Database Testing**: Use in-memory Room database for testing data layer
- **Navigation Testing**: Test navigation flows with Navigation Testing library

### Test Organization
- **Test Packages**: Mirror production package structure in test packages
- **Test Naming**: Use descriptive test method names that explain the scenario
- **Test Categories**: Separate unit, integration, and UI tests into different source sets
- **Test Fixtures**: Create shared test fixtures for common test setup

### Performance Testing
- **Benchmark Testing**: Use Macrobenchmark library for performance testing
- **Memory Testing**: Monitor memory usage in tests to catch leaks
- **Startup Testing**: Test app startup time and cold start performance
