package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * Decorator組み合わせテスト
 *
 * ============================================
 * 複雑なデコレータ組み合わせの完全検証
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - デコレータの組み合わせは予期しないバグの温床
 * - 2重、3重のデコレータで正しく動作することを検証
 * - プロパティの委譲チェーンが壊れていないか確認
 *
 * カバーする機能:
 * - ChanceDecorator + BonusOutputDecorator
 * - 3重デコレータ（最重要）
 * - プロパティ委譲の確認
 * - 条件チェックの伝播
 *
 * ============================================
 */
@DisplayName("Decorator組み合わせテスト（★最重要★）")
public class DecoratorCombinationTest {

    private IModularRecipe baseRecipe;
    private ConditionContext context;

    @BeforeEach
    public void setUp() {
        baseRecipe = ModularRecipe.builder()
            .registryName("base_recipe")
            .recipeGroup("test_group")
            .duration(300)
            .priority(5)
            .build();

        context = new ConditionContext(null, 0, 0, 0);
    }

    // ========================================
    // 2重デコレータ
    // ========================================

    @Test
    @DisplayName("【2重】Chance + Bonus の組み合わせ")
    public void testChancePlusBonus() {
        // ボーナス出力
        List<IRecipeOutput> bonusOutputs = new ArrayList<>();
        bonusOutputs.add(new ItemOutput(new ItemStack(Items.diamond, 1)));

        // 1層目: BonusOutputDecorator (100%ボーナス)
        IModularRecipe withBonus = new BonusOutputDecorator(
            baseRecipe,
            new ConstantExpression(1.0),
            bonusOutputs,
            null);

        // 2層目: ChanceRecipeDecorator (50%確率)
        IModularRecipe decorated = new ChanceRecipeDecorator(withBonus, new ConstantExpression(0.5));

        // プロパティが正しく委譲される
        assertEquals("base_recipe", decorated.getRegistryName());
        assertEquals("test_group", decorated.getRecipeGroup());
        assertEquals(300, decorated.getDuration());
        assertEquals(5, decorated.getPriority());

        // 統計的テスト: 50%の確率で条件を満たす
        int successCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (decorated.isConditionMet(context)) {
                successCount++;
            }
        }

