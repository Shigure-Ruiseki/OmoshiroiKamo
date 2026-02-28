# Recipe System: Test Plan

> [!IMPORTANT]
> **Language Note**: Please be aware that the actual test source code and in-code documentation (comments, display names) are currently provided in **Japanese only**. This document serves as a guide to understand the logic and strategy behind those tests.

## 1. Core Testing Areas

### 1.1 Integrity & Logic
- **Matching**: Ensuring recipes correctly identify valid port configurations.
- **Processing**: Consumption of inputs and production of outputs (Simulated vs Real).
- **Priority**: Handling overlapping recipes in the same group.

### 1.2 Resource Handling (I/O)
- **Items**: Stack size compliance and NBT persistence.
- **Fluids**: Tank capacity and partial drain/fill logic.
- **Energy**: RF/EU buffer handling.
- **Custom**: Registry handling for Gas, Mana, and Essentia.

### 1.3 Decorators & Conditions
- **Decorators**: Wrapped logic for bonuses and efficiency.
- **Conditions**: Environment checks (Weather, Dimensions, Time).

## 2. Implementation Status
The Recipe System currently has a robust test suite. New integrations (like DML or Chickens) should add specific integration tests here.

## 3. Best Practices
- **Probability Tests**: For chance-based outputs, use 1000-iteration statistical checks.
- **Context Mocking**: Use a mock `ConditionContext` for environment-dependent tests.
## 4. File-Based Integration Testing

The Recipe System provides comprehensive integration tests that load real JSON files to verify the entire parsing and logic pipeline.

### 4.1 Test Resource Location
- **Path**: `src/test/resources/recipes`
- **Structure**: Recipes are organized into 5 functional groups:
  - `basic_processing.json`: Basic items, fluids, and energy.
  - `advanced_magic.json`: Mana, Essentia, and Vis.
  - `complex_io.json`: Multiple inputs/outputs.
  - `recipe_logic.json`: Priority, bulk consumption, and metadata.
  - `inheritance_test.json`: Parent-child relationships and abstract templates.

### 4.2 JSONLoaderIntegrationTest
Validates 20+ distinct recipe scenarios, ensuring high reliability:
- **I/O Verification**: Item (including OreDict and Metadata), Fluid, Energy, Mana, Gas, Essentia, and Vis.
- **Detailed Assertions**: Verifies `perTick` flags, `damage` values, and stack sizes.
- **Advanced Features**:
  - **Inheritance**: Property merging from `parent` recipes.
  - **Abstract Templates**: Ensuring `abstract: true` recipes are treated as templates and not loaded directly.
  - **Priority System**: Resolving recipe conflicts using the `priority` field.
- **Coverage**: All previous "ignored" placeholders have been implemented with full assertions.
- **Source**: `src/test/java/.../recipe/integration/JSONLoaderIntegrationTest.java`
