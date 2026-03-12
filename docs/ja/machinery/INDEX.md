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

## 🔗 関連ドキュメント

### Recipe System
- [概要](../recipes/OVERVIEW.md)
- [JSON フォーマット](../recipes/JSON_FORMAT.md)
- [開発者ガイド](../recipes/DEVELOPER_GUIDE.md)

### Structure System
- [概要](../structures/OVERVIEW.md)
- [JSON フォーマット](../structures/JSON_FORMAT.md)
- [開発者ガイド](../structures/DEVELOPER_GUIDE.md)

---

*このドキュメントは随時更新されます。*
