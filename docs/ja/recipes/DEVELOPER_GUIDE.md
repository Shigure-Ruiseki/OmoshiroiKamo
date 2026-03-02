# レシピシステム: 開発者ガイド

このガイドでは、コードを介してレシピシステムを拡張する方法について説明します。

## 1. 新しい入出力 (I/O) タイプの追加

新しいリソース（例：Thaumcraft の Essentia）をサポートするには：

1. **`IRecipeInput` および `IRecipeOutput` を実装する**:
   特定のリソースのチェック・消費、およびチェック・生成の方法を定義します。
2. **`MachineryJsonReader` で登録する**:
   `MachineryJsonReader` はリソースタイプを処理するために内部マップを使用しています。新しい `type` 文字列を認識できるようにパースロジックを更新してください。

## 2. Recipe Decorator の使用

Decorator を使用すると、元のレシピクラスを変更せずにレシピにカスタムロジックを追加できます。これは「ボーナス出力」や「効率ブースト」などに利用されます。

```java
public class MyDecorator extends RecipeDecorator {
    public MyDecorator(IModularRecipe internal) { super(internal); }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        boolean success = super.processOutputs(outputPorts, simulate);
        if (success && !simulate) {
            // ここにボーナスロジックを記述
        }
        return success;
    }
}
```

## 3. API を介した統合

`RecipeLoader` は、レシピを操作するための中央窓口です。

- **`allRecipes()`**: 登録されているすべてのレシピを返します。
- **`getRecipesByGroup(String group)`**: 特定のマシン用のレシピをフィルタリングして取得します。

## 4. 使用されているデザインパターン

- **Builder パターン**: 複雑な `ModularRecipe` オブジェクトの構築に使用。
- **Decorator パターン**: 動的な機能拡張に使用。
- **Strategy パターン**: `IRecipeInput` の実装が、リソース処理の戦略（Strategy）として機能します。
- **Flyweight パターン**: メモリを節約するため、共通のリソース定義が共有されることがあります。
