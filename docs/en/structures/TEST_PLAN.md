# Structure System: Test Plan

> [!IMPORTANT]
> **Language Note**: Please be aware that the actual test source code and in-code documentation (comments, display names) are currently provided in **Japanese only**. This document serves as a guide to understand the logic and strategy behind those tests.

## 1. Testing Strategy (7 Phases)

### Phase 1: JSON Integration (45 tests)
- **Basic Loading**: Validating `name`, `displayName`, `recipeGroup`, and basic metadata.
- **Layer Parsing**: Handling multiple layers, new/old formats, and empty rows.
- **Mapping Parsing**: Single/multiple block associations, wildcards, and reserved symbols.
- **Requirement Parsing**: All 7 resource types, min/max values, and registry integration.
- **Error Handling**: Missing mandatory fields, syntax errors, and invalid types.

### Phase 2: Structural Validation (40 tests)
- **Layer Consistency**: Row width mismatches, empty layers, and size limits (1x1x1 to 50x50x50).
- **Symbol Integrity**: Undefined symbols in layers, unused symbols in mappings, and controller (Q) presence.
- **Controller Detection**: Precise offset calculation and rotation (180°) handling.
- **Block Resolution**: Valid/invalid IDs and mod-specific block handling.

### Phase 3: Builder Pattern (25 tests)
- **Operation**: Null safety, mandatory fields, and fluent API behavior.
- **Immutability**: Ensuring built objects cannot be modified post-construction.

### Phase 4: Requirement System (30 tests)
- **Classes**: Individual logic for Item, Fluid, Energy, Mana, Gas, Essentia, and Vis.
- **Registry**: Dynamic registration, overwrite safety, and default value handling.

### Phase 5: Visitor Pattern (20 tests)
- **Traversal**: Full coverage of layers and requirements during a visit.
- **Validation Visitor**: Detailed error collection and reporting.

### Phase 6: Registry & Manager (30 tests)
- **CustomStructureRegistry**: Definition lookup and StructureLib integration.
- **StructureManager**: Singleton state and lifecycle management.

### Phase 7: Serialization (15 tests)
- **Round-trip**: Ensuring `JSON -> Object -> JSON` results in identical data.

## 2. Priority Matrix

- **CRITICAL**: JSON Loader, Undefined symbol detection, Controller offset calculation.
- **HIGH**: Builder validation, Requirement Registry, Validation Visitor.
- **MEDIUM**: Registry management, Serialization round-trips.

## 3. Implementation Guidelines
Tests are located in `src/test/java/ruiseki/omoshiroikamo/api/structure/`. Follow the Recipe System testing patterns (JUnit 5).
