# Modular Machinery ドキュメント

Modular Machineryモジュールの技術ドキュメント一覧です。

## 📚 ドキュメント

### システム設計

#### [External Port Proxy システム](./EXTERNAL_PROXY.md)
外部ブロック（チェスト、タンク、エネルギーストレージ等）を機械の一部として統合するためのプロキシシステムの設計ドキュメント。

**内容**:
- Adapter + Proxy パターンの融合設計
- 6タイプのプロキシ実装（Item, Fluid, Energy, Gas, Essentia, Mana）
- AbstractExternalProxy 基底クラスの詳細
- Self-Validation Pattern との統合
- プロキシファクトリの登録方法
- コード例と使用例

**対象読者**: 開発者、デザインパターン学習者

---

---

## 💡 新機能ガイド

### 動的数量システム (Expression System)
レシピの入出力量を動的に変化させるための式システム。マシンの状態やワールド環境に応じて、柔軟なレシピ設計が可能になります。

**主な機能**:
- **マシン状態の参照**: エネルギー、流体、マナ、ガス、Tier、進捗など
- **ワールド環境の参照**: 時間、天候、月齢、バイオーム、経過日数など
- **数学関数**: 三角関数、対数、べき乗、乱数など
- **条件分岐**: 三項演算子、論理演算子による複雑な制御

**使用例**:
```json
{
  "inputs": [
    { "item": "minecraft:iron_ingot", "amount": "tier * 10 + 5" }
  ],
  "outputs": [
    { "fluid": "steam", "amount": "energy_p * 1000" }
  ]
}
```

**関連ドキュメント**:
- [JSON フォーマット: 動的数量](../recipes/JSON_FORMAT.md#31-動的数量-dynamic-amount) - 基本的な使い方
- [実用例集](../recipes/EXPRESSION_EXAMPLES.md) - パターン別の詳細な使用例

**対象読者**: レシピ作成者、Modpack 製作者

---

## 🔗 関連ドキュメント

### Recipe System
- [概要](../recipes/OVERVIEW.md)
- [JSON フォーマット](../recipes/JSON_FORMAT.md)
- [実用例集](../recipes/EXPRESSION_EXAMPLES.md) 🆕
- [開発者ガイド](../recipes/DEVELOPER_GUIDE.md)

### Structure System
- [概要](../structures/OVERVIEW.md)
- [JSON フォーマット](../structures/JSON_FORMAT.md)
- [開発者ガイド](../structures/DEVELOPER_GUIDE.md)

---

*このドキュメントは随時更新されます。*
