## Jetpack Compose UI best practices

- **Composable Functions**: Write small, focused composable functions that do one thing well
- **State Hoisting**: Lift state up to the nearest common ancestor that needs it; keep composables stateless when possible
- **Reusable Components**: Create reusable composables with clear, well-documented parameters and sensible defaults
- **Material Design**: Follow Material Design 3 principles and use Material3 components consistently
- **Theme System**: Use MaterialTheme and custom design tokens for colors, typography, and spacing
- **Preview Functions**: Always include @Preview composables for visual testing and documentation
- **Performance**: Use remember, derivedStateOf, and LaunchedEffect appropriately to avoid unnecessary recompositions
- **Navigation**: Use Jetpack Navigation Compose with type-safe arguments and clear navigation graphs
- **Accessibility**: Add contentDescription, semantic properties, and test with TalkBack
- **Responsive Design**: Use adaptive layouts with WindowSizeClass for different screen sizes
- **Error Boundaries**: Handle UI errors gracefully with error states and recovery options
