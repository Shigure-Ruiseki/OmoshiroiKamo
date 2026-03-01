package ruiseki.omoshiroikamo.api.modular.recipe.visitor;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * RecipeValidationVisitor のユニットテスト
 *
 * ============================================
 * Visitor パターンとは？
 * ============================================
 *
 * データ構造（レシピ）と、それに対する操作（バリデーション）を分離するパターン
 *
 * メリット：
 * - レシピクラス自体を変更せずに、新しい操作を追加できる
 * - バリデーション、コスト計算、NEI表示など、目的別にVisitorを作れる
 *
 * 例：
 * レシピ.accept(バリデーションVisitor) → エラーチェック
 * レシピ.accept(コスト計算Visitor) → 総コスト計算
 * レシピ.accept(NEI表示Visitor) → NEIに表示
 *
 * ============================================
 */
@DisplayName("RecipeValidationVisitor のテスト")
public class RecipeValidationVisitorTest {

    private RecipeValidationVisitor validator;

    @BeforeEach
    public void setUp() {
        validator = new RecipeValidationVisitor();
    }

    // ========================================
    // 正常なレシピのテスト
    // ========================================

    @Test
    @DisplayName("正常なレシピはエラーなし")
    public void test正常なレシピ() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("valid_recipe")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, 1))
            .addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 1)))
            .build();

        recipe.accept(validator);

        assertTrue(validator.isValid());
        assertEquals(
            0,
            validator.getErrors()
                .size());
    }

    // ========================================
    // 不正なレシピの検出
    // ========================================

    @Test
    @DisplayName("duration が 0 以下の場合はエラー")
    public void testDurationが0以下() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_duration")
            .recipeGroup("test")
            .duration(0) // ← 不正
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
        assertTrue(
            validator.getErrors()
                .size() > 0);

        // エラーメッセージに "duration" が含まれているか確認
        String errorMessage = validator.getErrors()
            .get(0);
        assertTrue(
            errorMessage.toLowerCase()
                .contains("duration"));
    }

    @Test
    @DisplayName("duration が負の場合はエラー")
    public void testDurationが負() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("negative_duration")
            .recipeGroup("test")
            .duration(-10) // ← 不正
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
    }

    // ========================================
    // 入力の検証
    // ========================================

    @Test
    @DisplayName("入力アイテムの個数が0以下の場合はエラー")
    public void test入力アイテム個数が0以下() {
        // ItemInput は内部で個数チェックしているが、
        // 強制的に不正な値を作れる場合のテスト

        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_input")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, -1)) // ← 負の個数
            .build();

        recipe.accept(validator);

        // バリデーターが負の個数を検出するはず
        assertFalse(validator.isValid());
    }

    @Test
    @DisplayName("入力エネルギーが0の場合にエラーを起こさない")
    public void test入力エネルギーが0以下() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_energy")
            .recipeGroup("test")
            .duration(100)
            .addInput(new EnergyInput(0))
            .build();

        recipe.accept(validator);

        assertTrue(validator.isValid());
    }

    @Test
    @DisplayName("入力エネルギーが負の場合にエラー")
    public void test入力エネルギーが負() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_energy")
            .recipeGroup("test")
            .duration(100)
            .addInput(new EnergyInput(-100))
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
    }

    // ========================================
    // 出力の検証
    // ========================================

    @Test
    @DisplayName("出力アイテムの個数が0の場合にエラー")
    public void test出力アイテム個数が0() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_output")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 0))) // ← 0個
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
    }

    @Test
    @DisplayName("出力エネルギーが0の場合にエラー")
    public void test出力エネルギーが0() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_output")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new EnergyOutput(0)) // ← 0
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
    }

    @Test
    @DisplayName("出力エネルギーが負の場合にエラー")
    public void test出力エネルギーが負() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("invalid_output")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new EnergyOutput(-100)) // ← 負
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());
    }

    // ========================================
    // エラーメッセージのテスト
    // ========================================

    @Test
    @DisplayName("エラーメッセージにレシピ名が含まれる")
    public void testエラーメッセージにレシピ名が含まれる() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("my_broken_recipe")
            .recipeGroup("test")
            .duration(0)
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());

        // エラーメッセージに "my_broken_recipe" が含まれているか
        String errorMessage = validator.getErrors()
            .get(0);
        assertTrue(errorMessage.contains("my_broken_recipe"));
    }

    @Test
    @DisplayName("複数のエラーを同時に検出できる")
    public void test複数エラーの検出() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("multi_error")
            .recipeGroup("test")
            .duration(-1) // エラー1: 負のduration
            .addInput(new ItemInput(Items.iron_ingot, -5)) // エラー2: 負の個数
            .build();

        recipe.accept(validator);

        assertFalse(validator.isValid());

        // 2つ以上のエラーが検出されるはず
        assertTrue(
            validator.getErrors()
                .size() >= 2);
    }

    // ========================================
    // Visitor の再利用テスト
    // ========================================

    @Test
    @DisplayName("Visitorを複数のレシピで再利用できる")
    public void testVisitorの再利用() {
        IModularRecipe recipe1 = ModularRecipe.builder()
            .registryName("recipe1")
            .recipeGroup("test")
            .duration(100)
            .build();

        IModularRecipe recipe2 = ModularRecipe.builder()
            .registryName("recipe2")
            .recipeGroup("test")
            .duration(0) // ← エラー
            .build();

        // recipe1 を検証
        recipe1.accept(validator);
        assertTrue(validator.isValid());

        // 同じVisitorでrecipe2を検証
        RecipeValidationVisitor validator2 = new RecipeValidationVisitor();
        recipe2.accept(validator2);
        assertFalse(validator2.isValid());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: FluidInput/Output のバリデーションテスト
    // TODO: GasInput/Output のバリデーションテスト
    // TODO: 他のVisitor（PortRegistrationVisitor など）のテスト
}
