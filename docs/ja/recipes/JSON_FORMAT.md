# レシピシステム: JSON フォーマットリファレンス

レシピは `config/omoshiroikamo/modular/recipes/*.json` で定義されます。

## 1. ファイル構成
単一のレシピオブジェクトを記述するか、または複数のレシピをまとめて記述することができます。

### 複数レシピの構成 (推奨)
```json
{
  "group": "マシン名",
  "recipes": [
    { ... レシピ定義 1 ... },
    { ... レシピ定義 2 ... }
  ]
}
```

## 2. レシピのプロパティ

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | 文字列 | 表示名（任意） |
| `duration` | 整数 | 処理時間（tick単位）。`time` も使用可能。 |
| `priority` | 整数 | レシピの優先度。数値が大きいほど優先されます（デフォルト: 0） |
| `inputs` | 配列 | 必要なリソースのリスト |
| `outputs` | 配列 | 生成されるリソースのリスト |
| `conditions` | 配列 | 特殊な制約条件 |
| `decorators` | 配列 | レシピの挙動を拡張するデコレータ |

## 3. 入力と出力 (Inputs and Outputs)

入出力は、オブジェクト内に特定のキーが存在するかどうかで型が判定されます。

### アイテム
- `item`: ブロック/アイテムID。
- `amount`: 数量。
- `meta`: メタデータ（任意）。
- `ore`: 鉱石辞書名（inputのみ、`item`の代わりに使用）。

```json
{ 
  "item": "minecraft:coal", 
  "amount": 64 
}
```

### 液体
- `fluid`: 液体ID。
- `amount`: ミリバケツ量。

```json
{ 
  "fluid": "water", 
  "amount": 1000 
}
```

### エネルギー・マナ
- `energy` / `mana`: 量。
- `perTick` / `pertick`: trueの場合、合計ではなく毎tick消費/生産します。

```json
{ 
  "energy": 100, 
  "perTick": true 
}
```

### その他のリソース
- `gas`: ガスID。
- `essentia`: 相（Aspect）名。
- `vis`: 相（Aspect）名。

```json
{ 
  "essentia": "ignis", 
  "amount": 10 
}
```

## 3. 条件 (Conditions)
条件は毎 tick、または処理の開始時にチェックされます。論理演算（CoRパターン）を使用して複雑な条件を構成可能です。

利用可能なタイプ:
- `dimension`: 特定の次元にいるか。
- `biome`: 特定のバイオームにいるか。
- `block_below`: マシンの下に特定のブロックがあるか。
- `tile_nbt`: マシンのTileEntityのNBT値をチェック。
- `weather`: ※実装予定。雨や雷などの天候をチェックします。

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

### サポートされている論理演算
- `and`, `or`, `not`, `xor`, `nand`, `nor`

## 4. デコレータ (Decorators)
デコレータはレシピの実行中や終了時に追加の挙動を与えます。

- `chance`: レシピの成功確率を制御。
- `bonus`: 確率で追加の出力を生成。
- `requirement`: 実行中に追加の構造的要件をチェック。
- `weighted_random`: 重み付きリストから出力を選択。

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

## 5. エクスプレッション (Expression)
一部のパラメータ（デコレータの確率など）には、数値を動的に算出する `Expression` を使用できます。数値定数を直接記述する代わりに、以下のオブジェクト形式を使用できます。

- `constant`: 固定の数値を返します。
- `nbt`: マシンの TileEntity から指定した NBT パス（`energyStored` 等）の数値を読み取ります。
- `map_range`: ある範囲の数値を別の範囲に線形補完でマッピングします。

```json
"chance": {
  "type": "map_range",
  "input": { "type": "nbt", "path": "energyStored" },
  "inMin": 0, "inMax": 100000,
  "outMin": 0.1, "outMax": 1.0
}
```

## 6. 継承
`abstract`（抽象）レシピを使用して、共通のプロパティを共有できます。

```json
{
  "registryName": "base_miner",
  "isAbstract": true,
  "time": 200,
  "inputs": [...]
}
```
他のレシピで `"parent": "base_miner"` を指定することで、これらの値を継承できます。
