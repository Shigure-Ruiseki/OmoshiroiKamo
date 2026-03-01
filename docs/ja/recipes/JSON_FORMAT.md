# レシピシステム: JSON フォーマットリファレンス

レシピは `config/omoshiroikamo/modular/recipes/*.json` で定義されます。

## 1. レシピのプロパティ

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `machine` | 文字列 | マシングループ名（必須）。 |
| `registryName` | 文字列 | レシピの一意な ID（必須）。 |
| `name` | 文字列 | 表示名（任意）。 |
| `time` | 整数 | 処理時間（tick単位、必須）。 |
| `inputs` | 配列 | 必要なリソースのリスト。 |
| `outputs` | 配列 | 生成されるリソースのリスト。 |
| `conditions` | 配列 | 特殊な制約条件（任意）。 |

## 2. 入力と出力 (Inputs and Outputs)

`inputs` または `outputs` の各項目には `type`（型）が必要です。

### アイテムの例
```json
{
  "type": "item",
  "id": "minecraft:iron_ingot",
  "amount": 1
}
```

### 液体の例
```json
{
  "type": "fluid",
  "id": "water",
  "amount": 1000
}
```

### エネルギーの例
```json
{
  "type": "energy",
  "amount": 10000
}
```

## 3. 条件 (Conditions)
条件は毎 tick、または処理の開始時にチェックされます。

```json
"conditions": [
  { "type": "weather", "weather": "rain" },
  { "type": "dimension", "dim": -1 }
]
```

## 4. 継承
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
