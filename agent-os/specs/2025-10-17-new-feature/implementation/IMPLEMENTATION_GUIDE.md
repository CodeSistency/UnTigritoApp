# Implementation Delegation Guide

## Overview

This guide outlines the implementation workflow for the UnTigritoApp Project Setup specification. The implementation is divided into task groups, each delegated to specialized subagents.

## Task Group Assignments

### Group 1: Project Foundation Setup
**Assigned to**: android-developer  
**Estimated Time**: 2-3 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 1.1 Configure Version Management (libs.versions.toml)
- 1.2 Configure Build System (gradle configuration)

**Deliverables**:
- Updated `gradle/libs.versions.toml` with centralized dependency management
- Updated `settings.gradle.kts` for project configuration
- Updated `app/build.gradle.kts` with proper plugin configuration and dependencies
- All versions referenced through version catalog (no hardcoded versions)

**Implementation Report**: `1_project_foundation_setup.md`

---

### Group 2: Dependency Injection Setup
**Assigned to**: android-developer  
**Estimated Time**: 1-2 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 2.1.1 Create Application class with @HiltAndroidApp
- 2.1.2 Create main DI module (AppModule.kt)
- 2.1.3 Create data layer DI module (DataModule.kt)

**Deliverables**:
- `App.kt` with @HiltAndroidApp annotation
- `di/AppModule.kt` with singleton providers
- `di/DataModule.kt` with data layer dependencies
- Updated AndroidManifest.xml with Application class
- All modules compile without errors

**Implementation Report**: `2_dependency_injection_setup.md`

---

### Group 3: Clean Architecture Structure
**Assigned to**: android-developer  
**Estimated Time**: 2-3 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 3.1.1 Create domain model classes
- 3.1.2 Create repository interfaces
- 3.1.3 Create use cases
- 3.2.1 Create repository implementations
- 3.2.2 Create data sources
- 3.3.1 Create ViewModels
- 3.3.2 Create Screen Composables

**Deliverables**:
- Domain layer: models, interfaces, use cases (pure Kotlin, no Android dependencies)
- Data layer: repository implementations, data sources (local/remote)
- Presentation layer: ViewModels (with Hilt injection), Screen Composables
- Proper separation of concerns with correct dependency directions

**Implementation Report**: `3_clean_architecture_structure.md`

---

### Group 4: UI and Navigation Setup
**Assigned to**: android-developer  
**Estimated Time**: 2-3 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 4.1.1 Create color definitions
- 4.1.2 Create typography system
- 4.1.3 Create main theme configuration
- 4.2.1 Create navigation graph
- 4.2.2 Integrate navigation with MainActivity

**Deliverables**:
- Material Design 3 compliant theme system
- Color definitions following Material guidelines
- Typography system with proper text styles
- Navigation graph with route definitions
- Integration in MainActivity
- Proper light/dark theme support

**Implementation Report**: `4_ui_navigation_setup.md`

---

### Group 5: Component Architecture
**Assigned to**: android-developer  
**Estimated Time**: 1-2 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 5.1.1 Create common UI components
- 5.1.2 Create feature-specific components

**Deliverables**:
- Reusable Composable components in `presentation/components/common/`
- Feature-specific components in `presentation/components/ui/`
- Components properly typed and state-managed
- Follows Compose best practices

**Implementation Report**: `5_component_architecture.md`

---

### Group 6: Testing Infrastructure
**Assigned to**: testing-engineer  
**Estimated Time**: 1-2 hours  
**Status**: Awaiting Implementation

**Tasks**:
- 6.1.1 Add testing libraries to version catalog
- 6.1.2 Configure test build variants
- 6.2.1 Create test directories
- 6.2.2 Create basic test examples

**Deliverables**:
- Testing libraries added to version catalog (JUnit, MockK, Turbine, Espresso)
- Proper test source sets configured
- Test directory structure created
- Basic test examples for ViewModel and UI
- Tests compile and run

**Implementation Report**: `6_testing_infrastructure.md`

---

### Group 7: Quality Assurance
**Assigned to**: android-developer  
**Estimated Time**: 1 hour  
**Status**: Awaiting Implementation

**Tasks**:
- 7.1.1 Set up linting rules
- 7.1.2 Configure code formatting
- 7.2.1 Create project documentation
- 7.2.2 Add code comments

**Deliverables**:
- Linting rules configured and enforced
- Code formatting configured (ktlint or similar)
- Project documentation created
- Key classes documented with meaningful comments

**Implementation Report**: `7_quality_assurance.md`

---

## Implementation Workflow

### For Each Subagent:

1. **Review Requirements**
   - Read the spec file: `agent-os/specs/2025-10-17-new-feature/spec.md`
   - Review assigned task groups in tasks.md
   - Check task-assignments.yml for your role

2. **Implement Tasks**
   - Follow the detailed acceptance criteria for each task
   - Implement code following established patterns and standards
   - Ensure proper error handling and validation
   - Write clean, well-documented code

3. **Update Task Status**
   - Check off completed tasks in `agent-os/specs/2025-10-17-new-feature/tasks.md`
   - Use [ ] for pending and [x] for completed
   - Include timestamps and implementer notes

4. **Document Implementation**
   - Create numbered implementation report (e.g., `1_project_foundation_setup.md`)
   - Place in `agent-os/specs/2025-10-17-new-feature/implementation/`
   - Include:
     - What was implemented
     - Any challenges encountered
     - Files created/modified
     - Links to code
     - Test results

## Acceptance Criteria Checklist

### For All Groups:
- [ ] Code compiles without errors
- [ ] Follows established coding standards
- [ ] Follows Clean Architecture principles (where applicable)
- [ ] Proper error handling implemented
- [ ] Code is well-documented with comments
- [ ] Changes are tracked and documented

### Group-Specific Success Criteria:
- Group 1: Build time < 30 seconds, all versions centralized
- Group 2: Hilt initializes correctly, dependencies injectable
- Group 3: Clean separation of layers, no inappropriate dependencies
- Group 4: Theme applies correctly, navigation works seamlessly
- Group 5: Components are reusable and testable
- Group 6: All tests pass, coverage baseline established
- Group 7: Code follows standards, documentation is complete

## Questions & Support

- Refer to spec.md for technical details
- Check task-assignments.yml for role clarity
- Review standards/ directory for coding guidelines
- Verify architecture patterns before implementation

---

**Status**: Awaiting subagent implementations
