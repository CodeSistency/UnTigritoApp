## Android UI component best practices

- **Single Responsibility**: Each composable should have one clear purpose and do it well
- **Reusability**: Design composables to be reused across different contexts with configurable parameters
- **Composability**: Build complex UIs by combining smaller, simpler composables rather than monolithic structures
- **Clear Interface**: Define explicit, well-documented parameters with sensible defaults for ease of use
- **Encapsulation**: Keep internal implementation details private and expose only necessary APIs
- **Consistent Naming**: Use clear, descriptive names that indicate the composable's purpose and follow team conventions
- **State Management**: Keep state as local as possible; lift it up only when needed by multiple composables
- **Minimal Parameters**: Keep the number of parameters manageable; if a composable needs many parameters, consider composition or splitting it
- **Documentation**: Document composable usage, parameters, and provide @Preview examples for easier adoption
- **Material Design**: Follow Material Design 3 guidelines and use Material3 components consistently
- **Accessibility**: Include proper contentDescription, semantic properties, and test with TalkBack
