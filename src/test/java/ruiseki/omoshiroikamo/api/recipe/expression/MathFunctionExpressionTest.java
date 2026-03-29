package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * MathFunctionExpression および ExpressionParser での関数パースを検証するテスト
 */
@DisplayName("MathFunctionExpression（数学関数）のテスト")
public class MathFunctionExpressionTest {

    @Test
    @DisplayName("基本的な一変数関数: abs, sqrt, floor, ceil, round")
    public void testBasicUnaryFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertEquals(
            5.0,
            ExpressionParser.parseExpression("abs(-5)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            4.0,
            ExpressionParser.parseExpression("sqrt(16)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            2.0,
            ExpressionParser.parseExpression("floor(2.9)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            3.0,
            ExpressionParser.parseExpression("ceil(2.1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            3.0,
            ExpressionParser.parseExpression("round(2.5)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("三角関数: sin, cos, tan (ラジアン)")
    public void testTrigonometricFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertEquals(
            1.0,
            ExpressionParser.parseExpression("sin(pi / 2)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("sin(0)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            1.0,
            ExpressionParser.parseExpression("cos(0)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("cos(pi / 2)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            1.0,
            ExpressionParser.parseExpression("tan(pi / 4)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("度数法/ラジアン変換: rad, deg")
    public void testAngleConversion() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            Math.PI / 2,
            ExpressionParser.parseExpression("rad(90)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            90.0,
            ExpressionParser.parseExpression("deg(pi / 2)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("数学定数: pi, e")
    public void testConstants() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            Math.PI,
            ExpressionParser.parseExpression("pi")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            Math.E,
            ExpressionParser.parseExpression("e")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("多変数関数: pow, min, max, atan2")
    public void testBinaryFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertEquals(
            8.0,
            ExpressionParser.parseExpression("pow(2, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            10.0,
            ExpressionParser.parseExpression("min(10, 20)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            20.0,
            ExpressionParser.parseExpression("max(10, 20)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            Math.PI / 4,
            ExpressionParser.parseExpression("atan2(1, 1)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("逆三角関数: asin, acos, atan")
    public void testInverseTrigonometricFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            Math.PI / 2,
            ExpressionParser.parseExpression("asin(1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("acos(1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            Math.PI / 4,
            ExpressionParser.parseExpression("atan(1)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("指数・対数関数: exp, log, log10")
    public void testLogExpFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            Math.E,
            ExpressionParser.parseExpression("exp(1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            1.0,
            ExpressionParser.parseExpression("log(e)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            2.0,
            ExpressionParser.parseExpression("log10(100)")
                .evaluate(context)
                .asDouble(),
            0.001);
        // log(x, base) のテスト
        assertEquals(
            3.0,
            ExpressionParser.parseExpression("log(8, 2)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            2.0,
            ExpressionParser.parseExpression("log(100, 10)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("組合せ・順列: nCr, nPr, fact (エイリアス含む)")
    public void testCombinatorics() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            120.0,
            ExpressionParser.parseExpression("fact(5)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            60.0,
            ExpressionParser.parseExpression("npr(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            10.0,
            ExpressionParser.parseExpression("ncr(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);

        // エイリアスのテスト
        assertEquals(
            10.0,
            ExpressionParser.parseExpression("combi(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            10.0,
            ExpressionParser.parseExpression("combination(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            60.0,
            ExpressionParser.parseExpression("perm(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            60.0,
            ExpressionParser.parseExpression("permu(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            60.0,
            ExpressionParser.parseExpression("permutation(5, 3)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("特殊関数: clamp, random, sign")
    public void testSpecialFunctions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            100.0,
            ExpressionParser.parseExpression("clamp(150, 0, 100)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("clamp(-50, 0, 100)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            50.0,
            ExpressionParser.parseExpression("clamp(50, 0, 100)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            1.0,
            ExpressionParser.parseExpression("sign(10)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            -1.0,
            ExpressionParser.parseExpression("sign(-10)")
                .evaluate(context)
                .asDouble(),
            0.001);

        double rnd = ExpressionParser.parseExpression("random()")
            .evaluate(context)
            .asDouble();
        assertTrue(rnd >= 0.0 && rnd < 1.0);

        // クリップの逆転 (min > max) のテスト
        assertEquals(
            50.0,
            ExpressionParser.parseExpression("clamp(50, 100, 0)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("clamp(-10, 100, 0)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("安全性ガード: sqrt(-1), asin(2), log(0), log(10, 1)")
    public void testSafetyGuards() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("sqrt(-1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("asin(2.0)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("acos(-1.5)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("log(0)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("log(-5)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("log(10, 1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("log(10, -2)")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("決定論的ランダムの検証")
    public void testDeterministicRandom() {
        ConditionContext context1 = new ConditionContext(null, 10, 20, 30, null, 12345L);
        ConditionContext context1_same = new ConditionContext(null, 10, 20, 30, null, 12345L);
        ConditionContext context2 = new ConditionContext(null, 10, 20, 30, null, 54321L);

        double val1 = ExpressionParser.parseExpression("random()")
            .evaluate(context1)
            .asDouble();
        double val1_same = ExpressionParser.parseExpression("random()")
            .evaluate(context1_same)
            .asDouble();
        double val2 = ExpressionParser.parseExpression("random()")
            .evaluate(context2)
            .asDouble();

        assertEquals(val1, val1_same, "同じシードなら同じ値が返るべき");
        assertNotEquals(val1, val2, "違うシードなら違う値が返るべき");

        // chance() の検証
        double chance1 = ExpressionParser.parseExpression("chance(0.5)")
            .evaluate(context1)
            .asDouble();
        double chance1_same = ExpressionParser.parseExpression("chance(0.5)")
            .evaluate(context1_same)
            .asDouble();
        assertEquals(chance1, chance1_same, "同じシードなら chance() も同じ結果になるべき");
    }

    @Test
    @DisplayName("複合式での利用")
    public void testComplexExpressions() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertEquals(
            15.0,
            ExpressionParser.parseExpression("10 + sqrt(25)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            2.0,
            ExpressionParser.parseExpression("max(1, 2) * min(5, 1)")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            0.5,
            ExpressionParser.parseExpression("sin(rad(30))")
                .evaluate(context)
                .asDouble(),
            0.001);
        assertEquals(
            15.0,
            ExpressionParser.parseExpression("ncr(5, 2) + fact(3) - 1")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("nbt() との見分け")
    public void testFunctionDistinction() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);
        // nbt() は現状 World/TE がないとデフォルト 0 を返す
        assertEquals(
            0.0,
            ExpressionParser.parseExpression("nbt('any')")
                .evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("引数エラーの検証")
    public void testArgumentErrors() {
        assertThrows(RecipeScriptException.class, () -> { ExpressionParser.parseExpression("sin()"); });
        assertThrows(RecipeScriptException.class, () -> { ExpressionParser.parseExpression("pow(1)"); });
        assertThrows(RecipeScriptException.class, () -> { ExpressionParser.parseExpression("npr(1)"); });
        assertThrows(RecipeScriptException.class, () -> { ExpressionParser.parseExpression("log()"); });
        assertThrows(RecipeScriptException.class, () -> { ExpressionParser.parseExpression("nbt()"); });
    }
}
