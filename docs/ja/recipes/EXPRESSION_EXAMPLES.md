# レシピ式の実用例集

このドキュメントでは、動的数量システム（Expression System）を使った実践的なレシピ例を紹介します。

## 📚 関連ドキュメント

- [JSON フォーマット](./JSON_FORMAT.md) - 基本的な JSON 構文
---

## 1. 基本パターン

### 1.1 Tier依存レシピ

マシンの Tier に応じて入出力量を調整するレシピ。

#### 例1: リニアスケーリング
```json
{
  "group": "tier_scaling_machine",
  "recipes": [
    {
      "inputs": [
        {
          "item": "minecraft:iron_ingot",
          "amount": "tier * 8"
        }
      ],
      "outputs": [
        {
          "item": "minecraft:diamond",
          "amount": "tier"
        }
      ],
      "duration": 200
    }
  ]
}
```
- Tier 1: 鉄 8 個 → ダイヤ 1 個
- Tier 8: 鉄 64 個 → ダイヤ 8 個

#### 例2: 指数スケーリング
```json
{
  "inputs": [
    {
      "item": "rare_material",
      "amount": "pow(2, tier - 1)"
    }
  ],
  "outputs": [
    {
      "item": "super_material",
      "amount": "1"
    }
  ]
}
```
- Tier 1: 1 個、Tier 2: 2 個、Tier 3: 4 個、Tier 4: 8 個...
- 高 Tier になるほど飛躍的に必要量が増加

#### 例3: Tier閾値による分岐
```json
{
  "outputs": [
    {
      "item": "output_item",
      "amount": "tier >= 5 ? tier * 2 : tier"
    }
  ]
}
```
- Tier 4 以下: Tier × 1 個
- Tier 5 以上: Tier × 2 個

---

### 1.2 エネルギー依存レシピ

マシンのエネルギー残量に応じて効率が変化するレシピ。

#### 例1: エネルギー充填率ボーナス
```json
{
  "inputs": [
    {
      "energy": 10000,
      "perTick": true
    },
    {
      "item": "minecraft:coal",
      "amount": "1"
    }
  ],
  "outputs": [
    {
      "fluid": "steam",
      "amount": "floor(1000 * (1.0 + energy_p * 0.5))"
    }
  ],
  "duration": 100
}
```
- エネルギー 0%: 1000 mB
- エネルギー 50%: 1250 mB
- エネルギー 100%: 1500 mB

#### 例2: 低エネルギー時のペナルティ
```json
{
  "inputs": [
    {
      "item": "raw_ore",
      "amount": "energy_p < 0.2 ? 2 : 1"
    }
  ],
  "outputs": [
    {
      "item": "processed_ore",
      "amount": "1"
    }
  ]
}
```
- エネルギー 20% 未満: 原料 2 個必要
- エネルギー 20% 以上: 原料 1 個で処理可能

#### 例3: エネルギー節約モード
```json
{
  "inputs": [
    {
      "energy": "max(1000, floor((1.0 - energy_p) * 5000))",
      "perTick": true
    }
  ],
  "outputs": [
    {
      "item": "product",
      "amount": "1"
    }
  ]
}
```
- エネルギー満タン: 1000 RF/t で処理
- エネルギー空: 5000 RF/t で処理（急速充電から恩恵）

---

### 1.3 時間・天候依存レシピ

ワールドの環境条件に応じて変化するレシピ。

#### 例1: 昼夜サイクル
```json
{
  "outputs": [
    {
      "item": "solar_crystal",
      "amount": "time >= 0 && time < 12000 ? 5 : 1"
    }
  ],
  "conditions": [
    {
      "weather": "clear"
    }
  ]
}
```
- 昼間（0-12000 tick）かつ晴天: 5 個
- 夜間または雨天: 1 個

#### 例2: 月齢依存の魔術レシピ
```json
{
  "inputs": [
    {
      "essentia": "luna",
      "amount": "8 - moon_phase"
    }
  ],
  "outputs": [
    {
      "item": "moonstone",
      "amount": "moon_phase + 1"
    }
  ]
}
```
- 満月（moon_phase = 0）: エッセンティア 8、出力 1 個
- 新月（moon_phase = 7）: エッセンティア 1、出力 8 個

#### 例3: 季節変動（日数ベース）
```json
{
  "outputs": [
    {
      "item": "seasonal_herb",
      "amount": "1 + floor(sin(day * 0.1) * 3)"
    }
  ]
}
```
- サイン波で周期的に変動する出力量（1〜4 個）

---

## 2. 応用パターン

### 2.1 段階的な出力増加

処理回数に応じてボーナスが増える成長型レシピ。

#### 例1: マイルストーンボーナス
```json
{
  "outputs": [
    {
      "item": "reward",
      "amount": "10 + floor(recipeprocessed / 100) * 2"
    }
  ]
}
```
- 0-99 回: 10 個
- 100-199 回: 12 個
- 200-299 回: 14 個
- 累積で +2 個ずつボーナス

#### 例2: 経験値カーブ
```json
{
  "outputs": [
    {
      "item": "experience_orb",
      "amount": "min(100, floor(sqrt(recipeprocessed) * 5))"
    }
  ]
}
```
- 初期は急速に増加、後期は緩やかに（最大 100 個）

---

### 2.2 複数条件の組み合わせ

複数の要素を掛け合わせた複雑なレシピ。

#### 例1: Tier × エネルギー × 進捗
```json
{
  "outputs": [
    {
      "item": "advanced_product",
      "amount": "floor(tier * energy_p * (1.0 + recipeprocessed / 1000.0))"
    }
  ]
}
```
- Tier、エネルギー充填率、処理回数の3要素で決定
- Tier 8、エネルギー 100%、1000 回処理で 16 個

