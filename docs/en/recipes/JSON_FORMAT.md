# Recipe System: JSON Format Reference

Recipes are defined in `config/omoshiroikamo/recipes/*.json`. 

## 1. Recipe Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `machine` | String | The machine group name (required). |
| `registryName` | String | Unique ID for the recipe (required). |
| `name` | String | Display name (optional). |
| `time` | Integer | Duration in ticks (required). |
| `inputs` | Array | List of required resources. |
| `outputs` | Array | List of resources to produce. |
| `conditions` | Array | Special constraints (optional). |

## 2. Inputs and Outputs

Every item in `inputs` or `outputs` requires a `type`.

### Item Example
```json
{
  "type": "item",
  "id": "minecraft:iron_ingot",
  "amount": 1
}
```

### Fluid Example
```json
{
  "type": "fluid",
  "id": "water",
  "amount": 1000
}
```

### Energy Example
```json
{
  "type": "energy",
  "amount": 10000
}
```

## 3. Conditions
Conditions are checked every tick or at the start of the process.

```json
"conditions": [
  { "type": "weather", "weather": "rain" },
  { "type": "dimension", "dim": -1 }
]
```

## 4. Inheritance
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
