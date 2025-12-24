# Custom Structure System

OmoshiroiKamo allows players and modpack makers to customize the multiblock structures of its machines using JSON files. This system provides flexibility to change the appearance and material requirements of multiblocks without needing to update the mod itself.

## Configuration Location

The structure configuration files are located in the `config` folder of your Minecraft instance:

`config/omoshiroikamo/structures/`

Upon the first launch, the mod will generate default JSON files for all supported machines:

*   `ore_miner.json` - For the Quantum Ore Extractor
*   `res_miner.json` - For the Quantum Resource Extractor
*   `solar_array.json` - For the Solar Array
*   `quantum_beacon.json` - For the Quantum Beacon

## JSON File Format

Each JSON file contains a list of structure definitions. A structure definition consists of a **name**, **layers**, and **mappings**.

### 1. Structure Entry

A typical entry looks like this:

```json
{
  "name": "oreExtractorTier1",
  "layers": [ ... ],
  "mappings": { ... }
}
```

*   **name**: The unique identifier for the structure (e.g., `oreExtractorTier1`, `solarArrayTier3`). Do not change this if you want to override the default structure for a specific machine tier.
*   **layers**: Defines the physical shape of the structure using character symbols.
*   **mappings**: Defines what blocks each character symbol represents.

### 2. Layers

The `layers` field is an array of layer objects. Each layer represents a vertical slice of the multiblock, starting from the bottom (layer 0).

```json
"layers": [
  {
    "name": "layer0",
    "rows": [
      "FFFFF",
      "F___F",
      "F_P_F",
      "F___F",
      "FFFFF"
    ]
  },
  ...
]
```

*   **rows**: An array of strings where each character represents a block position.
*   All rows in a layer must have the same length.
*   The symbol `_` is reserved for **Air** (empty space).

### 3. Mappings

The `mappings` object links the characters used in `layers` to actual in-game blocks.

There are two ways to define a mapping:

#### A. Single Block
Maps a symbol to a specific block ID.

```json
"F": "omoshiroikamo:basaltStructure:0"
```

Or, using the object format to enforce a limit:

```json
"Q": {
  "block": "omoshiroikamo:quantumOreExtractor:0",
  "max": 1
}
```

*   **block**: The resource location of the block (e.g., `modid:blockname:meta`).
*   **max** (Optional): The maximum number of times this block can appear in the structure. Useful for controllers.

#### B. Multiple Choices (Candidates)
Maps a symbol to a list of allowed blocks. The structure check will pass if *any* of the listed blocks are found at that position.

```json
"A": {
  "blocks": [
    { "id": "omoshiroikamo:modifierNull:0" },
    { "id": "omoshiroikamo:modifierSpeed:0" },
    { "id": "omoshiroikamo:modifierAccuracy:0" }
  ]
}
```

*   **blocks**: A list of block objects.
*   **id**: The resource location of the block.
*   Wildcards `*` can be used for metadata (e.g., `minecraft:wool:*` allows any color of wool).

## Default Mappings

Each file can contain a special entry named `default`. The mappings defined here are applied to **all** structures in that file, unless overridden by a specific structure entry.

```json
{
  "name": "default",
  "mappings": {
    "_": "air",
    "F": "omoshiroikamo:basaltStructure:*"
  }
}
```

## Example: Solar Array Tier 1

Here is a simplified example of what a valid JSON definition might look like for a small structure.

```json
[
  {
    "name": "default",
    "mappings": {
      "_": "air",
      "Q": { "block": "omoshiroikamo:solarArray:0", "max": 1 },
      "G": { "block": "omoshiroikamo:solarCell:0" }
    }
  },
  {
    "name": "solarArrayTier1",
    "layers": [
      {
        "rows": [
          "GGG",
          "GQG",
          "GGG"
        ]
      }
    ]
  }
]
```

In this example:
*   `_` is Air.
*   `Q` is the Solar Array Controller (limit 1).
*   `G` is a Solar Cell.
*   The structure is a 3x3 grid of Solar Cells with the Controller in the center.

## Reserved Symbols

Some symbols have hardcoded meanings in the mod and **cannot** be redefined in JSON. Even if you define them in the `mappings` section, your definition will be ignored (though their placement in `layers` will still be respected).

*   **`Q`**: Structure Controller. Must appear exactly once in the structure.
*   **`_`** (Underscore): Mandatory Air. The structure check will fail if any block is present.
*   **` `** (Space): Any block. The structure check ignores this position (usually used for air, but does not enforce it).
*   **`A`**: Modifier Slot. Used for Speed/Accuracy modifiers.
*   **`L`**: Lens Slot. Used for the laser lens.
*   **`G`**: Solar Cell Slot. Used in the Solar Array.

## In-Game Reloading

You can reload structure definitions without restarting the game. This allows you to tweak shapes and mappings on the fly.

**Command:** `/ok structure reload`

> [!NOTE]
> Only **non-reserved** symbols can have their definitions reloaded.
> 
> *   You **CAN** change what `F` maps to (e.g., change Basalt to Obsidian) and see it update immediately.
> *   You **CAN** change the shape (move `Q` or `A` to a different spot) and see it update immediately.
> *   You **CANNOT** change the functionality of `Q` or `A` (e.g., making `A` accept a different customized block list via JSON won't work because `A` is reserved).

## Validation & Errors

*   Errors found during loading (e.g., invalid JSON syntax, missing blocks) will be logged to `log.txt`.
*   A dedicated error report might be generated in `config/omoshiroikamo/error.txt` if severe issues occur.
## Structure Tools & Commands

### Checking Status
To check if the structure system is initialized and if there are any errors:

**Command:** `/ok structure status`

This will display:
*   Initialization status.
*   Total number of errors found (if any).

### The Structure Wand
The **Structure Wand** is a tool used to scan in-game structures and save them to JSON files.

**Usage:**
*   **Right-Click**: Set Position 1.
*   **Shift + Right-Click**: Set Position 2.
*   **Shift + Left-Click**: Clear selection.

Once you have selected a region (Position 1 and Position 2), you can save it to a file.

**Command:** `/ok structure wand save <name>`

*   This will scan the selected area and save it to `config/omoshiroikamo/structures/custom/<name>.json`.
*   It automatically detects the block at the Controller position as 'Q' (if a valid controller exists).

### Manual Scanning
If you prefer not to use the wand or need to scan via command block/console, you can use the direct scan command.

**Command:** `/ok structure scan <name> <x1> <y1> <z1> <x2> <y2> <z2>`

*   **name**: The name of the file to save (will be saved in `custom/` folder).
*   **x1 y1 z1**: Coordinates of the first corner.
*   **x2 y2 z2**: Coordinates of the opposite corner.

