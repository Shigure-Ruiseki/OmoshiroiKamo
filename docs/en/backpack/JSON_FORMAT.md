# Backpack JSON Format

JSON file format specification for backpack templates.

## 📁 File Location

Place JSON files in the following directory:
```
config/omoshiroikamo/backpack/dump/
├── starter_pack.json
├── mining_setup.json
└── farming_setup.json
```

## 📋 Basic Structure

```json
{
  "BackpackTier": "Leather",
  "MainColor": "#8B4513",
  "AccentColor": "#D2691E",
  "SearchBackpack": true,
  "Inventory": [
    {
      "Slot": 0,
      "id": "minecraft:diamond_pickaxe",
      "Count": 1,
      "nbt": {
        "ench": [
          {
            "id": 32,
            "lvl": 5
          }
        ]
      }
    }
  ],
  "Upgrade": [
    {
      "Slot": 0,
      "id": "omoshiroikamo:upgrade_feeding_advanced",
      "Count": 1,
      "nbt": {
        "FilterItems": {
          "Items": [],
          "Size": 16
        },
        "HungerFeedingStrategy": 0,
        "HurtFeedingStrategy": 0
      }
    }
  ]
}
```

## 🔧 Field Details

### BackpackTier (Required)

Specifies the backpack tier (size).

**Available Values:**
- `"Leather"` - 27 slots (3x9) + 1 upgrade slot
- `"Iron"` - 54 slots (6x9) + 2 upgrade slots
- `"Gold"` - 81 slots (9x9) + 3 upgrade slots
- `"Diamond"` - 108 slots (12x9) + 5 upgrade slots
- `"Obsidian"` - 120 slots (12x10) + 7 upgrade slots

**Example:**
```json
{
  "BackpackTier": "Diamond"
}
```

### MainColor (Optional)

Specifies the main color of the backpack as a hexadecimal color code.

**Default Value:** `"#FFFFFF"`

**Example:**
```json
{
  "MainColor": "#8B4513"
}
```

### AccentColor (Optional)

Specifies the accent color of the backpack as a hexadecimal color code.

**Default Value:** `"#FFFFFF"`

**Example:**
```json
{
  "AccentColor": "#D2691E"
}
```

### SearchBackpack (Optional)

Whether to make items in the backpack searchable.

**Default Value:** `true`

**Example:**
```json
{
  "SearchBackpack": false
}
```

### Inventory (Optional)

Array of items to place in the backpack's inventory.

**Structure:**
```json
{
  "Inventory": [
    {
      "Slot": 0,
      "id": "minecraft:diamond",
      "Count": 64
    },
    {
      "Slot": 1,
      "id": "minecraft:iron_ingot:0",
      "Count": 32,
      "nbt": {
        "display": {
          "Name": "Custom Name"
        }
      }
    }
  ]
}
```

### Upgrade (Optional)

Array of items to place in the backpack's upgrade slots.

**Structure:**
```json
{
  "Upgrade": [
    {
      "Slot": 0,
      "id": "omoshiroikamo:upgrade_feeding",
      "Count": 1,
      "nbt": {
        "FilterItems": {
          "Items": [],
          "Size": 9
        }
      }
    }
  ]
}
```

## 📦 Item Entry Structure

Each item entry has the following fields:

### Slot (Required)

Slot number where the item will be placed (starting from 0).

**Example:** `"Slot": 0`

### id (Required)

Item ID. If metadata exists, separate with colon.

**Format:**
- `"mod:item"` - No metadata
- `"mod:item:meta"` - With metadata

**Examples:**
```json
"id": "minecraft:diamond_sword"
"id": "minecraft:wool:14"
"id": "omoshiroikamo:upgrade_feeding_advanced"
```

### Count (Optional)

Stack size of the item.

**Default Value:** `1`
**Range:** `1-64` (varies by item)

**Example:** `"Count": 64`

### nbt (Optional)

Item NBT data. Specified as a JSON object.

**Example:**
```json
{
  "nbt": {
    "display": {
      "Name": "§6Golden Pickaxe"
    },
    "ench": [
      {
        "id": 32,
        "lvl": 5
      },
      {
        "id": 33,
        "lvl": 1
      }
    ]
  }
}
```

## 📝 Complete Examples

### Starter Pack

