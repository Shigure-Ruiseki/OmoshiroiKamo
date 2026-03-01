package ruiseki.omoshiroikamo.api.modular.recipe.edge;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * エッジケース・境界値テスト
 *
 * ============================================
 * システムの極限状態をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - 境界値でのオーバーフロー/アンダーフロー検出
 * - Null安全性の確認
 * - 空データの扱い
 * - 極端な値での動作確認
 *
 * ============================================
 */
@DisplayName("エッジケース・境界値テスト")
public class EdgeCaseTest {

    // ========================================
    // 境界値テスト
    // ========================================

    @Test
    @DisplayName("【境界】Duration = Integer.MAX_VALUE")
    public void test最大Duration() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("max_duration")
            .recipeGroup("test")
            .duration(Integer.MAX_VALUE)
            .build();

        assertEquals(Integer.MAX_VALUE, recipe.getDuration());
    }

    @Test
    @DisplayName("【境界】Duration = 1（最小）")
    public void test最小Duration() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("min_duration")
            .recipeGroup("test")
            .duration(1)
            .build();

        assertEquals(1, recipe.getDuration());
    }

    @Test
    @DisplayName("【境界】Priority = Integer.MAX_VALUE")
    public void test最大Priority() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("max_priority")
            .recipeGroup("test")
            .duration(100)
            .priority(Integer.MAX_VALUE)
            .build();

        assertEquals(Integer.MAX_VALUE, recipe.getPriority());
    }

    @Test
    @DisplayName("【境界】Priority = Integer.MIN_VALUE")
    public void test負のPriority() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("negative_priority")
            .recipeGroup("test")
            .duration(100)
            .priority(Integer.MIN_VALUE)
            .build();

        assertEquals(Integer.MIN_VALUE, recipe.getPriority());
    }

    // ========================================
    // Null/空データテスト
    // ========================================

    @Test
    @DisplayName("【Null安全】入力・出力が空のレシピ")
    public void test空レシピ() {
        IModularRecipe emptyRecipe = ModularRecipe.builder()
            .registryName("empty")
            .recipeGroup("test")
            .duration(100)
            .build();

        assertNotNull(emptyRecipe.getInputs());
        assertNotNull(emptyRecipe.getOutputs());
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
    @DisplayName("【Null安全】registryNameが空文字列（バリデーションでエラーになるべき）")
    public void test空のRegistryName() {
        // 空のregistryNameでビルドを試みる
        assertThrows(Exception.class, () -> {
            ModularRecipe.builder()
                .registryName("")
                .recipeGroup("test")
                .duration(100)
                .build();
        }, "空のregistryNameはエラーになるべき");
    }

    // ========================================
    // 特殊ケース
    // ========================================

    @Test
    @DisplayName("【特殊】同じアイテムが入力と出力の両方にある")
    public void test入出力同一アイテム() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("same_item_io")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, 1))
            .addOutput(new ItemOutput(new ItemStack(Items.iron_ingot, 1)))
            .build();

        assertEquals(
            1,
            recipe.getInputs()
                .size());
        assertEquals(
            1,
            recipe.getOutputs()
                .size());
    }

    @Test
    @DisplayName("【特殊】非常に長いregistryName（255文字）")
    public void test長いRegistryName() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            longName.append("a");
        }

        IModularRecipe recipe = ModularRecipe.builder()
            .registryName(longName.toString())
            .recipeGroup("test")
            .duration(100)
            .build();

        assertEquals(
            255,
            recipe.getRegistryName()
                .length());
    }

    @Test
    @DisplayName("【特殊】registryNameに特殊文字")
    public void test特殊文字RegistryName() {
        // アンダースコアやハイフンは許可されるべき
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("recipe_with-special_chars123")
            .recipeGroup("test")
            .duration(100)
            .build();

        assertEquals("recipe_with-special_chars123", recipe.getRegistryName());
    }

    @Test
    @DisplayName("【特殊】recipeGroupが非常に長い")
    public void test長いRecipeGroup() {
        StringBuilder longGroup = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longGroup.append("group_");
        }

        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup(longGroup.toString())
            .duration(100)
            .build();

        assertTrue(
            recipe.getRecipeGroup()
                .length() > 500);
    }

    // ========================================
    // 大量データテスト
    // ========================================

    @Test
    @DisplayName("【大量】50個の入力を持つレシピ")
    public void test大量入力() {
        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("many_inputs")
            .recipeGroup("test")
            .duration(100);

        for (int i = 0; i < 50; i++) {
            builder.addInput(new ItemInput(Items.iron_ingot, i + 1));
        }

        IModularRecipe recipe = builder.build();

        assertEquals(
            50,
            recipe.getInputs()
                .size());
    }

    @Test
    @DisplayName("【大量】50個の出力を持つレシピ")
    public void test大量出力() {
        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("many_outputs")
            .recipeGroup("test")
            .duration(100);

        for (int i = 0; i < 50; i++) {
            builder.addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, i + 1)));
        }

        IModularRecipe recipe = builder.build();

        assertEquals(
            50,
            recipe.getOutputs()
                .size());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: メモリリークテスト（大量のレシピ作成・破棄）
    // TODO: マルチスレッドテスト（並行アクセス）
    // TODO: パフォーマンステスト（10000レシピのソート）
}
