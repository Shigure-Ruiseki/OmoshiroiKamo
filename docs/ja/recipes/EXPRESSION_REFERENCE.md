# 式パーサー 変数・関数リファレンス

OmoshiroiKamo の式パーサー (`ExpressionParser`) で使用できる変数、関数、および高度なクエリのリファレンスです。
これらは JSON レシピの `amount` フィールド、`Condition` の `expression`、マシンの性能係数の設定などで利用されます。

## 📚 関連ドキュメント

- [JSON フォーマット](./JSON_FORMAT.md) - 基本的な JSON 構文
- [実用例集](./EXPRESSION_EXAMPLES.md) - 実践的なレシピ例

---

## 1. 基本演算

標準的な算術および論理演算をサポートしています。

- **算術演算**: `+`, `-`, `*`, `/`, `%` (剰余)
- **比較演算**: `==`, `!=`, `>`, `<`, `>=`, `<=`
- **論理演算**: `&&` (AND), `||` (OR), `!` (NOT)
- **グループ化**: `()`, `{}`

> [!TIP]
> 条件式 (`1 == 1`) は、数値計算の中では `1` (真) または `0` (偽) として扱われます。
> 例: `10 + (day > 100) * 5` (100日目以降なら 15、それ以外なら 10)

## 2. 組み込み変数

### ワールド変数
マシンの設置されている世界の情報を取得します。

#### 時間・暦
- `day` / `total_days`: 累積経過日数
- `time`: 現在の時刻 (0 - 23999)
- `tick`: ワールドの総経過時間 (Total World Time の生の tick 値)
- `moon` / `moon_phase`: 現在の月齢 (0 - 7、0 = 満月、4 = 新月)

#### 座標・ディメンション
- `x` / `y` / `z`: コントローラーの座標
- `dimension`: 現在のディメンション ID（オーバーワールド=0、ネザー=-1、エンド=1）

#### 天候・環境
- `is_day`: 昼であるか (1 or 0)
- `is_night`: 夜であるか (1 or 0)
- `is_raining`: 雨が降っているか (1 or 0)
- `is_thundering`: 雷雨であるか (1 or 0)
- `temp`: コントローラー位置のバイオーム温度 (0.0 〜 2.0)
- `humidity`: コントローラー位置のバイオーム湿度 (0.0 〜 1.0)

#### 光・空間
- `light`: コントローラー位置のブロックライト値 (0 - 15、空と地面の合成)
- `light_block`: ブロック光源のみのライト値 (0 - 15)
- `light_sky`: 空のライト値 (0 - 15)
- `can_see_sky`: コントローラーが空を見えるか (1 or 0)
- `can_see_void`: コントローラーの真下が Y=0 まで空洞か (1 or 0)

#### レシピ進行
- `recipe_tick`: 現在のレシピが開始してからの経過時間 (ticks)
- `progress_tick`: 現在のレシピの進捗の raw tick 値

#### その他
- `redstone`: コントローラーが受けているレッドストーン信号強度 (0 - 15)
- `random_seed`: レシピ評価セッションのシード値（`random()` / `chance()` の再現性に使用）
- `world_seed`: ワールド生成時のシード値
- `facing`: マシンの向き (0:下, 1:上, 2:北, 3:南, 4:西, 5:東)

### 定数
- `pi`: 円周率 (π ≒ 3.14159)
- `e`: ネイピア数 (e ≒ 2.71828)

## 3. マシンプロパティ

マシンの現在の状態を取得します。

### アイテム (Item)
- **変数 (全体量)**
    - `item` / `item_total`: 現在蓄積されているアイテムの総数。
    - `item_in` / `item_out`: 搬入 / 搬出ポートのアイテム合計数。
    - `item_max` / `item_capacity`: マシンの最大アイテムスロット数。
    - `item_f` / `item_free` / `item_space`: アイテム全体の空き容量。
    - `item_p` / `item_percent`: アイテムの充填率 (0.0 ~ 1.0)。
- **関数 (種類指定)**
    - `item("id")`: 指定した ID または OreDict のアイテムの現在蓄積量。
    - `item_in("id")` / `item_out("id")`: 指定アイテムの搬入 / 搬出ポート量。
    - `item_f("id")` / `item_f_in("id")` / `item_f_out("id")`: 指定アイテムの受け入れ可能数。
