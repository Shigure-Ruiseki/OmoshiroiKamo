# 構造体システム: JSON フォーマットリファレンス

このリファレンスでは、マルチブロック構造体を定義するための JSON 形式について説明します。ファイルは `config/omoshiroikamo/structures/` に配置してください。

## 1. ファイル構成
ファイルには単一のオブジェクト、またはオブジェクトの配列を含めることができます。`default`（または `defaults`）という名前の特殊なオブジェクトを使用して、共通のマッピングを定義できます。

## 2. 主要なプロパティ

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | 文字列 | 一意識別子（必須）。 |
| `displayName` | 文字列 | ユーザーフレンドリーな表示名（任意）。 |
| `recipeGroup` | 文字列/配列 | この構造体が対応するレシピグループ。 |
| `mappings` | オブジェクト | 文字記号とブロックの対応。 |
| `layers` | 配列 | 構造体の垂直方向のスライス（下から上へ）。 |
| `requirements` | 配列 | 最小限必要な機能（ポートなど）。 |
| `controllerOffset` | 配列 `[x, y, z]` | スキャン開始点からの相対的なコントローラー位置。 |

## 3. マッピング (Mappings)
マッピングは、`layers` 内の文字をブロック ID にリンクします。

### 文字列形式
`"F": "omoshiroikamo:basaltStructure:*"` (メタデータにワイルドカード `*` が使用可能)

### オブジェクト形式 (制限付き)
```json
"Q": {
  "block": "omoshiroikamo:quantumOreExtractor:0",
  "max": 1
}
```

### 複数候補の指定
```json
"A": {
  "blocks": [
    "omoshiroikamo:modifierNull:0",
    "omoshiroikamo:modifierSpeed:0"
  ]
}
```

## 4. 要件 (Requirements)
要件は、マシンが備えていなければならない内部コンポーネント（ポート）を定義します。

```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

## 5. 予約記号
- **`Q`**: コントローラー（通常必須）。
- **`_`**: 強制的な空気（Air）。
- **` ` (スペース)**: 任意のブロック（スキャン時に無視されます）。
- **`A`, `L`, `G`**: 一般的なモディファイアやレンズに使用される、事前に定義されたスロット。

## 6. コマンド
- `/ok multiblock reload`: 固定構造体を再読み込みします。
- `/ok modular reload`: レシピとカスタム構造体データを再読み込みします。
