# Structure System: JSON Format Reference

This reference describes the JSON format used to define multiblock structures. Files should be placed in `config/omoshiroikamo/modular/structures/`.

## 1. File Structure
A file can contain a single object or an array of objects. A special object named `default` (or `defaults`) can be used to define shared mappings.

## 2. Main Entry Properties

### ※Since 1.5.1.4, "properties" has been abolished! There is no backward compatibility! 
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

## 3. Mappings
Mappings link characters in `layers` to block IDs.

### String Format
`"F": "omoshiroikamo:basaltStructure:*"` (Wildcard `*` for meta)

### Object Format (with limits)
```json
"Q": {
  "block": "omoshiroikamo:quantumOreExtractor:0",
  "max": 1
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

```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

## 5. Reserved Symbols
- **`Q`**: Controller (usually required).
- **`_`**: Forced Air.
- **` ` (Space)**: Any block (ignored by scan).
- **`A`, `L`, `G`**: Legacy/Predefined slots for commonly used modifiers/lenses.

## 6. Commands
- `/ok multiblock reload`: Reloads fixed structures.
- `/ok modular reload`: Reloads recipe and custom structure data.
