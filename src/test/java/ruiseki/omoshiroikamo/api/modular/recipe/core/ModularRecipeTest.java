package ruiseki.omoshiroikamo.api.modular.recipe.core;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * ModularRecipe クラスのユニットテスト
 *
 * ============================================
 * テストコードの読み方（初心者向け）
 * ============================================
 *
 * 1. @Test = このメソッドが「1つのテスト」
 * 2. メソッド名 = 何をテストするか（日本語OK）
 * 3. assertEquals(期待値, 実際の値) = 値が一致するかチェック
 * 4. assertTrue(条件) = 条件がtrueかチェック
 * 5. assertThrows(例外クラス, 処理) = 例外が投げられるかチェック
 *
 * ============================================
 * テストの実行方法
 * ============================================
 *
 * コマンドライン:
 * gradlew test
 *
 * IntelliJ IDEA:
 * クラス名を右クリック → "Run 'ModularRecipeTest'"
 *
 * ============================================
 */
@DisplayName("ModularRecipe のテスト")
public class ModularRecipeTest {

    private ModularRecipe.Builder basicBuilder;

    @BeforeEach
    public void setUp() {
        basicBuilder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .name("Test Recipe")
            .duration(100)
            .priority(0);
    }

    // ========================================
    // 基本的なテスト
    // ========================================

    @Test
    @DisplayName("レシピが正常に作成できる")
    public void testレシピの作成() {
        ModularRecipe recipe = basicBuilder.addInput(new ItemInput(Items.iron_ingot, 1))
            .addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 1)))
            .build();

        assertEquals("test_recipe", recipe.getRegistryName());
        assertEquals("test_group", recipe.getRecipeGroup());
        assertEquals(100, recipe.getDuration());
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
    @DisplayName("registryNameなしでビルドするとエラー")
    public void testRegistryNameなしでエラー() {
        // registryNameを設定「しない」ビルダー
        ModularRecipe.Builder invalidBuilder = ModularRecipe.builder()
            .recipeGroup("test_group")
            .duration(100);

        // build()を呼ぶと IllegalStateException が投げられる「はず」
        assertThrows(IllegalStateException.class, () -> { invalidBuilder.build(); });
    }

    @Test
    @DisplayName("recipeGroupなしでビルドするとエラー")
    public void testRecipeGroupなしでエラー() {
        ModularRecipe.Builder invalidBuilder = ModularRecipe.builder()
            .registryName("test")
            .duration(100);

        assertThrows(IllegalStateException.class, () -> { invalidBuilder.build(); });
    }

    @Test
    @DisplayName("入力と出力のリストが変更不可能（不変性のテスト）")
    public void test入力出力リストの不変性() {
        ModularRecipe recipe = basicBuilder.addInput(new ItemInput(Items.iron_ingot, 1))
            .build();

        // getInputs() で取得したリストに後から追加しようとするとエラーになる「はず」
        // （これにより、レシピが勝手に変更されないことを保証）
        assertThrows(
            UnsupportedOperationException.class,
            () -> {
                recipe.getInputs()
                    .add(new ItemInput(Items.gold_ingot, 1));
            });
    }

    // ========================================
    // 比較のテスト (compareTo)
    // ========================================

    @Test
    @DisplayName("優先度が高いレシピが先に来る")
    public void test優先度による順序() {
        ModularRecipe lowPriority = basicBuilder.priority(0)
            .build();
        ModularRecipe highPriority = basicBuilder.registryName("high_priority")
            .priority(10)
            .build();

        // highPriority の方が優先度が高いので、compareTo は負の数を返す
        // （負の数 = highPriority が「小さい」= ソート時に「前」に来る）
        assertTrue(highPriority.compareTo(lowPriority) < 0);
        assertTrue(lowPriority.compareTo(highPriority) > 0);
    }

    @Test
    @DisplayName("同じ優先度なら入力タイプ数で比較")
    public void test入力タイプ数による順序() {
        // 1種類の入力タイプ（アイテムのみ）
        ModularRecipe oneType = basicBuilder.addInput(new ItemInput(Items.iron_ingot, 1))
            .build();

        // 2種類の入力タイプ（アイテム + エネルギー）
        ModularRecipe twoTypes = basicBuilder.registryName("two_types")
            .addInput(new ItemInput(Items.iron_ingot, 1))
            .addInput(new EnergyInput(1000))
            .build();

        // 入力タイプが多い方が先に来る
        assertTrue(twoTypes.compareTo(oneType) < 0);
    }

    // ========================================
    // ヒント：次に書くべきテスト
    // ========================================

    // TODO: processInputs() のテスト（モックPortが必要）
    // TODO: processOutputs() のテスト（モックPortが必要）
    // TODO: Visitor パターンのテスト → RecipeValidationVisitorTest.java で
}
