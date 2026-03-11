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
## 4. File-Based Integration Testing

### Overview
Integrated tests use real JSON files as test resources. These files are loaded from the classpath to simulate a production-like environment.

### Test Resource Configuration
- **Location**: `src/test/resources/structures/`
- **Format**: Individual JSON files for each structure definition.
- **Count**: 14 structure definition files.

### Individual File List
1. `minimal.json` - Minimal structure (1x1x1)
2. `simple_3x3x1.json` - Simple 3x3 layout
3. `complex_3x3x3.json` - Complex 3-layer structure
4. `with_item_requirement.json` - Item requirement verification
5. `with_fluid_requirement.json` - Fluid requirement verification
6. `with_energy_requirement.json` - Energy requirement verification
7. `with_all_requirements.json` - Comprehensive test with 4 types of requirements
8. `with_display_name.json` - Optional metadata (displayName)
9. `with_recipe_group.json` - Optional metadata (recipeGroup)
10. `with_controller_offset.json` - Optional metadata (controllerOffset)
11. `with_tint_color.json` - Optional metadata (tintColor)
12. `with_tier.json` - Optional metadata (tier)
13. `with_multiple_blocks.json` - Multiple block mapping test
14. `with_metadata.json` - Block mapping with metadata/damage values

### Test Class: StructureFileLoaderTest
**Location**: `src/test/java/ruiseki/omoshiroikamo/api/structure/integration/StructureFileLoaderTest.java`

**Coverage**: 26 integration tests

**Key Verification Points**:
- Successful loading of name, layers, and mappings.
- Correct parsing of all optional metadata fields (displayName, recipeGroup, controllerOffset, tintColor, tier).
- Validation of various Requirement types (Item, Fluid, Energy, Mana).
- Proper handling of multi-block mappings and metadata-aware blocks.
- Support for `default mappings` fallback.

**Implementation Pattern**:
```java
@BeforeAll
public static void setUpAll() {
    structures = new HashMap<>();
    defaultMappings = new HashMap<>();

    // Read each file individually
    for (String filename : STRUCTURE_FILES) {
        InputStream inputStream = StructureFileLoaderTest.class
            .getResourceAsStream("/structures/" + filename);

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonElement element = new JsonParser().parse(new JsonReader(reader));
            StructureJsonReader.FileData data = StructureJsonReader.readFile(element);

            // Merge structures and mappings
            structures.putAll(data.structures);
            defaultMappings.putAll(data.defaultMappings);
        }
    }
}
```

**Key Design Principles**:
- **No Skip Policy**: Fail the test via `assertNotNull` if a resource file is missing.
- **Classpath Resources**: Load from test resources using `getResourceAsStream()`.
- **Individual File Format**: Manage each structure in its own file to improve maintainability.
- **Merge Pattern**: Consolidate structures loaded from multiple files into a single Map.
- **Compatibility**: Support Minecraft 1.7.10's legacy Gson API (`new JsonParser().parse(new JsonReader(reader))`).

This integration testing suite ensures that the JSON parsing logic remains robust when dealing with real-world file inputs.
