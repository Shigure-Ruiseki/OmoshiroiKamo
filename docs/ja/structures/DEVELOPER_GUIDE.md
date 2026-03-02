# 構造体システム: 開発者ガイド

このガイドは、コードを介して構造体 JSON システムを拡張しようとする開発者を対象としています。

## 1. 新しい要件 (Requirement) の登録

新しいタイプの要件（例：Botania の `manaInput`）を追加するには：

1. **`IStructureRequirement` を実装する**:
   カウントロジックを処理するクラスを作成します。
   ```java
   public class ManaRequirement implements IStructureRequirement {
       private final int min;
       public ManaRequirement(int min) { this.min = min; }
       
       @Override
       public boolean isMet(List<IModularPort> ports) {
           return ports.stream().filter(p -> p instanceof IManaPort).count() >= min;
       }
       
       public static ManaRequirement fromJson(String type, JsonObject json) {
           return new ManaRequirement(json.get("min").getAsInt());
       }
   }
   ```

2. **パーサーを登録する**:
   `FMLPreInitializationEvent` または `FMLInitializationEvent` 中に行います。
   ```java
   RequirementRegistry.register("manaInput", ManaRequirement::fromJson);
   ```

## 2. Visitor の使用

Visitor パターンを使用すると、構造体を走査して特定のアクションを実行できます。

### 例: ティア (Tier) のスキャン
構造体内にいくつのエリートブロックがあるかに基づいて「ティア」を決定したい場合：

```java
public class TierScannerVisitor implements IStructureVisitor {
    private int eliteCount = 0;

    @Override
    public void visit(IStructureEntry entry) {
        // ここでレイヤーやマッピングを反復処理できます
    }

    // 通常、StructureLib 統合を介したワールドスキャン中に呼び出されます
    public void onBlockFound(Block block, int meta) {
        if (block == ModBlocks.eliteCasing) eliteCount++;
    }
}
```

## 3. ベストプラクティス

- **登録順序**: JSON の読み込みが発生する前に、すべての要件が登録されていることを確認してください（通常は `postInit` より前）。
- **カプセル化**: `StructureEntry` インスタンスを直接作成するのではなく、`StructureEntryBuilder` を使用してください。
- **早期失敗 (Fail Fast)**: JSON をロードした直後にバリデーション Visitor を使用して、構文や論理的なエラーを早期にキャッチしてください。
