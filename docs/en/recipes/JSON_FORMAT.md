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

| `decorators` | Array | Decorators to extend recipe behavior. |
| `requiredTier` | Object | Required component Tiers (e.g., `{"glass": 1, "casing": 3}`). |

## 2.1 Recipe Priority and Sorting
Recipes are evaluated and displayed in the following order (higher items take precedence):
1. **Max Required Tier**: Recipes requiring higher Tiers take the highest precedence.
2. **Priority (`priority`)**: If the max Tiers are equal, higher priority values take precedence.
3. **Input Type Count**: Recipes requiring more diverse resource types take precedence.
4. **Total Item Input Count**: Recipes requiring a larger total quantity of items take precedence.

## 3. Inputs and Outputs

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

## 3.1 Dynamic Amount

The `amount` field in inputs and outputs can accept **expressions** instead of fixed values.
Using expressions allows you to dynamically adjust quantities based on machine state or world environment.

### Basic Usage

```json
{
  "inputs": [
    {
      "item": "minecraft:iron_ingot",
      "amount": "tier * 10 + 5"
    }
  ],
  "outputs": [
    {
      "fluid": "water",
      "amount": "energy_p * 1000"
    }
  ]
}
```

In the above example:
- **Input**: Requires 15 items at Tier 1, 55 items at Tier 5
- **Output**: Output amount varies with energy fill percentage (1000mB at full, 500mB at 50%)

### Available Variables and Functions

For a complete list of available variables and functions, refer to [ArithmeticParserFields.md](../../../run/ArithmeticParserFields.md).

**Main Machine Properties**:
- `tier` - Current machine Tier (1-16)
- `energy` / `energy_p` - Energy amount / fill percentage (0.0-1.0)
- `fluid` / `fluid_p` - Fluid amount / fill percentage
- `mana` / `mana_p` - Mana amount / fill percentage
- `gas` / `gas_p` - Gas amount / fill percentage
- `progress` - Recipe progress (0.0-1.0)
- `recipeprocessed` - Number of processed recipes (statistics)
- `is_running` - Whether machine is running (1 or 0)
- `batch` - Current batch size (number of times processed at once)
- `speed_multi` - Speed multiplier (inverse of processing time)
- `energy_multi` - Energy multiplier (consumption multiplier)

**Main World Properties**:
- `day` / `total_days` - Elapsed days
- `time` - Current time (0-23999)
- `moon_phase` - Moon phase (0-7)

**Math Functions**:
- `min(a, b)` / `max(a, b)` - Minimum/Maximum value
- `floor(x)` / `ceil(x)` / `round(x)` - Floor/Ceiling/Rounding
- `sqrt(x)` / `pow(base, exp)` - Square root/Power
- `sin(x)` / `cos(x)` - Trigonometric functions (radians)

### Practical Examples

#### Example 1: Tier-Dependent Input Amount
```json
{
  "inputs": [
    { "item": "minecraft:diamond", "amount": "tier * 2" }
  ]
}
```
Requires 2 diamonds at Tier 1, 16 diamonds at Tier 8.

#### Example 2: Energy-Based Output
```json
{
  "outputs": [
    { "fluid": "steam", "amount": "energy_p * 10000" }
  ]
}
```
Produces 10,000mB at full energy, 5,000mB at 50% energy.

#### Example 3: Time-of-Day Production Variance
```json
{
  "outputs": [
    {
      "item": "minecraft:glowstone_dust",
      "amount": "time > 13000 && time < 23000 ? 10 : 5"
    }
  ]
}
```
Produces 10 items at night (13000-23000 ticks), 5 items during daytime.

#### Example 4: Progress-Based Bonus
```json
{
  "outputs": [
    {
      "item": "output_item",
      "amount": "10 + floor(recipeprocessed / 100)"
    }
  ]
}
```
Output increases by 1 every 100 recipe completions.

#### Example 5: Multi-Condition Combination
```json
{
  "inputs": [
    {
      "item": "fuel",
      "amount": "max(1, tier * 5 - floor(energy_p * 20))"
    }
  ]
}
```
Higher tiers require more fuel, but less fuel is needed when energy is high. Minimum 1 required.

### Notes

- Expression results are rounded to integers (fractional parts are truncated)
- Negative values are treated as 0
- If an expression is invalid, the fixed `amount` value is used as a fallback
- Ternary operator `? :` can be used for conditional branching
- Logical operators `&&` (AND), `||` (OR) are available

---

### 11. External Block NBT Check/Consume (Block Nbt Input)
Assess and consume NBT data from blocks within the structure at recipe start.

