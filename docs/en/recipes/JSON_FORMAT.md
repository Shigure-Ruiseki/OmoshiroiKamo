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

## 3. Conditions
Conditions are checked every tick or at the start of the process. Logical operators (CoR Pattern) can be used to construct complex conditions.

Available types:
- `dimension`: Is in a specific dimension.
- `biome`: Is in a specific biome.
- `block_below`: Is there a specific block below the machine.
- `tile_nbt`: Checks NBT values of the machine's TileEntity.
- `weather`: * Planned implementation. Checks for weather like rain or thunder.

```json
"conditions": [
  { "type": "dimension", "dim": -1 },
  {
    "type": "or",
    "conditions": [
       { "type": "weather", "weather": "rain" },
       { "type": "weather", "weather": "thunder" }
    ]
  }
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

```json
"chance": {
  "type": "map_range",
  "input": { "type": "nbt", "path": "energyStored" },
  "inMin": 0, "inMax": 100000,
  "outMin": 0.1, "outMax": 1.0
}
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
