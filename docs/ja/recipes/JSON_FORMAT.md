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

| `decorators` | 配列 | レシピの挙動を拡張するデコレータ |
| `requiredTier` | オブジェクト | 要求されるコンポーネントの Tier (例: `{"glass": 1, "casing": 3}`) |

## 2.1 レシピの優先順位とソート
レシピは以下の順序で評価・表示されます（上位の項目が優先）：
1. **要求される最大 Tier**: 最も高い Tier 要求を持つレシピが優先されます（上位 Tier レシピがリストの先頭にきます）。
2. **優先度 (`priority`)**: `requiredTier` が同じ場合、この数値が大きいほど優先されます。
3. **入力の種類数**: より多くの種類のリソースを要求するレシピが優先されます。
4. **アイテム入力の総数**: 合計アイテム要求数が多いレシピが優先されます。

## 3. 入力と出力 (Inputs and Outputs)

## 2.1 レシピの優先順位とソート
レシピは以下の順序で評価・表示されます：
1. **要求される最大 Tier**: 数値が大きい（上位 Tier）レシピが最優先されます。
2. **優先度 (`priority`)**: `requiredTier` が同じ場合、この数値が大きいほど優先されます。
3. **入力の種類数**: より多くの種類のリソースを要求するレシピが優先されます。
4. **アイテム入力の総数**: 合計アイテム要求数が多いレシピが優先されます。

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

### 11. 外部ブロック NBT チェック/消費 (Block Nbt Input)
レシピ開始時に構造体内のブロックの NBT をチェック・消費します。

- `type`: `"block_nbt"`
- `symbol`: 対象の記号。
- `key`: チェック対象の NBT キー名。
- `operation`: (`"sub"` | `"set"` | `"add"`)。`"sub"` は不足時にレシピ開始を阻止します。
- `value`: 数値または式。
- `consume`: true（デフォルト）の場合、レシピ開始時に実際に値を書き換えます。
- `optional`: true の場合、対象ブロックや NBT キーが見つからない場合でもレシピを開始できます。false（デフォルト）の場合、対象が見つからなければレシピ開始を阻止します。

```json
"inputs": [{
  "type": "block_nbt",
  "symbol": "S",
  "key": "stored_energy",
  "operation": "sub",
  "value": 100
}]
```

### ブロック (Block)
構造体内の特定のシンボル位置にあるブロックを検知・操作します。本 mod では、**`replace`（操作前）** と **`block`（操作後）** という統一された命名規則を採用しています。
(注)いくつかのTileEntityは設置時にクラッシュの原因になります。(Angelica+ET Futurm導入時のBeaconで確認済)
もしバグを発見したらissueの作成をお願いします。

- `symbol`: 構造体定義で使用されている記号（1文字）。
- `replace`: (**条件/旧ブロック**) 操作対象のブロックID。
- `block`: (**結果/新ブロック**) 最終的にその位置にあるべきブロックID。
- `consume`: (**Inputのみ**) trueの場合、`block` を指定しなくても自動的に空気に置き換えます（消去）。
- `optional`: trueの場合、対象ブロックが見つからなくてもレシピを開始できます（あれば実行する）。
- `amount`: 操作する最大個数。
- `nbt`: (**Outputのみ**) 設置するブロックのTileEntityに適用するNBTデータ。値に **Expression** を使用可能です。

#### 7つの主要ユースケース

| # | ケース | 入出力 | 設定例 | 挙動 |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **存在確認** | `inputs` | `"block": "stone"` | 石があるか確認（消えなない） |
| 2 | **必須消費** | `inputs` | `"block": "stone", "consume": true` | 石を消去（開始時） |
| 3 | **任意消費** | `inputs` | `"consume": true, "optional": true` | あれば消去（開始時） |
| 4 | **入力置換** | `inputs` | `"replace": "A", "block": "B"` | AをBに変換（開始時） |
| 5 | **新規設置** | `outputs`| `"block": "gold"` | 空きに金を設置（終了時） |
| 6 | **必須置換** | `outputs`| `"replace": "stone", "block": "gold"` | 石を金に置換（終了時） |
| 7 | **任意置換** | `outputs`| `"replace": "stone", "block": "gold", "optional": true` | 石あれば金に置換（終了時） |