- `type`: `"block_nbt"`
- `symbol`: The target symbol.
- `key`: The NBT key to check.
- `operation`: (`"sub"` | `"set"` | `"add"`). `"sub"` prevents start if value is insufficient.
- `value`: Numeric constant or Expression.
- `consume`: If true (default), actually modifies NBT when recipe starts.
- `optional`: If true, allows recipe start even if the target block or NBT key is missing. If false (default), missing targets prevent the recipe from starting.

```json
"inputs": [{
  "type": "block_nbt",
  "symbol": "S",
  "key": "stored_energy",
  "operation": "sub",
  "value": 100
}]
```

### Blocks
Detect/manipulate blocks at specific symbol positions within the structure. This mod uses a unified naming convention: **`replace` (Before)** and **`block` (After)**.
(Note) Some TileEntities cause crashes when placed. (Confirmed with Beacon in Angelica + ETFuturm setup)
If you find a bug, please create an issue.

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

#### 8. External Block NBT Manipulation (Block Nbt Output)
Manipulate NBT data of any TileEntity within the structure. Unlike `block` replacement, this modifies internal data numerically without changing the block itself.

- `type`: Specify `"block_nbt"`.
- `symbol`: The symbol target.
- `key`: The target NBT key.
- `operation`: The operation type (`"set"`, `"add"`, `"sub"`)。
- `value`: The numeric value or Expression for the operation.
- `optional`: If true, failure to find the target block or NBT key will not block recipe completion. If false (default), missing targets will block the recipe (treated as insufficient capacity).

```json
"outputs": [{
  "type": "block_nbt",
  "symbol": "S",
  "key": "stored_energy",
  "operation": "add",
  "value": 1000
}]
```

## 3. Conditions
Conditions are checked every tick or at the start of the process. Logical operators (CoR Pattern) can be used to construct complex conditions.

Available types:
(Note: If the `type` property is omitted, the type is automatically inferred from the keys used.)

- **`block` (Recommended)**: Checks for a block at the machine's current position. (`block`: string ID or object)
- `dimension`: Checks if the machine is in a specific dimension.
    - **Shorthand**: `{ "dimension": 0 }`
    - `ids`: Array of numeric IDs (Legacy format).
- `biome`: Assesses biome names, tags, or environmental values.
    - **Shorthand**: `{ "biome": "Plains" }` / `{ "tag": "FOREST" }`
    - `biomes`: Array of biome names.
    - `tags`: Array of Forge BiomeDictionary tags.
    - `minTemp` / `maxTemp`: Temperature range check (optional).
    - `minHumid` / `maxHumid`: Humidity range check (optional).
- `offset`: Wraps another condition to be checked at a relative offset `(dx, dy, dz)`.
    - `dx`, `dy`, `dz`: Relative coordinates.
    - `condition`: The condition object to execute.
- `pattern`: Checks biome layout using a grid pattern.
    - `pattern`: Array of strings.
    - `keys`: Mapping of pattern characters to condition objects.
- `block_below`: Checks for a specific block below the machine (Y-1). Using `offset` + `block` is now recommended instead.
- `tile_nbt`: Checks NBT values of the machine's TileEntity.
- `weather`: Checks current weather. (`rain`, `thunder`, `clear`)
- `comparison`: Compares two expressions (`left`, `right`, `operator`).
- `expression`: Direct mathematical string expression.

```json
"conditions": [
  { 
    "pattern": [ "FFF", "F#F", "FFF" ],
    "keys": {
      "#": { "biome": "Plains" },
      "F": { "tag": "FOREST" }
    }
  },
  { "weather": "rain" },
  { "expression": "day % 28 == 0" }
]
```

### Supported Logical Operators (Shortcuts)
You can use the operator name directly as the key.
- **`and`**: `{ "and": [ { condition1 }, { condition2 } ] }`
- **`or`**: `{ "or": [ ... ] }`
- **`not`**: `{ "not": { condition } }`
- `xor`, `nand`, `nor` are also supported.

## 4. Decorators
Decorators provide additional behavior during or after recipe execution.
(Note: If the `type` property is omitted, the type is inferred from the keys used.)

- `chance`: Controls the success probability of the recipe.
- `bonus`: Gives a chance to produce extra outputs.
- `requirement`: Checks additional structural requirements during execution.
- `weighted_random`: Selects an output from a weighted pool.

```json
"decorators": [
  {
    "chance": 0.5
  },
  {
    "bonus": {
      "chance": 0.1,
      "outputs": [{ "item": "minecraft:diamond", "amount": 1 }]
    }
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

- **Advanced Functions**:
  - `nbt('key')`: Retrieves machine's own NBT.
  - `nbt('S', 'key')`: Retrieves NBT from the block at symbol `S`.

```json
"condition": "nbt('S', 'energy') > 5000",
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