#### 例2: 環境総合評価
```json
{
  "inputs": [
    {
      "item": "catalyst",
      "amount": "max(1, 10 - tier - floor(energy_p * 3) - (time < 6000 ? 2 : 0))"
    }
  ]
}
```
- 高 Tier、高エネルギー、朝方ほど触媒が少なく済む（最低 1 個）

---

### 2.3 効率最適化レシピ

特定の条件下で最高効率になるよう調整されたレシピ。

#### 例1: スイートスポット設計
```json
{
  "outputs": [
    {
      "item": "optimized_product",
      "amount": "floor(10 * (1.0 - abs(energy_p - 0.5) * 2))"
    }
  ]
}
```
- エネルギー 50% で最大効率（10 個）
- エネルギー 0% or 100% で最低効率（0 個）
- 常に半分程度のエネルギーを維持するのが最適

#### 例2: バランス型
```json
{
  "inputs": [
    {
      "item": "resource_a",
      "amount": "max(1, tier - floor(fluid_p * 5))"
    },
    {
      "item": "resource_b",
      "amount": "max(1, tier - floor(mana_p * 5))"
    }
  ]
}
```
- 流体とマナのバランスが取れているほど必要資源が減少

---

## 3. 高度なテクニック

### 3.1 ステートマシン的動作

NBT と組み合わせて段階的な動作を実現。

```json
{
  "inputs": [
    {
      "type": "block_nbt",
      "symbol": "C",
      "key": "stage",
      "operation": "add",
      "value": "1"
    }
  ],
  "outputs": [
    {
      "item": "stage_reward",
      "amount": "min(10, nbt('C', 'stage'))"
    }
  ]
}
```
- 実行するたびに stage が +1
- 報酬も徐々に増加（最大 10 個）

---

### 3.2 確率的な出力

`chance()` 関数と組み合わせた確率的出力。

```json
{
  "outputs": [
    {
      "item": "rare_drop",
      "amount": "chance(0.1 + tier * 0.05) ? 1 : 0"
    }
  ]
}
```
- Tier 1: 15% の確率で 1 個
- Tier 8: 50% の確率で 1 個

---

### 3.3 動的な処理時間

`duration` フィールドにも式が使える場合の例。

```json
{
  "duration": "max(20, floor(200 / (1.0 + tier * 0.2)))"
}
```
- Tier が高いほど処理時間が短縮（最低 20 tick）

---

## 4. トラブルシューティング

### 4.1 よくあるエラー

#### エラー1: 式が評価されない
```json
// ❌ 間違い
{
  "amount": tier * 10
}

// ✅ 正しい
{
  "amount": "tier * 10"
}
```
**原因**: 式は文字列として記述する必要があります。

---

#### エラー2: 変数名の間違い
```json
// ❌ 間違い
{
  "amount": "Tier * 10"  // 大文字
}

// ✅ 正しい
{
  "amount": "tier * 10"  // 小文字
}
```
**原因**: 変数名は小文字で記述します。

---

#### エラー3: 全角文字の使用
```json
// ❌ 間違い
{
  "amount": "tier × 10"  // 全角の乗算記号
}

// ✅ 正しい
{
  "amount": "tier * 10"  // 半角の *
}
```
**原因**: 演算子は半角英数字で記述します。

---

#### エラー4: ゼロ除算
```json
// ❌ 危険
{
  "amount": "1000 / energy_p"
}

// ✅ 安全
{
  "amount": "energy_p > 0 ? floor(1000 / energy_p) : 0"
}
```
**原因**: エネルギーが 0 の時にゼロ除算エラーが発生。

---

### 4.2 デバッグ方法

#### ステップ1: シンプルな式から始める
```json
// まずは基本的な変数のみ
{
  "amount": "tier"
}
```

#### ステップ2: 徐々に複雑化
```json
// 演算を追加
{
  "amount": "tier * 2"
}
```

#### ステップ3: 条件分岐を追加
```json
// 三項演算子を使用
{
  "amount": "tier > 5 ? tier * 2 : tier"
}
```

#### ステップ4: ログファイルを確認
- `logs/latest.log` でエラーメッセージを確認
- パース失敗時は詳細なエラーが出力される

---

### 4.3 パフォーマンスのヒント

#### ヒント1: 重い計算を避ける
```json
// ⚠️ 重い
{
  "amount": "pow(pow(tier, 2), 2)"
}

// ✅ 軽い
{
  "amount": "pow(tier, 4)"
}
```

#### ヒント2: 不要な再計算を避ける
- 式は毎回評価されるため、複雑すぎる式は避ける
- 可能であれば事前計算された値を NBT に保存

---

## 5. レシピ設計のベストプラクティス

### 5.1 バランス調整

1. **初期値を適切に設定**
   - Tier 1 でも実用的な出力を確保
   - 指数的な成長は慎重に

2. **上限を設ける**
   ```json
   {
     "amount": "min(1000, tier * 100)"
   }
   ```

3. **下限を保証**
   ```json
   {
     "amount": "max(1, floor(energy_p * 10))"
   }
   ```

---

### 5.2 ユーザビリティ

1. **直感的な動作**
   - Tier が上がれば効率も上がる（分かりやすい）
   - 逆転した動作は避ける

2. **フィードバックの提供**
   - JEI で式が表示される（今後実装予定）
   - ツールチップで現在値を表示（今後実装予定）

3. **ドキュメント化**
   - 特殊な式を使う場合はコメントを追加
   - Modpack 製作者向けの説明を用意

---

## 6. 参考リンク

- [JSON フォーマット完全版](./JSON_FORMAT.md)
- [開発者ガイド](./DEVELOPER_GUIDE.md)

---

*このドキュメントは、実際のユースケースを基に継続的に更新されます。*