- **スロット情報 (関数)**
    - `item_slot()`: 合計スロット数。
    - `item_slot_in()` / `item_slot_out()`: 入力 / 出力ごとのスロット数。
    - `item_slot_empty()`: 空きスロット数。

### エネルギー (Energy)
- `energy` / `energy_stored` / `energy_total`: 現在蓄積されているエネルギー量。
- `energy_max` / `energy_capacity`: マシンの最大エネルギー容量。
- `energy_f` / `energy_free`: エネルギーの空き容量 (`max - stored`)。
- `energy_p` / `energy_percent`: エネルギーの充填率 (0.0 ~ 1.0)。
- `energy_per_tick`: 1ティックあたりのエネルギー消費/生成量（対応マシンのみ）。

### 流体 (Fluid)
- **変数 (全体量)**
    - `fluid` / `fluid_stored` / `fluid_total`: 現在蓄積されている流体の総量。
    - `fluid_in` / `fluid_out`: 搬入 / 搬出ポートの流体合計量。
    - `fluid_max` / `fluid_capacity`: 流体タンクの最大容量。
    - `fluid_p` / `fluid_percent`: 流体の充填率 (0.0 ~ 1.0)。
- **関数 (種類指定)**
    - `fluid("name")`: 指定した名前の流体の現在蓄積量。
    - `fluid_in("name")` / `fluid_out("name")`: 指定した流体の搬入 / 搬出ポート量。

### その他リソース
- **マナ (Mana)**: `mana`, `mana_max`, `mana_p`
- **ガス (Gas)**: `gas`, `gas_max`, `gas_p`
- **エッセンティア (Essentia)**: `essentia("aspectName")`
- **Vis**: `vis("aspectName")`

### 統計・状態
- `recipe_count`: マシンが処理したレシピの累計回数。
- `progress` / `progress_percent`: 現在のレシピの進行度 (0.0 ~ 1.0)。
- `tier`: マシンの現在の Tier。
- `is_running`: マシンが動作中であるか (1 or 0)。
- `timeplaced`: マシンが設置されてからの累積時間 (ticks)。
- `timecontinue`: マシンが連続稼働している時間 (ticks)。

### 構造プロパティ
構造体定義から提供される性能係数。

- `batch`: 現在のバッチサイズ。
- `speed_multi`: 速度倍率。
- `energy_multi`: エネルギー倍率。

---

## 4. 関数リファレンス

### 数学関数
- `abs(x)`, `sqrt(x)`, `pow(base, exp)`
- `min(a, b...)`, `max(a, b...)`
- `sin(x)`, `cos(x)`, `tan(x)` (入力は**ラジアン**)
- `asin(x)`, `acos(x)`, `atan(x)` (出力はラジアン)
- `rad(deg)`, `deg(rad)` (度とラジアンの変換)
- `floor(x)`, `ceil(x)`, `round(x)`
- `clamp(val, min, max)`
- `random()`: 0 以上 1 未満の乱数
- `chance(x)`: 確率 `x` (0.0 - 1.0) に基づいて 1 または 0 を返す

### 高度なクエリ
- `can_see_sky(filter...)`: 上空視界判定。ガラス等を透過させるには ID を指定。
- `can_see_void(filter...)`: 真下が奈落か判定。
- `count_blocks(distance, filter...)`: 周囲の特定ブロックをカウント。
    - 例: `count_blocks(1, "minecraft:iron_block")`
- `nbt('key')`: マシン本体の NBT を取得。
- `nbt('symbol', 'key')`: 構造体の特定シンボル位置の NBT を取得。

---

## 5. 設計のヒント

### よくある間違い
- **文字列の引用符**: 式自体は JSON 内で文字列として `"amount": "tier * 2"` のように記述します。
- **大文字・小文字**: 変数名はすべて**小文字**です（`Tier` ではなく `tier`）。
- **全角文字**: 演算子や変数名に全角文字を使用しないでください。

### パフォーマンス
式は毎ティック評価される可能性があるため、極端に複雑な式や、広範囲の `count_blocks` などの多用は避けてください。
