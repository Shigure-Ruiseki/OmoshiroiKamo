# Structure System: JSON Format Reference

This reference describes the JSON format used to define multiblock structures. Files should be placed in `config/omoshiroikamo/modular/structures/`.

## 1. File Structure
A file can contain a single object or an array of objects. A special object named `default` (or `defaults`) can be used to define shared mappings.

## 2. Main Entry Properties

### Since 1.5.1.4, "properties" has been abolished! There is no backward compatibility! 
### Instead, please write it as follows

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | String | Unique identifier (required). |
| `displayName` | String | User-friendly name (optional). |
| `recipeGroup` | String/Array | The recipe groups this structure is compatible with. |
| `mappings` | Object | Character-to-block associations. |
| `layers` | Array | Vertical slices of the structure (top to bottom). |
| `requirements` | Array | Minimum functional needs (e.g., ports). |
| `tintColor` | String | RGB hex color for structure rendering (e.g., `#FF0000`). |
| `speedMultiplier` | Float | Multiplier for processing speed (default: 1.0). |
| `energyMultiplier` | Float | Multiplier for energy consumption (default: 1.0). |
| `batchMin` | Integer | Minimum batch size for recipes (default: 1). |
| `batchMax` | Integer | Maximum batch size for recipes (default: 1). |
| `tier` | Integer | Machine tier (default: 0). |
| `defaultFacing` | String | Default facing is horizontal. You can modify it to vertical (`UP`, `DOWN`). |

## 3. Mappings
Mappings link characters in `layers` to block IDs.

### String Format
`"F": "omoshiroikamo:basaltStructure:*"` (Wildcard `*` for meta)

### Object Format (Partial Implementation Planned)
```json
"Q": {
  "block": "omoshiroikamo:quantumOreExtractor:0",
  "max": 1  // * Currently not implemented. Planned to limit the maximum number of installations in the future.
}
```

### Multiple Choices
```json
"A": {
  "blocks": [
    "omoshiroikamo:modifierNull:0",
    "omoshiroikamo:modifierSpeed:0"
  ]
}
```

## 4. Requirements
Requirements define what internal components (Ports) the machine must have.

Available types: `itemInput`, `itemOutput`, `fluidInput`, `fluidOutput`, `energyInput`, `energyOutput`, `manaInput`, `manaOutput`, `gasInput`, `gasOutput`, `essentiaInput`, `essentiaOutput`, `visInput`, `visOutput`

### Array Format
```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

### Object Format
Since 1.5.1.4, an object format using type keys is also supported.
```json
"requirements": {
    "energyInput": { "min": 1 },
    "itemOutput": 1,
    "fluidInput": { "min": 1, "max": 4 }
}
```
* If the value is a number, it is treated as `min`.

## 5. Reserved Symbols

The following symbols have special meanings in the structure system.

### 5.1 System Reserved Symbols (Mandatory)
These symbols are used for core system functions and **cannot be overridden** in JSON `mappings`.

| Symbol | Meaning | Description |
| :--- | :--- | :--- |
| `Q` | Controller | Exactly one is required per structure. |
| `_` | Air | Treated as a forced air block. |
| (Space) | Any | Any block (ignored during validation). |

### 5.2 Conventional Reserved Symbols (Conditional)
`A`, `L`, and `G` are conventionally used by specific modules and behave differently depending on the structure type.

| Symbol | Meaning | In Internal Machines | In Modular Structures |
| :--- | :--- | :--- | :--- |
| `A` | Modifier | **Code Priority** | Overridable in JSON |
| `L` | Lens | **Code Priority** | Overridable in JSON |
| `G` | Solar Cell | **Code Priority** | Overridable in JSON |

> [!IMPORTANT]
> **In Internal Machines (Existing Multiblocks):**
> For machines like Solar Array or Extractor, these symbols are tied to internal logic (e.g., addon connectivity). Therefore, any definitions in JSON for these symbols will be skipped/protected by the system's code.
> 
> **In Modular Structures:**
> For new structures created in `modular/structures/`, you can freely define and use these symbols just like any other character (e.g., `B`, `C`, `X`).

## 6. Commands
- `/ok multiblock reload`: Reloads Multiblock module structures.
- `/ok modular reload`: Reloads Modular module recipe and structure data.
