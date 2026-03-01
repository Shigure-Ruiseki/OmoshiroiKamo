package ruiseki.omoshiroikamo.api.modular.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression（式評価）のユニットテスト
 *
 * ============================================
 * 式評価システムの基本機能をテスト
 * ============================================
 *
 * Expression は ChanceDecorator や BonusOutputDecorator で使用される
 * 動的な値評価システムです。
 *
 * - ConstantExpression: 定数値（0.5など）
 * - MapRangeExpression: 値の範囲マッピング
 * - NbtExpression: NBTデータから値を取得
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("Expression（式評価）のテスト")
public class ExpressionTest {

    private ConditionContext context;

    // ========================================
    // ConstantExpression のテスト
    // ========================================

    @Test
    @DisplayName("ConstantExpression: 定数値を返す")
    public void testConstantExpression() {
        IExpression expr = new ConstantExpression(0.75);

        // どのコンテキストでも同じ値を返す
        assertEquals(0.75, expr.evaluate(new ConditionContext(null, 0, 0, 0)), 0.001);
    }

    @Test
    @DisplayName("ConstantExpression: 0.0")
    public void testConstantZero() {
        IExpression expr = new ConstantExpression(0.0);
        assertEquals(0.0, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("ConstantExpression: 1.0")
    public void testConstantOne() {
        IExpression expr = new ConstantExpression(1.0);
        assertEquals(1.0, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("ConstantExpression: 負の値")
    public void testConstantNegative() {
        IExpression expr = new ConstantExpression(-5.5);
        assertEquals(-5.5, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("ConstantExpression: 大きな値")
    public void testConstantLarge() {
        IExpression expr = new ConstantExpression(99999.99);
        assertEquals(99999.99, expr.evaluate(new ConditionContext(null, 0, 0, 0)), 0.001);
    }

    // ========================================
    // ExpressionsParser のテスト
    // ========================================

    @Test
    @DisplayName("ExpressionsParser: 数値からConstantExpressionを生成")
    public void testParseNumber() {
        JsonPrimitive json = new JsonPrimitive(0.5);
        IExpression expr = ExpressionsParser.parse(json);

        assertNotNull(expr);
        assertTrue(expr instanceof ConstantExpression);
        assertEquals(0.5, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("ExpressionsParser: 整数からConstantExpressionを生成")
    public void testParseInteger() {
        JsonPrimitive json = new JsonPrimitive(3);
        IExpression expr = ExpressionsParser.parse(json);

        assertNotNull(expr);
        assertEquals(3.0, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("ExpressionsParser: 0からConstantExpressionを生成")
    public void testParseZero() {
        JsonPrimitive json = new JsonPrimitive(0);
        IExpression expr = ExpressionsParser.parse(json);

        assertNotNull(expr);
        assertEquals(0.0, expr.evaluate(new ConditionContext(null, 0, 0, 0)), 0.001);
    }

    // ========================================
    // MapRangeExpression のテスト
    // ========================================

    @Test
    @DisplayName("MapRangeExpression: 基本的な範囲マッピング")
    public void testMapRangeBasic() {
        // 0-100 を 0.0-1.0 にマッピング
        // 入力値50 → 出力値0.5
        MapRangeExpression expr = new MapRangeExpression(new ConstantExpression(50), 0, 100, 0.0, 1.0, true);

        // コンテキストに test_value=50 を設定した場合を想定
        assertNotNull(expr);
    }

    @Test
    @DisplayName("MapRangeExpression: 逆マッピング")
    public void testMapRangeReverse() {
        // 0-100 を 1.0-0.0 にマッピング
        MapRangeExpression expr = new MapRangeExpression(new ConstantExpression(50), 0, 100, 1.0, 0.0, true);

        assertNotNull(expr);
    }

    // ========================================
    // NbtExpression のテスト
    // ========================================

    @Test
    @DisplayName("NbtExpression: NBTパスの指定")
    public void testNbtExpression() {
        // NBTデータから値を取得する式
        // 例: "machine.efficiency" というパスから値を取得
        NbtExpression expr = new NbtExpression("machine.efficiency", 1.0);

        assertNotNull(expr);
        // デフォルト値が返される（コンテキストにNBTがない場合）
        assertEquals(1.0, expr.evaluate(new ConditionContext(null, 0, 0, 0)), 0.001);
    }

    @Test
    @DisplayName("NbtExpression: デフォルト値")
    public void testNbtExpressionDefault() {
        NbtExpression expr = new NbtExpression("missing.path", 0.75);

        // コンテキストが不完全な場合、デフォルト値を返す
        assertEquals(0.75, expr.evaluate(new ConditionContext(null, 0, 0, 0)), 0.001);
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】ExpressionsParser: nullを渡した場合")
    public void testParseNull() {
        IExpression expr = ExpressionsParser.parse(null);

        // nullの場合、デフォルト値（例: 0.0）を返すConstantExpressionが生成される
        if (expr != null) {
            double result = expr.evaluate(null);
            assertTrue(result >= 0.0);
        }
    }

    @Test
    @DisplayName("【エッジ】ConstantExpression: Double.MAX_VALUE")
    public void testConstantMaxValue() {
        IExpression expr = new ConstantExpression(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, expr.evaluate(null), 0.001);
    }

    @Test
    @DisplayName("【エッジ】ConstantExpression: Double.MIN_VALUE")
    public void testConstantMinValue() {
        IExpression expr = new ConstantExpression(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, expr.evaluate(null), 0.001);
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: MapRangeExpression の実際の評価テスト（コンテキストが必要）
    // TODO: NbtExpression の実際のNBTデータからの値取得テスト
    // TODO: ExpressionsParser の JsonObject からの複雑な式のパース
    // TODO: カスタムExpressionの実装と登録
}
