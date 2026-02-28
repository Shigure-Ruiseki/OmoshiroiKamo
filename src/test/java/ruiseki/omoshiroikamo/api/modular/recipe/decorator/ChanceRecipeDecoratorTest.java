package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.IExpression;

/**
 * ChanceRecipeDecorator のユニットテスト
 *
 * ============================================
 * Decorator パターンとは？
 * ============================================
 *
 * 既存のオブジェクトに、新しい機能を「後付け」するパターン
 *
 * 例：
 * 基本レシピ: 100%成功
 * ↓ ChanceDecoratorで装飾
 * 確率レシピ: 50%成功
 *
 * メリット：
 * - 元のレシピクラスを変更せずに機能追加
 * - デコレータを重ねて複雑な挙動も実現可能
 *
 * ============================================
 */
@DisplayName("ChanceRecipeDecorator のテスト")
public class ChanceRecipeDecoratorTest {

    private IModularRecipe baseRecipe;
    private ConditionContext context;

    @BeforeEach
    public void setUp() {
        // 基本レシピ（常に条件を満たす）
        baseRecipe = ModularRecipe.builder()
            .registryName("base_recipe")
            .recipeGroup("test")
            .duration(100)
            .build();

        // テスト用のコンテキスト
        context = new ConditionContext(null, 0, 0, 0);
    }

    // ========================================
    // 基本的なテスト
    // ========================================

    @Test
    @DisplayName("100%の確率なら常に条件を満たす")
    public void test100パーセントなら常にtrue() {
        // 100% (1.0) の確率デコレータ
        IExpression alwaysTrue = new ConstantExpression(1.0);
        IModularRecipe decoratedRecipe = new ChanceRecipeDecorator(baseRecipe, alwaysTrue);

        // 何度呼んでもtrueになるはず
        for (int i = 0; i < 100; i++) {
            assertTrue(decoratedRecipe.isConditionMet(context));
        }
    }

    @Test
    @DisplayName("0%の確率なら常に条件を満たさない")
    public void test0パーセントなら常にfalse() {
        // 0% (0.0) の確率デコレータ
        IExpression alwaysFalse = new ConstantExpression(0.0);
        IModularRecipe decoratedRecipe = new ChanceRecipeDecorator(baseRecipe, alwaysFalse);

        // 何度呼んでもfalseになるはず
        for (int i = 0; i < 100; i++) {
            assertFalse(decoratedRecipe.isConditionMet(context));
        }
    }

    @Test
    @DisplayName("50%の確率ならおおよそ半分がtrueになる")
    public void test50パーセントの確率分布() {
        // 50% (0.5) の確率デコレータ
        IExpression halfChance = new ConstantExpression(0.5);
        IModularRecipe decoratedRecipe = new ChanceRecipeDecorator(baseRecipe, halfChance);

        // 1000回試行
        int successCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            if (decoratedRecipe.isConditionMet(context)) {
                successCount++;
            }
        }

        // 統計的に、1000回中400～600回くらいがtrueになるはず
        // （完全に500回ではないのは乱数の性質上）
        assertTrue(
            successCount >= 400 && successCount <= 600,
            "Expected around 500 successes, but got " + successCount);
    }

    // ========================================
    // Decorator の委譲テスト
    // ========================================

    @Test
    @DisplayName("デコレータは元のレシピのregistryNameを返す")
    public void testデコレータの委譲() {
        IExpression expression = new ConstantExpression(1.0);
        IModularRecipe decoratedRecipe = new ChanceRecipeDecorator(baseRecipe, expression);

        // デコレータを通しても、元のレシピの情報が取得できる
        assertEquals("base_recipe", decoratedRecipe.getRegistryName());
        assertEquals("test", decoratedRecipe.getRecipeGroup());
        assertEquals(100, decoratedRecipe.getDuration());
    }

    // ========================================
    // デコレータの重ね掛けテスト
    // ========================================

    @Test
    @DisplayName("デコレータを2重に重ねられる")
    public void testデコレータの重ね掛け() {
        // 50%デコレータ
        IExpression halfChance = new ConstantExpression(0.5);
        IModularRecipe firstDecorator = new ChanceRecipeDecorator(baseRecipe, halfChance);

        // さらに50%デコレータを重ねる
        // 結果: 0.5 * 0.5 = 0.25 (25%)
        IModularRecipe secondDecorator = new ChanceRecipeDecorator(firstDecorator, halfChance);

        // 1000回試行
        int successCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            if (secondDecorator.isConditionMet(context)) {
                successCount++;
            }
        }

        // 統計的に、1000回中150～350回くらいがtrueになるはず（25%）
        assertTrue(
            successCount >= 150 && successCount <= 350,
            "Expected around 250 successes (25%), but got " + successCount);
    }

    // ========================================
    // Expression のテスト
    // ========================================

    @Test
    @DisplayName("getChanceExpression()で設定した式を取得できる")
    public void testGetChanceExpression() {
        IExpression expression = new ConstantExpression(0.75);
        ChanceRecipeDecorator decorator = new ChanceRecipeDecorator(baseRecipe, expression);

        // 設定した式が取得できる
        IExpression retrieved = decorator.getChanceExpression();
        assertNotNull(retrieved);
        assertEquals(0.75, retrieved.evaluate(context));
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: 動的なExpression（NBT依存など）のテスト
    // TODO: BonusOutputDecoratorとの組み合わせテスト
    // TODO: JSON読み込みテスト（DecoratorParser経由）
}
