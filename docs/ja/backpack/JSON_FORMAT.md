# Backpack JSON フォーマット

バックパックテンプレートの JSON ファイルフォーマット仕様書です。

## 📁 ファイル配置

JSONファイルは以下のディレクトリに配置します：
```
config/omoshiroikamo/backpack/dump/
├── starter_pack.json
├── mining_setup.json
└── farming_setup.json
```

## 📋 基本構造

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

## 🔧 フィールド詳細

### BackpackTier (必須)

バックパックのティア（サイズ）を指定します。

**使用可能な値:**
- `"Leather"` - 27スロット (3x9) + アップグレード1スロット
- `"Iron"` - 54スロット (6x9) + アップグレード2スロット
- `"Gold"` - 81スロット (9x9) + アップグレード3スロット
- `"Diamond"` - 108スロット (12x9) + アップグレード5スロット
- `"Obsidian"` - 120スロット (12x10) + アップグレード7スロット

**例:**
```json
{
  "BackpackTier": "Diamond"
}
```

### MainColor (オプション)

バックパックのメインカラーを16進数カラーコードで指定します。

**デフォルト値:** `"#FFFFFF"`

**例:**
```json
{
  "MainColor": "#8B4513"
}
```

### AccentColor (オプション)

バックパックのアクセントカラーを16進数カラーコードで指定します。

**デフォルト値:** `"#FFFFFF"`

**例:**
```json
{
  "AccentColor": "#D2691E"
}
```

### SearchBackpack (オプション)

バックパック内のアイテムを検索可能にするかどうか。

**デフォルト値:** `true`

**例:**
```json
{
  "SearchBackpack": false
}
```

### Inventory (オプション)

バックパックのインベントリに入れるアイテムの配列。

**構造:**
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

### Upgrade (オプション)

バックパックのアップグレードスロットに入れるアイテムの配列。

**構造:**
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

## 📦 アイテムエントリの構造

各アイテムエントリは以下のフィールドを持ちます：

### Slot (必須)

アイテムを配置するスロット番号（0から開始）。

**例:** `"Slot": 0`

### id (必須)

アイテムのID。メタデータがある場合はコロンで区切って指定。

**フォーマット:**
- `"mod:item"` - メタデータなし
- `"mod:item:meta"` - メタデータあり

**例:**
```json
"id": "minecraft:diamond_sword"
"id": "minecraft:wool:14"
"id": "omoshiroikamo:upgrade_feeding_advanced"
```

### Count (オプション)

アイテムのスタック数。

**デフォルト値:** `1`
**範囲:** `1-64` (アイテムによって異なる)

**例:** `"Count": 64`

### nbt (オプション)

アイテムのNBTデータ。JSONオブジェクト形式で指定。

**例:**
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

## 📝 完全な例

### スターターパック

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

### マイニングセットアップ

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

## 🔗 関連コマンド

### エクスポート

手に持っているバックパックをJSONファイルにエクスポート：
```
/ok backpack export <name>
```

### インポート

JSONテンプレートを手に持っているバックパックに適用：
```
/ok backpack import <name>
```

### 付与

JSONテンプレートからバックパックを生成してプレイヤーに付与：
```
/ok backpack give <name>
```

## ⚠️ 注意事項

1. **スロット番号の範囲**: Inventoryのスロット番号はバックパックのティアによって異なります
   - Leather: 0-26 (27スロット)
   - Iron: 0-53 (54スロット)
   - Gold: 0-80 (81スロット)
   - Diamond: 0-107 (108スロット)
   - Obsidian: 0-119 (120スロット)

2. **アップグレードスロット**: ティアによって異なります
   - Leather: 0 (1スロット)
   - Iron: 0-1 (2スロット)
   - Gold: 0-2 (3スロット)
   - Diamond: 0-4 (5スロット)
   - Obsidian: 0-6 (7スロット)

3. **NBTデータの互換性**: NBTデータはMinecraft 1.7.10の形式に従う必要があります

4. **カラーコードの形式**: 必ず `#` で始まる6桁の16進数を使用してください

5. **ファイル名**: `.json` 拡張子は自動的に追加されるため、コマンドでは拡張子なしで指定してください
