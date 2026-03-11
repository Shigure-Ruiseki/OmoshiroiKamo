# Structure System: Developer Guide

This guide is for developers looking to extend the Structure JSON System via code.

## 1. Registering New Requirements

To add a new type of requirement (e.g., `manaInput` from Botania):

1. **Implement `IStructureRequirement`**:
   Create a class that handles the counting logic.
   ```java
   public class ManaRequirement implements IStructureRequirement {
       private final int min;
       public ManaRequirement(int min) { this.min = min; }
       
       @Override
       public boolean isMet(List<IModularPort> ports) {
           return ports.stream().filter(p -> p instanceof IManaPort).count() >= min;
       }
       
       public static ManaRequirement fromJson(String type, JsonObject json) {
           return new ManaRequirement(json.get("min").getAsInt());
       }
   }
   ```

2. **Register the Parser**:
   Do this during `FMLPreInitializationEvent` or `FMLInitializationEvent`.
   ```java
   RequirementRegistry.register("manaInput", ManaRequirement::fromJson);
   ```

## 2. Using Visitors

The Visitor pattern allows you to traverse a structure and perform actions.

### Example: Tier Scanning
If you want to determine the "Tier" based on how many elite blocks are in the structure:

```java
public class TierScannerVisitor implements IStructureVisitor {
    private int eliteCount = 0;

    @Override
    public void visit(IStructureEntry entry) {
        // You can iterate layers and mappings here
    }

    // Usually called during a world scan via StructureLib integration
    public void onBlockFound(Block block, int meta) {
        if (block == ModBlocks.eliteCasing) eliteCount++;
    }
}
```

## 3. Best Practices

- **Registry Order**: Ensure all requirements are registered before any JSON loading occurs (usually before `postInit`).
- **Encapsulation**: Use `StructureEntryBuilder` instead of creating `StructureEntry` instances directly.
- **Fail Fast**: Use the validation visitor after loading a JSON to catch syntax and logic errors early.

## 4. JSON Reader & Writer

The system provides a unified mechanism for converting between JSON and `StructureEntry` objects.

### StructureJsonReader
Reads JSON elements (entire files or individual structure objects) and converts them into internal objects.
- `readFile(JsonElement)`: Reads an entire file and returns `FileData` containing a list of structures and default mappings.
- `readStructure(JsonObject, Map<String, String>)`: Parses an individual structure object.

### StructureJsonWriter
Serializes `StructureEntry` objects back into `JsonObject`.
- `writeStructure(IStructureEntry)`: Converts a structure definition into JSON format.

Using these classes ensures consistency and avoids manual JSON parsing bugs.

## 4. Testing

To maintain long-term stability, we follow a rigorous testing strategy with over 220 test cases. See the [Test Plan](./TEST_PLAN.md) for detailed test phases and implementation guidelines.
