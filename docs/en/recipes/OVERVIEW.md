# Recipe System: Overview

The Modular Recipe System in OmoshiroiKamo is built to handle complex, multi-resource crafting that scales beyond traditional 1.7.10 recipe systems.

## 1. Core Concepts

- **Resource Abstraction**: Recipes do not depend on specific Minecraft objects like `ItemStack`. Instead, they use `IRecipeInput` and `IRecipeOutput`, which can represent anything (Items, Fluids, Energy, Mana, or custom NBT data).
- **Builder Pattern**: `ModularRecipe` instances are created via a `Builder`. This ensures that even with dozens of optional parameters (durations, priorities, multiple I/O), the code remains readable and type-safe.
- **Recipe Groups**: Every recipe belongs to a "Group" (e.g., `ore_miner`, `crusher`). Machines call for recipes within their specific group, allowing different machines to share or have unique recipe sets.

## 2. Processing Flow

1. **Match**: The `ProcessAgent` in the machine grabs all recipes in its group.
2. **Input Check**: Each recipe checks the machine's ports to see if requirements (Items, Energy, etc.) are met.
3. **Condition Check**: Any additional `ICondition` (e.g., must be raining, must be in The Nether) is evaluated.
4. **Execution**: If matched and the machine is started, the engine consumes inputs and, after a set `duration`, produces outputs.

## 3. Decoupled Architecture

The recipe engine doesn't know *how* to consume energy or fluids. It asks the `IRecipeInput` implementation to "process" itself against a list of `IModularPort`. This decoupling allows us to add support for new energy systems (like IC2 EU or Botania Mana) just by adding a new `IRecipeInput` type.
