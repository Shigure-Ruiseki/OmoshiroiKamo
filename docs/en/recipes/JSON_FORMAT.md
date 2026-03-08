# Recipe System: JSON Format Reference

Recipes are defined in `config/omoshiroikamo/modular/recipes/*.json`. 

## 1. File Structure
You can define a single recipe object or a collection of recipes.

### Multiple Recipes Configuration (Recommended)
```json
{
  "group": "MachineName",
  "recipes": [
    { ... Recipe Definition 1 ... },
    { ... Recipe Definition 2 ... }
  ]
}
```

## 2. Recipe Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | String | Display name (optional). |
| `duration` | Integer | Duration in ticks. `time` is also accepted. |
| `priority` | Integer | Recipe priority. Higher values take precedence (default: 0). |
| `inputs` | Array | List of required resources. |
| `outputs` | Array | List of resources to produce. |
| `conditions` | Array | Special constraints. |
| `decorators` | Array | Decorators to extend recipe behavior. |

## 3. Inputs and Outputs

The resource type is determined by the presence of a specific key within the object.

### Items
- `item`: Block/Item ID.
- `amount`: Quantity.
- `meta`: Metadata (optional).
- `ore`: Ore Dictionary name (input only, used instead of `item`).

```json
{ 
  "item": "minecraft:coal", 
  "amount": 64 
}
```

### Fluids
- `fluid`: Fluid ID.
- `amount`: Milli-buckets count.

```json
{ 
  "fluid": "water", 
  "amount": 1000 
}
```

### Energy & Mana
- `energy` / `mana`: Amount.
- `perTick` / `pertick`: If true, resource is consumed/produced every tick instead of as a lump sum.

```json
{ 
  "energy": 100, 
  "perTick": true 
}
```

### Other Resources
- `gas`: Gas ID.
- `essentia`: Aspect name.
- `vis`: Aspect name.

```json
{ 
  "essentia": "ignis", 
  "amount": 10 
}
```

### Blocks
Detect/manipulate blocks at specific symbol positions within the structure. This mod uses a unified naming convention: **`replace` (Before)** and **`block` (After)**.

- `symbol`: The character symbol used in the structure definition.
- `replace`: (**Condition/Old block**) The block ID to target for manipulation.
- `block`: (**Result/New block**) The block ID that should finally be at the position.
- `consume`: (**Input only**) If true, automatically replaces the block with Air (clearing). No need to specify `block`.
- `optional`: If true, the recipe can start even if the target block is not found (executes if present).
- `amount`: The maximum number of blocks to target.
- `nbt`: (**Output only**) NBT data to apply to the placed block's TileEntity. Supports **Expression** values.

#### 7 Key Use Cases

| # | Case | I/O | Example Config | Behavior |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Exist Check** | `inputs` | `"block": "stone"` | Checks for Stone (not consumed). |
| 2 | **Mandatory Consume**| `inputs` | `"block": "stone", "consume": true` | Clears Stone (at start). |
| 3 | **Optional Consume** | `inputs` | `"consume": true, "optional": true` | Clears if present (at start). |
| 4 | **Input Replace** | `inputs` | `"replace": "A", "block": "B"` | Transforms A to B (at start). |
| 5 | **Output Placement** | `outputs`| `"block": "gold"` | Places Gold in air (at end). |
| 6 | **Output Replace** | `outputs`| `"replace": "stone", "block": "gold"` | Replaces Stone with Gold (at end). |
| 7 | **Optional Replace** | `outputs`| `"replace": "stone", "block": "gold", "optional": true`| Replaces if Stone exists (at end). |

#### Dynamic NBT Example
```json
"outputs": [{
  "symbol": "D",
  "block": "modid:battery",
  "nbt": {
    "energy": { "type": "nbt", "path": "machine_power" }
  }
}]
```

## 3. Conditions
Conditions are checked every tick or at the start of the process. Logical operators (CoR Pattern) can be used to construct complex conditions.

Available types:
- `dimension`: Is in a specific dimension.
- `biome`: Is in a specific biome.
- `block_below`: Is there a specific block below the machine.
- `tile_nbt`: Checks NBT values of the machine's TileEntity.
- `weather`: Checks the current weather. Values: `rain`, `thunder`, `clear`.
- `comparison`: Compares two expressions (`left`, `right`, `operator`).
- `expression`: Direct mathematical string expression. Recommended method.

```json
"conditions": [
  { "type": "dimension", "dim": -1 },
  { "type": "weather", "weather": "rain" },
  { "type": "expression", "expression": "day % 28 == 0" }
]
```

### Supported Logical Operators
- `and`, `or`, `not`, `xor`, `nand`, `nor`

## 4. Decorators
Decorators provide additional behavior during or after recipe execution.

- `chance`: Controls the success probability of the recipe.
- `bonus`: Gives a chance to produce extra outputs.
- `requirement`: Checks additional structural requirements during execution.
- `weighted_random`: Selects an output from a weighted pool.

```json
"decorators": [
  {
    "type": "chance",
    "chance": 0.5
  },
  {
    "type": "bonus",
    "chance": 0.1,
    "outputs": [{ "type": "item", "id": "minecraft:diamond", "amount": 1 }]
  }
]
```

## 5. Expressions (IExpression)
Some parameters (like decorator chances) can use `IExpression` to calculate values dynamically. Instead of a direct numeric constant, you can use the following object format:

- `constant`: Returns a fixed numeric value.
- `nbt`: Reads a value from the machine's TileEntity NBT path (e.g., `energyStored`).
- `map_range`: Maps a value from one range to another using linear interpolation.
- `arithmetic`: Performs operations between two expressions (`left`, `right`, `operation`: `+`, `-`, `*`, `/`, `%`).
- `world_property`: Retrieves world info (`time`, `day`, `moon_phase`).

### Expression String (Recipe Script)
Instead of deep JSON objects, you can write mathematical/logical expressions directly as strings. This supports complex logic and is referred to as **"Recipe Script"**.

- **Advanced Features**:
  - **Logical Operators**: Supports `&&` (AND), `||` (OR), `!` (NOT).
  - **Grouping**: Use `()` or `{}` to control precedence.
  - **Whitespace/Newlines**: You can use newlines and tabs to make scripts readable.

```json
"condition": "day % 28 == 0 && (time > 12000 || weather == 'rain')",
"chance": "{ nbt('energyStored') / 100000.0 } * 0.8"
```

## 6. Inheritance
You can use an `abstract` recipe to share common properties.

```json
{
  "registryName": "base_miner",
  "isAbstract": true,
  "time": 200,
  "inputs": [...]
}
```
Recipes can then use `"parent": "base_miner"` to inherit those values.