```json
{
  "BackpackTier": "Leather",
  "MainColor": "#8B4513",
  "AccentColor": "#D2691E",
  "SearchBackpack": true,
  "Inventory": [
    {
      "Slot": 0,
      "id": "minecraft:diamond_pickaxe",
      "Count": 1,
      "nbt": {
        "ench": [
          {
            "id": 32,
            "lvl": 5
          },
          {
            "id": 33,
            "lvl": 1
          },
          {
            "id": 34,
            "lvl": 3
          }
        ]
      }
    },
    {
      "Slot": 1,
      "id": "minecraft:diamond_shovel",
      "Count": 1
    },
    {
      "Slot": 2,
      "id": "minecraft:diamond_axe",
      "Count": 1
    },
    {
      "Slot": 9,
      "id": "minecraft:torch",
      "Count": 64
    },
    {
      "Slot": 10,
      "id": "minecraft:cooked_beef",
      "Count": 64
    }
  ],
  "Upgrade": [
    {
      "Slot": 0,
      "id": "omoshiroikamo:upgrade_feeding",
      "Count": 1,
      "nbt": {
        "FilterItems": {
          "Items": [
            {
              "Count": 1,
              "Damage": 0,
              "id": "minecraft:cooked_beef"
            }
          ],
          "Size": 9
        },
        "FilterType": 0,
        "Enabled": true
      }
    },
    {
      "Slot": 1,
      "id": "omoshiroikamo:upgrade_magnet",
      "Count": 1,
      "nbt": {
        "FilterType": 1,
        "Enabled": true
      }
    }
  ]
}
```

### Mining Setup

```json
{
  "BackpackTier": "Diamond",
  "MainColor": "#555555",
  "AccentColor": "#00FFFF",
  "SearchBackpack": true,
  "Inventory": [
    {
      "Slot": 0,
      "id": "minecraft:diamond_pickaxe",
      "Count": 1,
      "nbt": {
        "ench": [
          {
            "id": 32,
            "lvl": 5
          },
          {
            "id": 33,
            "lvl": 1
          },
          {
            "id": 34,
            "lvl": 3
          },
          {
            "id": 35,
            "lvl": 3
          }
        ]
      }
    },
    {
      "Slot": 1,
      "id": "minecraft:diamond_shovel",
      "Count": 1,
      "nbt": {
        "ench": [
          {
            "id": 32,
            "lvl": 5
          },
          {
            "id": 33,
            "lvl": 1
          },
          {
            "id": 34,
            "lvl": 3
          },
          {
            "id": 35,
            "lvl": 3
          }
        ]
      }
    }
  ],
  "Upgrade": [
    {
      "Slot": 0,
      "id": "omoshiroikamo:upgrade_feeding_advanced",
      "Count": 1,
      "nbt": {
        "FilterItems": {
          "Items": [],
          "Size": 16
        },
        "HungerFeedingStrategy": 0,
        "HurtFeedingStrategy": 0,
        "Enabled": true
      }
    },
    {
      "Slot": 1,
      "id": "omoshiroikamo:upgrade_magnet",
      "Count": 1,
      "nbt": {
        "Enabled": true
      }
    }
  ]
}
```

## 🔗 Related Commands

### Export

Export a held backpack to a JSON file:
```
/ok backpack export <name>
```

### Import

Apply a JSON template to a held backpack:
```
/ok backpack import <name>
```

### Give

Generate a backpack from a JSON template and give it to a player:
```
/ok backpack give <name>
```

## ⚠️ Notes

1. **Slot Number Range**: Inventory slot numbers vary by backpack tier
   - Leather: 0-26 (27 slots)
   - Iron: 0-53 (54 slots)
   - Gold: 0-80 (81 slots)
   - Diamond: 0-107 (108 slots)
   - Obsidian: 0-119 (120 slots)

2. **Upgrade Slots**: Varies by tier
   - Leather: 0 (1 slot)
   - Iron: 0-1 (2 slots)
   - Gold: 0-2 (3 slots)
   - Diamond: 0-4 (5 slots)
   - Obsidian: 0-6 (7 slots)

3. **NBT Data Compatibility**: NBT data must conform to Minecraft 1.7.10 format

4. **Color Code Format**: Always use 6-digit hexadecimal starting with `#`

5. **File Names**: The `.json` extension is added automatically, so specify names without the extension in commands
