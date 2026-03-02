# レシピシステム: 開発者ガイド

このガイドでは、コードを介してレシピシステムを拡張する方法について説明します。

## 1. 新しいポートおよびリソースタイプの追加

新しいリソース（例：Thaumcraft の Essentia や新しいエネルギー形式）をサポートするための手順は以下の通りです。

1.  **`IPortType.Type` への定義追加**:
    `ruiseki.omoshiroikamo.api.modular.IPortType` 内の `Type` 列挙型に新しい定数を追加します。
2.  **独自の Port インターフェースの作成**:
    `IModularPort` を継承したインターフェースを作成し、リソースの保持・入出力を定義します。
3.  **TileEntity の実装**:
    `AbstractModularPortTE`（または `AbstractItemIOPortTE` / `AbstractEnergyIOPortTE` 等の具象基底クラス）を継承して、実際の TileEntity を実装します。
4.  **`IRecipeInput` / `IRecipeOutput` の実装**:
    `AbstractRecipeInput` / `AbstractRecipeOutput` を継承し、レシピ処理中におけるリソースの不足チェックおよび消費・生産ロジックを実装します。
5.  **パーサーの登録**:
    `InputParserRegistry` および `OutputParserRegistry` の `static` ブロック（または初期化時）に、JSON キーとパース関数の対応を登録します。
    ```java
    InputParserRegistry.register("my_resource", MyResourceInput::fromJson);
    ```

## 2. 論理条件 (Logical Conditions / CoRパターン)

レシピには、複数の条件を論理演算で組み合わせた複雑な制約を課すことができます。これは Chain of Responsibility (CoR) パターンに似た構造で実装されています。

### ICondition の実装
新しい条件タイプを作成するには `ICondition` を実装します。
```java
public class MyCondition implements ICondition {
    @Override
    public boolean isMet(ConditionContext context) {
        // 条件判定ロジック
        return true;
    }
}
```

### 論理演算子
以下の演算子が標準で提供されており、これらをネストすることで複雑なロジックを JSON で記述可能です。
- `OpAnd` (AND) / `OpOr` (OR) / `OpNot` (NOT)
- `OpXor` / `OpNand` / `OpNor`

これらは `ConditionParserRegistry` に登録されており、`Conditions.registerDefaults()` で初期化されます。

## 3. Recipe Decorator の使用

Decorator を使用すると、既存のレシピに「確率的な成功」や「ボーナスアイテム」などの動的な挙動を追加できます。

- `ChanceRecipeDecorator`: 確率によるレシピの成否を制御。
- `BonusOutputDecorator`: 確率で追加の出力を生成。
- `WeightedRandomDecorator`: 重み付きリストから出力を選択。

```java
public class MyDecorator extends RecipeDecorator {
    public MyDecorator(IModularRecipe internal) { super(internal); }
    // processOutputs 等をオーバーライドして挙動をカスタマイズ
}
```

## 4. エラーハンドリングとバリデーション

JSON のパースエラーや構造上の不備は、中央集約型のエラー収集システムで管理されます。

- **`JsonErrorCollector`**: 全てのパースエラーを収集するシングルトン。
- **エラー出力**: 収集されたエラーは `config/omoshiroikamo/json_errors.txt` に書き出されます。
- **ユーザー通知**: エラーが検出された場合、プレイヤーのログイン時にチャットメッセージで警告が表示されます。

開発者は、独自のパースロジック内で `JsonErrorCollector.getInstance().collect(type, message)` を呼び出すことで、この仕組みを利用できます。

## 5. JSON フレームワーク (Reader & Writer)

プロジェクトでは、データの読み書きを統一するために `AbstractJsonReader` および `AbstractJsonWriter` を提供しています。

- **`AbstractJsonReader<T>`**:
  - `read()`: ディレクトリ内の全 JSON ファイルを走査し、オブジェクトをキャッシュします。
  - `ParsingContext`: 現在処理中のファイル情報を保持し、エラー発生時のファイル特定を容易にします。
- **`AbstractJsonWriter<T>`**:
  - `write()`: オブジェクトを整形された JSON としてファイルに書き出します。

## 6. 使用されているデザインパターン

- **Builder パターン**: `ModularRecipe` の構築。
- **Decorator パターン**: レシピ挙動の拡張。
- **Strategy パターン**: `IRecipeInput` / `IExpression` によるロジックの切り替え。
- **Visitor パターン**: レシピの走査・バリデーション。
- **Flyweight パターン**: 共通リソース定義の共有。

## 7. テスト

レシピエンジンの整合性は `RecipeLoaderTest` 等の JUnit 5 テストによって検証されます。詳細は [テスト計画](./TEST_PLAN.md) を参照してください。