#### 動的NBTの例
```json
"outputs": [{
  "symbol": "D",
  "block": "modid:battery",
  "nbt": {
    "energy": { "type": "nbt", "path": "machine_power" }
  }
}]
```

#### 8. 外部ブロック NBT 操作 (Block Nbt Output)
構造体内の任意のブロック（TileEntity）の NBT を直接操作します。これは `block` による置換とは異なり、ブロックそのものを変えずに内部データのみを数値的に変更します。

- `type`: `"block_nbt"` を指定します。
- `symbol`: 操作対象の記号。
- `key`: 操作対象の NBT キー名。
- `operation`: 演算方法 (`"set"`, `"add"`, `"sub"`)。
- `optional`: true の場合、出力先のブロックや NBT キーがなくてもレシピの完了を許可します。false（デフォルト）の場合、出力先がなければレシピの実行が止まります（容量不足として扱われます）。
- `value`: 数値または代入する Expression。

```json
"outputs": [{
  "type": "block_nbt",
  "symbol": "S",
  "key": "stored_energy",
  "operation": "add",
  "value": 1000
}]
```

## 3. 条件 (Conditions)
条件は毎 tick、または処理の開始時にチェックされます。論理演算（CoRパターン）を使用して複雑な条件を構成可能です。

利用可能なタイプ:
- `dimension`: 特定の次元にいるか（`dim`: 数値）。
- `biome`: バイオーム名、タグ、環境数値を判定します。
    - `biomes`: バイオーム名の配列。
    - `tags`: Forge BiomeDictionary タグの配列（`HOT`, `COLD`, `WET`, `DRY`, `FOREST` など）。
    - `minTemp` / `maxTemp`: 気温の範囲判定（任意）。
    - `minHumid` / `maxHumid`: 湿度の範囲判定（任意）。
- `offset`: 任意の条件を指定した相対座標 `(dx, dy, dz)` で判定します。
    - `dx`, `dy`, `dz`: 相対座標。
    - `condition`: 実行する条件オブジェクト。
- `pattern`: クラフトレシピのような形式で、周囲のバイオーム配置を判定します。
    - `pattern`: 文字列の配列（例: `["AAA", "A#A", "AAA"]`）。
    - `keys`: パターン文字と条件オブジェクトのマッピング。
- `block_below`: マシンの下に特定のブロックがあるか。
- `tile_nbt`: マシンのTileEntityのNBT値をチェック。
- `weather`: 現在の天候を判定します。雨 (`rain`)、雷雨 (`thunder`)、晴天 (`clear`) を指定します。
- `comparison`: 二つの式を比較します（`left`, `right`, `operator`）。
- `expression`: 数学的な文字列式を直接記述します。最も推奨される方法です。

```json
"conditions": [
  { 
    "type": "pattern",
    "pattern": [ "FFF", "F#F", "FFF" ],
    "keys": {
      "#": { "type": "biome", "biomes": ["Plains"] },
      "F": { "type": "biome", "tags": ["FOREST"] }
    }
  },
  { "type": "weather", "weather": "rain" },
  { "type": "expression", "expression": "day % 28 == 0" }
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
- `arithmetic`: 二つの式の間で演算を行います（`left`, `right`, `operation`: `+`, `-`, `*`, `/`, `%`）。
- `world_property`: ワールドの情報（`time`, `day`, `moon_phase`）を取得します。

### 文字列による簡易記述 (Expression String / Recipe Script)
JSON のオブジェクト階層を避け、文字列として直接式を記述できます。これは単一の数値だけでなく、複雑な条件判定（論理演算）もサポートしており、本システムでは **「レシピスクリプト」** と呼称されます。

- **高度な文法**:
  - **論理演算**: `&&` (AND), `||` (OR), `!` (NOT) を使用可能。
  - **グループ化**: `()` または `{}` で演算の優先順位を制御。
  - **改行・空白**: 式の途中で改行して読みやすく記述できます。

- **高度な関数**:
  - `nbt('key')`: マシン本体の NBT を取得。
  - `nbt('S', 'key')`: 記号 `S` の位置にあるブロックの NBT を取得。

```json
"condition": "nbt('S', 'energy') > 5000",
"chance": "{ nbt('energy') / 100000.0 } * 0.8"
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
