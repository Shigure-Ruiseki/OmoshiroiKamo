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
