# Structure System: JSON Format Reference

This reference describes the JSON format used to define multiblock structures. Files should be placed in `config/omoshiroikamo/modular/structures/`.

## 1. File Structure
A file can contain a single object or an array of objects. A special object named `default` (or `defaults`) can be used to define shared mappings.

## 2. Main Entry Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | String | Unique identifier (required). |
| `displayName` | String | User-friendly name (optional). |
| `recipeGroup` | String/Array | The recipe groups this structure is compatible with. |
| `mappings` | Object | Character-to-block associations. |
| `layers` | Array | Vertical slices of the structure (bottom to top). |
| `requirements` | Array | Minimum functional needs (e.g., ports). |
| `controllerOffset` | Array `[x, y, z]` | Position of the controller relative to the start of the scan. |

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
