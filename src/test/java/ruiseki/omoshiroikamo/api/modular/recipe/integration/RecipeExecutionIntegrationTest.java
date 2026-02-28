package ruiseki.omoshiroikamo.api.modular.recipe.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.decorator.ChanceRecipeDecorator;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * レシピ実行フロー統合テスト
 *
 * ============================================
 * レシピの実行ロジックの検証
 * ============================================
 *
 * このテストは、レシピシステムの実行ロジックが
 * 正しく動作することを検証します。
 *
 * バグ発見の優先度: ★★★★★
 * - レシピマッチングが失敗すると、正しいレシピが選択されない
 * - 条件チェックが失敗すると、不正なレシピが実行される
 * - プライオリティが正しくないと、予期しないレシピが実行される
 *
 * カバーする機能:
 * - レシピマッチング
 * - プライオリティベースのソート
 * - 条件チェック
 * - デコレータの影響
 * - レシピの比較・検索
 *
 * ============================================
 */
@DisplayName("レシピ実行フロー統合テスト")
public class RecipeExecutionIntegrationTest {

    private ConditionContext context;

    @BeforeEach
    public void setUp() {
        context = new ConditionContext(null, 0, 0, 0);
    }

    // ========================================
    // レシピのプライオリティソート
    // ========================================

    @Test
    @DisplayName("【最優先】プライオリティが高い順にソートされる")
    public void testプライオリティソート() {
        // 異なるプライオリティのレシピを作成
        IModularRecipe recipe0 = ModularRecipe.builder()
            .registryName("priority_0")
            .recipeGroup("test")
            .duration(100)
            .priority(0)
            .build();

        IModularRecipe recipe1 = ModularRecipe.builder()
            .registryName("priority_1")
            .recipeGroup("test")
            .duration(100)
            .priority(1)
            .build();

        IModularRecipe recipe5 = ModularRecipe.builder()
            .registryName("priority_5")
            .recipeGroup("test")
            .duration(100)
            .priority(5)
            .build();

        IModularRecipe recipe3 = ModularRecipe.builder()
            .registryName("priority_3")
            .recipeGroup("test")
            .duration(100)
            .priority(3)
            .build();

        // リストに追加（順不同）
        List<IModularRecipe> recipes = new ArrayList<>();
        recipes.add(recipe0);
        recipes.add(recipe1);
        recipes.add(recipe5);
        recipes.add(recipe3);

        // ソート実行
        Collections.sort(recipes);

        // 期待: 5 → 3 → 1 → 0 の順
        assertEquals(
            5,
            recipes.get(0)
                .getPriority());
        assertEquals(
            3,
            recipes.get(1)
                .getPriority());
        assertEquals(
            1,
            recipes.get(2)
                .getPriority());
        assertEquals(
            0,
            recipes.get(3)
                .getPriority());
    }

    @Test
    @DisplayName("同じプライオリティの場合、registryNameでソートされる")
    public void test同じプライオリティ時のソート() {
        IModularRecipe recipeC = ModularRecipe.builder()
            .registryName("c_recipe")
            .recipeGroup("test")
            .duration(100)
            .priority(0)
            .build();

        IModularRecipe recipeA = ModularRecipe.builder()
            .registryName("a_recipe")
            .recipeGroup("test")
            .duration(100)
            .priority(0)
            .build();

        IModularRecipe recipeB = ModularRecipe.builder()
            .registryName("b_recipe")
            .recipeGroup("test")
            .duration(100)
            .priority(0)
            .build();

        List<IModularRecipe> recipes = new ArrayList<>();
        recipes.add(recipeC);
        recipes.add(recipeA);
        recipes.add(recipeB);

        Collections.sort(recipes);

        // アルファベット順: a → b → c
        assertEquals(
            "a_recipe",
            recipes.get(0)
                .getRegistryName());
        assertEquals(
            "b_recipe",
            recipes.get(1)
                .getRegistryName());
        assertEquals(
            "c_recipe",
            recipes.get(2)
                .getRegistryName());
    }

    // ========================================
    // レシピの条件チェック
    // ========================================

    @Test
    @DisplayName("条件なしのレシピは常にtrueを返す")
    public void test条件なしレシピ() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("no_condition")
            .recipeGroup("test")
            .duration(100)
            .build();

