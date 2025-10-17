## Android testing best practices

- **Write Minimal Tests During Development**: Do NOT write tests for every change or intermediate step. Focus on completing the feature implementation first, then add strategic tests only at logical completion points
- **Test Only Core User Flows**: Write tests exclusively for critical paths and primary user workflows. Skip writing tests for non-critical utilities and secondary workflows until if/when you're instructed to do so.
- **Defer Edge Case Testing**: Do NOT test edge cases, error states, or validation logic unless they are business-critical. These can be addressed in dedicated testing phases, not during feature development.
- **Test Behavior, Not Implementation**: Focus tests on what the code does, not how it does it, to reduce brittleness
- **Clear Test Names**: Use descriptive names that explain what's being tested and the expected outcome
- **Mock External Dependencies**: Isolate units by mocking databases, APIs, repositories, and other external services
- **Fast Execution**: Keep unit tests fast (milliseconds) so developers run them frequently during development
- **Android-Specific Testing**: Use JUnit for unit tests, MockK for mocking, and Espresso for UI tests
- **Compose Testing**: Test composables with Compose testing utilities and @Preview functions
- **Flow Testing**: Use Turbine for testing Kotlin Flows and StateFlow
- **Repository Testing**: Mock data sources and test repository implementations
- **ViewModel Testing**: Test ViewModel state changes and business logic
- **Navigation Testing**: Test navigation flows with Navigation Testing library
