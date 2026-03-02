# 構造体システム: JSON フォーマットリファレンス

このリファレンスでは、マルチブロック構造体を定義するための JSON 形式について説明します。ファイルは `config/omoshiroikamo/modular/structures/` に配置してください。

## 1. ファイル構成
ファイルには単一のオブジェクト、またはオブジェクトの配列を含めることができます。`default`（または `defaults`）という名前の特殊なオブジェクトを使用して、共通のマッピングを定義できます。

## 2. 主要なプロパティ

### ※1.5.1.4以降、"properties"を廃止しました！後方互換性はありません！
### 代わりに、以下の様に入れ子にせずに書いてください

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | 文字列 | 一意識別子（必須）。 |
| `displayName` | 文字列 | ユーザーフレンドリーな表示名（任意）。 |
| `recipeGroup` | 文字列/配列 | この構造体が対応するレシピグループ。 |
| `mappings` | オブジェクト | 文字記号とブロックの対応。 |
| `layers` | 配列 | 構造体の垂直方向のスライス（上から下へ）。 |
| `requirements` | 配列 | 最小限必要な機能（ポートなど）。 |
| `tintColor` | 文字列 | 構造体のレンダリング色（例: `#FF0000`）。 |
| `speedMultiplier` | Float | 処理速度の乗数（デフォルト: 1.0）。 |
| `energyMultiplier` | Float | エネルギー消費の乗数（デフォルト: 1.0）。 |
| `batchMin` | Integer | レシピの最小バッチサイズ（デフォルト: 1）。 |
| `batchMax` | Integer | レシピの最大バッチサイズ（デフォルト: 1）。 |
| `tier` | Integer | マシンのティア（デフォルト: 0）。 |
| `defaultFacing` | 文字列 | 構造体のデフォルトの向き（`UP`, `DOWN`）。指定がない場合は横向きになります。 |

## 3. マッピング (Mappings)
マッピングは、`layers` 内の文字をブロック ID にリンクします。

### 文字列形式
`"F": "omoshiroikamo:basaltStructure:*"` (メタデータにワイルドカード `*` が使用可能)

### オブジェクト形式 (一部実装予定)
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

利用可能なタイプ: `itemInput`, `itemOutput`, `fluidInput`, `fluidOutput`, `energyInput`, `energyOutput`, `manaInput`, `manaOutput`, `gasInput`, `gasOutput`, `essentiaInput`, `essentiaOutput`, `visInput`, `visOutput`

### 配列形式
```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

### オブジェクト形式
1.5.1.4以降、各タイプをキーとしたオブジェクト形式もサポートされています。
```json
"requirements": {
    "energyInput": { "min": 1 },
    "itemOutput": 1,
    "fluidInput": { "min": 1, "max": 4 }
}
```
※ 値が数値の場合は、`min` として扱われます。

## 5. 予約記号 (Reserved Symbols)

構造体システムでは、以下の記号が特殊な意味を持ちます。

### 5.1 システム予約記号 (必須)
これらの記号はシステムの中核機能で使用され、JSON の `mappings` で**上書きすることはできません**。

| 記号 | 意味 | 説明 |
| :--- | :--- | :--- |
| `Q` | コントローラー | 構造体に必ず1つ必要です。 |
| `_` | 空気 (Air) | 強制的な空気ブロックとして扱われます。 |
| (スペース) | 任意 (Any) | バリデーション対象外の空間です。 |

### 5.2 慣習的予約記号 (条件付き)
`A`, `L`, `G` は特定のモジュールで慣習的に使用されており、構造体の種類によって扱いが異なります。

| 記号 | 意味 | 既存マシンでの扱い | Modularでの扱い |
| :--- | :--- | :--- | :--- |
| `A` | Modifier | **コード定義が優先** | JSONで定義可能 |
| `L` | Lens | **コード定義が優先** | JSONで定義可能 |
| `G` | Solar Cell | **コード定義が優先** | JSONで定義可能 |

> [!IMPORTANT]
> **Internalマシン（既存マシン）の場合:**
> Solar Array や Extractor 等の既存マシンでは、これらの記号は内部ロジック（アドオン接続等）と密接に紐付いています。そのため、JSON で定義を書いてもシステム（コード）側の定義によってスキップ/保護されます。
> 
> **Modular構造体の場合:**
> `modular/structures/` に作成する新しい構造体では、これらの記号も他の記号（`B`, `C`, `X`等）と同様に自由に定義して使用できます。

## 6. コマンド
- `/ok multiblock reload`: Multiblockモジュールの構造体を再読み込みします。
- `/ok modular reload`: Modularモジュールのレシピと構造体データを再読み込みします。