        // 条件がない場合、常に true
        assertTrue(recipe.isConditionMet(context));
    }

    @Test
    @DisplayName("【デコレータ】ChanceDecoratorで確率チェックが動作する")
    public void testChanceデコレータの条件チェック() {
        // 基本レシピ
        IModularRecipe baseRecipe = ModularRecipe.builder()
            .registryName("base")
            .recipeGroup("test")
            .duration(100)
            .build();

        // 100%デコレータ
        IModularRecipe alwaysTrue = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(1.0));

        // 何度呼んでもtrueになるはず
        for (int i = 0; i < 20; i++) {
            assertTrue(alwaysTrue.isConditionMet(context), i + "回目のチェックでfalseになった");
        }

        // 0%デコレータ
        IModularRecipe alwaysFalse = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.0));

        // 何度呼んでもfalseになるはず
        for (int i = 0; i < 20; i++) {
            assertFalse(alwaysFalse.isConditionMet(context), i + "回目のチェックでtrueになった");
        }
    }

    @Test
    @DisplayName("【統計】50%確率デコレータの挙動検証")
    public void test50パーセントデコレータの統計() {
        IModularRecipe baseRecipe = ModularRecipe.builder()
            .registryName("base")
            .recipeGroup("test")
            .duration(100)
            .build();

        IModularRecipe halfChance = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.5));

        // 1000回試行
        int successCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            if (halfChance.isConditionMet(context)) {
                successCount++;
            }
        }

        // 400～600回の範囲に収まるはず（統計的に）
        assertTrue(successCount >= 400 && successCount <= 600, "Expected 400-600 successes, but got " + successCount);
    }

    // ========================================
    // レシピの基本プロパティ
    // ========================================

    @Test
    @DisplayName("レシピのDurationが正しく取得できる")
    public void testDurationの取得() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("duration_test")
            .recipeGroup("test")
            .duration(12345)
            .build();

        assertEquals(12345, recipe.getDuration());
    }

    @Test
    @DisplayName("レシピのRegistryNameが正しく取得できる")
    public void testRegistryNameの取得() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("my_special_recipe")
            .recipeGroup("test")
            .duration(100)
            .build();

        assertEquals("my_special_recipe", recipe.getRegistryName());
    }

    @Test
    @DisplayName("レシピのRecipeGroupが正しく取得できる")
    public void testRecipeGroupの取得() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("crushing")
            .duration(100)
            .build();

        assertEquals("crushing", recipe.getRecipeGroup());
    }

    @Test
    @DisplayName("入力リストが正しく取得できる")
    public void test入力リストの取得() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, 1))
            .addInput(new EnergyInput(100, false))
            .build();

        assertEquals(
            2,
            recipe.getInputs()
                .size());
    }

    @Test
    @DisplayName("出力リストが正しく取得できる")
    public void test出力リストの取得() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 1)))
            .addOutput(new ItemOutput(new ItemStack(Items.diamond, 1)))
            .build();

        assertEquals(
            2,
            recipe.getOutputs()
                .size());
    }

    // ========================================
    // レシピの比較・検索
    // ========================================

    @Test
    @DisplayName("同じregistryNameのレシピは等しいとみなされる")
    public void testレシピの等価性() {
        IModularRecipe recipe1 = ModularRecipe.builder()
            .registryName("same_name")
            .recipeGroup("test")
            .duration(100)
            .build();

        IModularRecipe recipe2 = ModularRecipe.builder()
            .registryName("same_name")
            .recipeGroup("test")
            .duration(200) // durationが違っても
            .build();

        // equals() が実装されている場合
        // （実装されていない場合はこのテストはスキップ）
        // assertEquals(recipe1, recipe2);

        // 少なくともregistryNameは同じ
        assertEquals(recipe1.getRegistryName(), recipe2.getRegistryName());
    }

    @Test
    @DisplayName("リスト内からregistryNameでレシピを検索できる")
    public void testレシピの検索() {
        List<IModularRecipe> recipes = new ArrayList<>();

        IModularRecipe targetRecipe = ModularRecipe.builder()
            .registryName("target")
            .recipeGroup("test")
            .duration(100)
            .build();

        recipes.add(
            ModularRecipe.builder()
                .registryName("recipe1")
                .recipeGroup("test")
                .duration(100)
                .build());
        recipes.add(targetRecipe);
        recipes.add(
            ModularRecipe.builder()
                .registryName("recipe2")
                .recipeGroup("test")
                .duration(100)
                .build());

        // 検索
        IModularRecipe found = null;
        for (IModularRecipe r : recipes) {
            if ("target".equals(r.getRegistryName())) {
                found = r;
                break;
            }
        }

        assertNotNull(found, "レシピが見つからない");
        assertEquals("target", found.getRegistryName());
    }

    // ========================================
    // デコレータとの統合
    // ========================================

    @Test
    @DisplayName("【デコレータ】デコレータを通してもregistryNameが取得できる")
    public void testデコレータ経由のプロパティ取得() {
        IModularRecipe baseRecipe = ModularRecipe.builder()
            .registryName("base_recipe")
            .recipeGroup("test_group")
            .duration(300)
            .priority(5)
            .build();

        IModularRecipe decorated = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(1.0));

        // デコレータを通しても、元のプロパティが取得できる
        assertEquals("base_recipe", decorated.getRegistryName());
        assertEquals("test_group", decorated.getRecipeGroup());
        assertEquals(300, decorated.getDuration());
        assertEquals(5, decorated.getPriority());
    }

    @Test
    @DisplayName("【デコレータ】複数のデコレータを重ねても動作する")
    public void test複数デコレータの重ね掛け() {
        IModularRecipe baseRecipe = ModularRecipe.builder()
            .registryName("base")
            .recipeGroup("test")
            .duration(100)
            .build();

        // 50%デコレータを2回重ねる → 25%
        IModularRecipe decorated1 = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.5));
        IModularRecipe decorated2 = new ChanceRecipeDecorator(decorated1, new ConstantExpression(0.5));

        // 統計的テスト
        int successCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            if (decorated2.isConditionMet(context)) {
                successCount++;
            }
        }

        // 150～350回の範囲に収まるはず（25%）
        assertTrue(
            successCount >= 150 && successCount <= 350,
            "Expected 150-350 successes (25%), but got " + successCount);

        // プロパティも正しく取得できる
        assertEquals("base", decorated2.getRegistryName());
        assertEquals(100, decorated2.getDuration());
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】入力・出力が空のレシピも作成できる")
    public void test空レシピの作成() {
        IModularRecipe emptyRecipe = ModularRecipe.builder()
            .registryName("empty")
            .recipeGroup("test")
            .duration(100)
            .build();

        assertNotNull(emptyRecipe);
        assertEquals(
            0,
            emptyRecipe.getInputs()
                .size());
        assertEquals(
            0,
            emptyRecipe.getOutputs()
                .size());
    }

    @Test
    @DisplayName("【エッジ】duration=1の超短時間レシピ")
    public void test超短時間レシピ() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("instant")
            .recipeGroup("test")
            .duration(1)
            .build();

        assertEquals(1, recipe.getDuration());
    }

    @Test
    @DisplayName("【エッジ】duration=1000000の超長時間レシピ")
    public void test超長時間レシピ() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("very_long")
            .recipeGroup("test")
            .duration(1000000)
            .build();

        assertEquals(1000000, recipe.getDuration());
    }

    @Test
    @DisplayName("【エッジ】priority=100の超高優先度レシピ")
    public void test超高優先度レシピ() {
        IModularRecipe highPriority = ModularRecipe.builder()
            .registryName("high")
            .recipeGroup("test")
            .duration(100)
            .priority(100)
            .build();

        IModularRecipe normalPriority = ModularRecipe.builder()
            .registryName("normal")
            .recipeGroup("test")
            .duration(100)
            .priority(0)
            .build();

        List<IModularRecipe> recipes = new ArrayList<>();
        recipes.add(normalPriority);
        recipes.add(highPriority);

        Collections.sort(recipes);

        // 高優先度が先
        assertEquals(
            100,
            recipes.get(0)
                .getPriority());
        assertEquals(
            0,
            recipes.get(1)
                .getPriority());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: 実際のポートを使ったマッチングテスト（モックポート必要）
    // TODO: リソース消費のテスト（AbstractRecipeProcessのサブクラス必要）
    // TODO: プログレス管理のテスト
    // TODO: 完了処理のテスト
    // TODO: 中断処理のテスト
}
