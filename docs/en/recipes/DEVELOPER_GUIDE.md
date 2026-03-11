# Recipe System: Developer Guide

This guide explains how to extend the recipe system via code.

## 1. Adding New Ports and Resource Types

To support a new resource (e.g., Thaumcraft Essentia or new energy forms):

1.  **Define in `IPortType.Type`**:
    Add your new constant to the `Type` enum in `ruiseki.omoshiroikamo.api.modular.IPortType`.
2.  **Create a Custom Port Interface**:
    Inherit from `IModularPort` and define how your resource is stored and transferred.
3.  **Implement the TileEntity**:
    Extend `AbstractModularPortTE` (or concrete base classes like `AbstractItemIOPortTE` / `AbstractEnergyIOPortTE`) to create the actual TileEntity.
4.  **Implement `IRecipeInput` / `IRecipeOutput`**:
    Extend `AbstractRecipeInput` / `AbstractRecipeOutput` to define resource availability checks and consumption/production logic during recipe processing.
5.  **Register Parsers**:
    Register the association between your JSON keys and parsing functions in the `static` blocks of `InputParserRegistry` and `OutputParserRegistry` (or during initialization).
    ```java
    InputParserRegistry.register("my_resource", MyResourceInput::fromJson);
    ```

## 2. Logical Conditions (CoR Pattern)

Recipes can impose complex constraints by combining multiple conditions using logical operators. This is implemented using a structure similar to the Chain of Responsibility (CoR) pattern.

### Implementing `ICondition`
To create a new condition type, implement the `ICondition` interface.
```java
public class MyCondition implements ICondition {
    @Override
    public boolean isMet(ConditionContext context) {
        // Condition logic
        return true;
    }
}
```

### Logical Operators
The following operators are provided out-of-the-box and can be nested in JSON to construct complex logic:
- `OpAnd` (AND) / `OpOr` (OR) / `OpNot` (NOT)
- `OpXor` / `OpNand` / `OpNor`

These are registered in `ConditionParserRegistry` and initialized via `Conditions.registerDefaults()`.

## 3. Using Recipe Decorators

Decorators allow you to dynamically add behaviors like "chance-based success" or "bonus items" to existing recipes.

- `ChanceRecipeDecorator`: Controls recipe success based on probability.
- `BonusOutputDecorator`: Produces extra outputs based on a chance.
- `WeightedRandomDecorator`: Selects an output from a weighted pool.

```java
public class MyDecorator extends RecipeDecorator {
    public MyDecorator(IModularRecipe internal) { super(internal); }
    // Override processOutputs, etc., to customize behavior.
}
```

## 4. Error Handling & Validation

JSON parsing errors and structural inconsistencies are managed by a centralized error collection system.

- **`JsonErrorCollector`**: A singleton that collects all parsing errors.
- **Error Output**: Collected errors are written to `config/omoshiroikamo/json_errors.txt`.
- **User Notification**: If errors are detected, a warning message is displayed in the chat when a player logs in.

Developers can utilize this by calling `JsonErrorCollector.getInstance().collect(type, message)` within their parsing logic.

## 5. JSON Framework (Readers & Writers)

The project provides `AbstractJsonReader` and `AbstractJsonWriter` to unify data handling.

- **`AbstractJsonReader<T>`**:
  - `read()`: Scans all JSON files in a directory and caches objects.
  - `ParsingContext`: Maintains info about the file currently being processed, making it easier to pinpoint the source of errors.
- **`AbstractJsonWriter<T>`**:
  - `write()`: Serializes objects into formatted JSON files.

## 6. Design Patterns in Use

- **Builder Pattern**: Constructing `ModularRecipe` objects.
- **Decorator Pattern**: Dynamic functional extensions.
- **Strategy Pattern**: Decoupling logic via `IRecipeInput` / `IExpression`.
- **Visitor Pattern**: Traversal for validation and serialization.
- **Flyweight Pattern**: Sharing common resource definitions.

## 7. Testing

The integrity of the recipe engine is verified through JUnit 5 tests like `RecipeLoaderTest`. For more details, see the [Test Plan](./TEST_PLAN.md).