        assertTrue(successCount >= 400 && successCount <= 600, "Expected 400-600, got " + successCount);
    }

    @Test
    @DisplayName("【2重】Bonus + Chance の組み合わせ（逆順）")
    public void testBonusPlusChance() {
        // 1層目: ChanceRecipeDecorator
        IModularRecipe withChance = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.5));

        // 2層目: BonusOutputDecorator
        List<IRecipeOutput> bonusOutputs = new ArrayList<>();
        bonusOutputs.add(new ItemOutput(new ItemStack(Items.gold_ingot, 2)));

        IModularRecipe decorated = new BonusOutputDecorator(
            withChance,
            new ConstantExpression(1.0),
            bonusOutputs,
            null);

        // プロパティが正しく委譲される
        assertEquals("base_recipe", decorated.getRegistryName());
        assertEquals(300, decorated.getDuration());
    }

    // ========================================
    // 3重デコレータ（★最重要★）
    // ========================================

    @Test
    @DisplayName("【★3重★】Chance + Bonus + Chance の組み合わせ")
    public void test3重Decorator() {
        // 1層目: ChanceRecipeDecorator (50%)
        IModularRecipe layer1 = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.5));

        // 2層目: BonusOutputDecorator (100%ボーナス)
        List<IRecipeOutput> bonusOutputs = new ArrayList<>();
        bonusOutputs.add(new ItemOutput(new ItemStack(Items.diamond, 1)));

        IModularRecipe layer2 = new BonusOutputDecorator(layer1, new ConstantExpression(1.0), bonusOutputs, null);

        // 3層目: ChanceRecipeDecorator (50%)
        IModularRecipe layer3 = new ChanceRecipeDecorator(layer2, new ConstantExpression(0.5));

        // プロパティが3層を超えて正しく委譲される
        assertEquals("base_recipe", layer3.getRegistryName());
        assertEquals("test_group", layer3.getRecipeGroup());
        assertEquals(300, layer3.getDuration());
        assertEquals(5, layer3.getPriority());

        // 統計的テスト: 0.5 * 0.5 = 0.25 (25%)
        int successCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (layer3.isConditionMet(context)) {
                successCount++;
            }
        }

        assertTrue(successCount >= 150 && successCount <= 350, "Expected 150-350 (25%), got " + successCount);
    }

    @Test
    @DisplayName("【★3重★】Bonus + Bonus + Chance の組み合わせ")
    public void test3重_ボーナス2回() {
        // 1層目: BonusOutputDecorator
        List<IRecipeOutput> bonus1 = new ArrayList<>();
        bonus1.add(new ItemOutput(new ItemStack(Items.diamond, 1)));

        IModularRecipe layer1 = new BonusOutputDecorator(baseRecipe, new ConstantExpression(1.0), bonus1, null);

        // 2層目: BonusOutputDecorator
        List<IRecipeOutput> bonus2 = new ArrayList<>();
        bonus2.add(new ItemOutput(new ItemStack(Items.gold_ingot, 2)));

        IModularRecipe layer2 = new BonusOutputDecorator(layer1, new ConstantExpression(1.0), bonus2, null);

        // 3層目: ChanceRecipeDecorator
        IModularRecipe layer3 = new ChanceRecipeDecorator(layer2, new ConstantExpression(0.5));

        // プロパティが正しく委譲される
        assertEquals("base_recipe", layer3.getRegistryName());
        assertEquals(300, layer3.getDuration());
    }

    // ========================================
    // 4重以上のデコレータ
    // ========================================

    @Test
    @DisplayName("【極端】4重デコレータ")
    public void test4重Decorator() {
        IModularRecipe layer1 = new ChanceRecipeDecorator(baseRecipe, new ConstantExpression(0.8));

        List<IRecipeOutput> bonus = new ArrayList<>();
        bonus.add(new ItemOutput(new ItemStack(Items.diamond, 1)));
        IModularRecipe layer2 = new BonusOutputDecorator(layer1, new ConstantExpression(1.0), bonus, null);

        IModularRecipe layer3 = new ChanceRecipeDecorator(layer2, new ConstantExpression(0.7));
        IModularRecipe layer4 = new ChanceRecipeDecorator(layer3, new ConstantExpression(0.9));

        // プロパティが4層を超えて正しく委譲される
        assertEquals("base_recipe", layer4.getRegistryName());
        assertEquals(300, layer4.getDuration());

        // 統計的テスト: 0.8 * 0.7 * 0.9 = 0.504 (約50%)
        int successCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (layer4.isConditionMet(context)) {
                successCount++;
            }
        }

        assertTrue(successCount >= 400 && successCount <= 600, "Expected around 500 (50%), got " + successCount);
    }

    @Test
    @DisplayName("【極端】5重デコレータ")
    public void test5重Decorator() {
        IModularRecipe current = baseRecipe;

        // 5回Chanceデコレータを重ねる (各50%)
        for (int i = 0; i < 5; i++) {
            current = new ChanceRecipeDecorator(current, new ConstantExpression(0.5));
        }

        // プロパティが5層を超えて正しく委譲される
        assertEquals("base_recipe", current.getRegistryName());

        // 統計的テスト: 0.5^5 = 0.03125 (約3%)
        int successCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (current.isConditionMet(context)) {
                successCount++;
            }
        }

        assertTrue(successCount >= 10 && successCount <= 60, "Expected around 31 (3%), got " + successCount);
    }

    // ========================================
    // 入力・出力の確認
    // ========================================

    @Test
    @DisplayName("デコレータを通しても入力・出力が正しく取得できる")
    public void testデコレータ経由の入出力() {
        // 入力・出力を持つレシピ
        IModularRecipe recipeWithIO = ModularRecipe.builder()
            .registryName("with_io")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput(Items.iron_ingot, 1))
            .addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 1)))
            .build();

        // デコレータを適用
        IModularRecipe decorated = new ChanceRecipeDecorator(recipeWithIO, new ConstantExpression(1.0));

        // 入力・出力が正しく取得できる
        assertEquals(
            1,
            decorated.getInputs()
                .size());
        assertEquals(
            1,
            decorated.getOutputs()
                .size());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: 実際のレシピ実行テスト（モックポートが必要）
    // TODO: JSON経由でのデコレータ適用テスト
    // TODO: 他のデコレータ（まだ実装されていない）との組み合わせ
}
