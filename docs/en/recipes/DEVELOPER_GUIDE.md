# Recipe System: Developer Guide

This guide explains how to extend the recipe system via code.

## 1. Adding New I/O Types

To support a new resource (e.g., Thaumcraft Essentia):

1. **Implement `IRecipeInput` and `IRecipeOutput`**:
   Define how to check/consume and check/produce the specific resource.
2. **Register in `MachineryJsonReader`**:
   The `MachineryJsonReader` uses internal maps to handle resource types. Update the parsing logic to recognize your new `type` string.

## 2. Using Recipe Decorators

Decorators allow you to wrap a recipe with additional logic without changing the original recipe class. This is used for "Bonus Outputs" or "Efficiency Boosts".

```java
public class MyDecorator extends RecipeDecorator {
    public MyDecorator(IModularRecipe internal) { super(internal); }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        boolean success = super.processOutputs(outputPorts, simulate);
        if (success && !simulate) {
            // Apply bonus logic here
        }
        return success;
    }
}
```

## 3. Integration via API

The `RecipeLoader` is the central point for interacting with recipes.

- **`allRecipes()`**: Returns every registered recipe.
- **`getRecipesByGroup(String group)`**: Filter recipes for a specific machine.

## 4. Design Patterns in Use

- **Builder Pattern**: Used for constructing complex `ModularRecipe` objects.
- **Decorator Pattern**: Used for dynamic functional extensions.
- **Strategy Pattern**: `IRecipeInput` implementations act as strategies for resource handling.
- **Flyweight Pattern**: Common resource definitions are often shared to save memory.

## 5. Testing

The integrity of the recipe engine is verified through continuous integration tests. For details on test categories and building your own tests for new resources, see the [Test Plan](./TEST_PLAN.md).
