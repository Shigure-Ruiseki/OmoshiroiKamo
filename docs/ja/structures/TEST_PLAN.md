# 構造体システム: テスト計画

このドキュメントでは、構造体 JSON システムの包括的なテスト戦略について説明します。目標は、構造の整合性、パースの正確性、および堅牢なエラーハンドリングを保証することです。

## 1. テスト戦略 (7つのフェーズ)

### フェーズ 1: JSON 統合 (45テスト)
- **基本ロード**: `name`, `displayName`, `recipeGroup` および基本的なメタデータの検証。
- **レイヤーパース**: 複数レイヤー、新旧フォーマット、空の行の取り扱い。
- **マッピングパース**: 単一/複数ブロックの関連付け、ワイルドカード、予約記号。
- **要件 (Requirement) パース**: 全7種のリソースタイプ、min/max 値、レジストリ統合。
- **エラーハンドリング**: 必須フィールドの欠落、構文エラー、無効な型の検出。

### フェーズ 2: 構造バリデーション (40テスト)
- **レイヤーの一貫性**: 行幅の不一致、空レイヤー、サイズ制限 (1x1x1 から 50x50x50)。
- **シンボルの整合性**: レイヤー内の未定義シンボル、マッピング内の未使用シンボル、コントローラー (Q) の存在確認。
- **コントローラー検出**: 正確なオフセット計算と回転 (180°) 処理。
- **ブロック解決**: 有効/無効な ID および Mod 固有のブロック処理。

### フェーズ 3: Builder パターン (25テスト)
- **操作**: Null 安全性、必須フィールドの検証、流れるような API の挙動。
- **不変性 (Immutability)**: 構築後のオブジェクトが変更されないことの保証。

### フェーズ 4: 要件システム (30テスト)
- **クラス別ロジック**: アイテム、液体、エネルギー、マナ、ガス、エッセンシア、Vis の各ロジック。
- **レジストリ**: 動的登録、上書きの安全性、デフォルト値の挙動。

### フェーズ 5: Visitor パターン (20テスト)
- **走査**: 訪問中におけるレイヤーおよび要件の完全なカバー。
- **バリデーション Visitor**: 詳細なエラー収集とレポート。

### フェーズ 6: レジストリ & マネージャー (30テスト)
- **CustomStructureRegistry**: 定義の検索と StructureLib 統合。
- **StructureManager**: シングルトン状態とライフサイクル管理。

### フェーズ 7: シリアライゼーション (15テスト)
- **ラウンドトリップ**: `JSON -> オブジェクト -> JSON` の変換結果が同一であることの確認。

## 2. 優先度マトリクス

- **最優先 (CRITICAL)**: JSON ローダー、未定義シンボル検出、コントローラーオフセット計算。
- **重要 (HIGH)**: Builder バリデーション、要件レジストリ、バリデーション Visitor。
- **中 (MEDIUM)**: レジストリ管理、シリアライゼーション。

## 3. 実装ガイドライン
テストは `src/test/java/ruiseki/omoshiroikamo/api/structure/` に配置されます。レシピシステムのテストパターン (JUnit 5) に従ってください。

## 4. ファイルベース統合テスト

### 概要
実際の JSON ファイルを使用した統合テストを実装しています。テストリソースとして JSON ファイルを配置し、クラスパスから読み込むことで、実際の運用環境に近い形でのテストを実現します。

### テストリソースの配置
- **場所**: `src/test/resources/structures/`
- **形式**: 各構造を個別の JSON ファイルとして配置
- **ファイル数**: 14個の構造定義ファイル

### 個別ファイルリスト
1. `minimal.json` - 最小構造 (1x1x1)
2. `simple_3x3x1.json` - シンプルな3x3構造
3. `complex_3x3x3.json` - 3層の複雑な構造
4. `with_item_requirement.json` - Item requirement
5. `with_fluid_requirement.json` - Fluid requirement
6. `with_energy_requirement.json` - Energy requirement
7. `with_all_requirements.json` - 全4種類のrequirements
8. `with_display_name.json` - displayName オプション
9. `with_recipe_group.json` - recipeGroup オプション
10. `with_controller_offset.json` - controllerOffset オプション
11. `with_tint_color.json` - tintColor オプション
12. `with_tier.json` - tier オプション
13. `with_multiple_blocks.json` - 複数ブロックマッピング
14. `with_metadata.json` - メタデータ付きブロック

### テストクラス: StructureFileLoaderTest
**場所**: `src/test/java/ruiseki/omoshiroikamo/api/structure/integration/StructureFileLoaderTest.java`

**テスト数**: 26テスト

**主要な検証項目**:
- 構造が正しく読み込まれること
- name, layers, mappings が全て設定されていること
- 各種オプション (displayName, recipeGroup, controllerOffset, tintColor, tier) の読み込み
- 様々な requirements (Item, Fluid, Energy, Mana) の読み込み
- 複数ブロックマッピングの処理
- メタデータ付きブロックの処理
- default mappings の読み込み

**実装パターン**:
```java
@BeforeAll
public static void setUpAll() {
    structures = new HashMap<>();
    defaultMappings = new HashMap<>();

    // 各ファイルを個別に読み込み
    for (String filename : STRUCTURE_FILES) {
        InputStream inputStream = StructureFileLoaderTest.class
            .getResourceAsStream("/structures/" + filename);

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonElement element = new JsonParser().parse(new JsonReader(reader));
            StructureJsonReader.FileData data = StructureJsonReader.readFile(element);

            // 構造とマッピングをマージ
            structures.putAll(data.structures);
            defaultMappings.putAll(data.defaultMappings);
        }
    }
}
```

**重要な設計方針**:
- **テストスキップしない**: ファイルが存在しない場合は `assertNotNull` でテストを失敗させる
- **クラスパスリソース**: `getResourceAsStream()` を使用してテストリソースから読み込み
- **個別ファイル形式**: 各構造を別々のファイルとして管理し、保守性を向上
- **マージパターン**: 複数ファイルから読み込んだ構造を単一の Map にマージ
- **互換性**: Minecraft 1.7.10 の古い Gson API に対応 (`new JsonParser().parse(new JsonReader(reader))`)

この統合テストにより、JSON パース機能が実際のファイルから正しく動作することを保証します。
