package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ComparisonCondition のユニットテスト
 *
 * ============================================
 * 式の比較条件テスト
 * ============================================
 *
 * ComparisonCondition は、2つの式（IExpression）を比較して
 * 条件を判定します。
 *
 * サポートされる演算子:
 * - >, >=, <, <=: 通常の比較
 * - ==: 浮動小数点の許容誤差 0.0001 以内で等しい
 * - !=: 浮動小数点の許容誤差 0.0001 以上で異なる
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("ComparisonCondition（式の比較）のテスト")
public class ComparisonConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: > (greater than)
    // ========================================

    @Test
    @DisplayName(">: 左が右より大きい場合にtrue")
    public void test大なり正常() {
        IExpression left = new ConstantExpression(10);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "10 > 5 はtrueになるべき");
    }

    @Test
    @DisplayName(">: 左が右と等しい場合にfalse")
    public void test大なり等しい() {
        IExpression left = new ConstantExpression(5);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5 > 5 はfalseになるべき");
    }

    @Test
    @DisplayName(">: 左が右より小さい場合にfalse")
    public void test大なり小さい() {
        IExpression left = new ConstantExpression(3);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "3 > 5 はfalseになるべき");
    }

    // ========================================
    // 正常系: >= (greater or equal)
    // ========================================

    @Test
    @DisplayName(">=: 左が右より大きい場合にtrue")
    public void test以上大きい() {
        IExpression left = new ConstantExpression(10);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "10 >= 5 はtrueになるべき");
    }

    @Test
    @DisplayName(">=: 左が右と等しい場合にtrue")
    public void test以上等しい() {
        IExpression left = new ConstantExpression(5);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "5 >= 5 はtrueになるべき");
    }

    @Test
    @DisplayName(">=: 左が右より小さい場合にfalse")
    public void test以上小さい() {
        IExpression left = new ConstantExpression(3);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "3 >= 5 はfalseになるべき");
    }

    // ========================================
    // 正常系: < (less than)
    // ========================================

    @Test
    @DisplayName("<: 左が右より小さい場合にtrue")
    public void test小なり正常() {
        IExpression left = new ConstantExpression(3);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, "<");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "3 < 5 はtrueになるべき");
    }

    @Test
    @DisplayName("<: 左が右と等しい場合にfalse")
    public void test小なり等しい() {
        IExpression left = new ConstantExpression(5);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, "<");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5 < 5 はfalseになるべき");
    }

    // ========================================
    // 正常系: <= (less or equal)
    // ========================================

    @Test
    @DisplayName("<=: 左が右より小さい場合にtrue")
    public void test以下小さい() {
        IExpression left = new ConstantExpression(3);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, "<=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "3 <= 5 はtrueになるべき");
    }

    @Test
    @DisplayName("<=: 左が右と等しい場合にtrue")
    public void test以下等しい() {
        IExpression left = new ConstantExpression(5);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, "<=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "5 <= 5 はtrueになるべき");
    }

    // ========================================
    // 正常系: == (equals with tolerance)
    // ========================================

    @Test
    @DisplayName("==: 完全に等しい場合にtrue")
    public void test等号完全一致() {
        IExpression left = new ConstantExpression(5.0);
        IExpression right = new ConstantExpression(5.0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "==");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "5.0 == 5.0 はtrueになるべき");
    }

    @Test
    @DisplayName("==: 浮動小数点許容誤差内（0.00005）でtrue")
    public void test等号許容誤差内() {
        IExpression left = new ConstantExpression(5.00001);
        IExpression right = new ConstantExpression(5.00002);
        ComparisonCondition condition = new ComparisonCondition(left, right, "==");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "5.00001 == 5.00002 は許容誤差0.0001内でtrueになるべき");
    }

    @Test
    @DisplayName("==: 浮動小数点許容誤差外（0.0002）でfalse")
    public void test等号許容誤差外() {
        IExpression left = new ConstantExpression(5.0002);
        IExpression right = new ConstantExpression(5.0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "==");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5.0002 == 5.0 は許容誤差0.0001外（差0.0002）でfalseになるべき");
    }

    @Test
    @DisplayName("==: 明らかに異なる値でfalse")
    public void test等号異なる() {
        IExpression left = new ConstantExpression(5.1);
        IExpression right = new ConstantExpression(5.0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "==");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5.1 == 5.0 はfalseになるべき");
    }

    // ========================================
    // 正常系: != (not equals with tolerance)
    // ========================================

    @Test
    @DisplayName("!=: 明らかに異なる値でtrue")
    public void test不等号異なる() {
        IExpression left = new ConstantExpression(5.1);
        IExpression right = new ConstantExpression(5.0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "!=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "5.1 != 5.0 はtrueになるべき");
    }

    @Test
    @DisplayName("!=: 浮動小数点許容誤差内（0.00005）でfalse")
    public void test不等号許容誤差内() {
        IExpression left = new ConstantExpression(5.00001);
        IExpression right = new ConstantExpression(5.00002);
        ComparisonCondition condition = new ComparisonCondition(left, right, "!=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5.00001 != 5.00002 は許容誤差0.0001内でfalseになるべき");
    }

    @Test
    @DisplayName("!=: 完全に等しい場合にfalse")
    public void test不等号完全一致() {
        IExpression left = new ConstantExpression(5.0);
        IExpression right = new ConstantExpression(5.0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "!=");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "5.0 != 5.0 はfalseになるべき");
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】両方ゼロの比較")
    public void test両方ゼロ() {
        IExpression left = new ConstantExpression(0);
        IExpression right = new ConstantExpression(0);
        ComparisonCondition condition = new ComparisonCondition(left, right, "==");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "0 == 0 はtrueになるべき");
    }

    @Test
    @DisplayName("【エッジ】負数の比較")
    public void test負数() {
        IExpression left = new ConstantExpression(-10);
        IExpression right = new ConstantExpression(-5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "-10 > -5 はfalseになるべき");
    }

    @Test
    @DisplayName("【エッジ】未知の演算子でfalse")
    public void test未知の演算子() {
        IExpression left = new ConstantExpression(5);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, "unknown");

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(condition.isMet(context), "未知の演算子はfalseになるべき");
    }

    // ========================================
    // JSON読み込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: 比較条件の読み込み")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.add("left", new JsonPrimitive(10));
        json.add("right", new JsonPrimitive(5));
        json.addProperty("operator", ">");

        ICondition condition = ComparisonCondition.fromJson(json);

        assertNotNull(condition);
        assertTrue(condition instanceof ComparisonCondition);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(condition.isMet(context), "JSONから読み込んだ比較条件が正しく動作するべき");
    }

    // ========================================
    // getDescription() のテスト
    // ========================================

    @Test
    @DisplayName("getDescription: 説明文が取得できる")
    public void testGetDescription() {
        IExpression left = new ConstantExpression(10);
        IExpression right = new ConstantExpression(5);
        ComparisonCondition condition = new ComparisonCondition(left, right, ">");

        String description = condition.getDescription();

        assertNotNull(description);
        // left.toString() + " " + operator + " " + right.toString() の形式
    }
}
