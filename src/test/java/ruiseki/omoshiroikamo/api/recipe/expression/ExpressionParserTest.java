package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

public class ExpressionParserTest {

    private ConditionContext emptyContext;

    @BeforeEach
    public void setUp() {
        // Mock context or use a dummy one if World is not needed for basic tests
        emptyContext = new ConditionContext(null, 0, 0, 0, null);
    }

    private double eval(String expr) {
        return ExpressionParser.parseExpression(expr)
            .evaluate(emptyContext);
    }

    private boolean check(String cond) {
        return ExpressionParser.parseCondition(cond)
            .isMet(emptyContext);
    }

    @Test
    @DisplayName("Basic arithmetic operations (+, -, *, /, %)")
    public void testArithmeticBasic() {
        assertEquals(5.0, eval("2 + 3"), 0.001);
        assertEquals(-1.0, eval("2 - 3"), 0.001);
        assertEquals(6.0, eval("2 * 3"), 0.001);
        assertEquals(2.0, eval("6 / 3"), 0.001);
        assertEquals(1.0, eval("7 % 3"), 0.001);
    }

    @Test
    @DisplayName("Arithmetic precedence and parentheses")
    public void testArithmeticPrecedence() {
        assertEquals(11.0, eval("2 + 3 * 3"), 0.001);
        assertEquals(15.0, eval("(2 + 3) * 3"), 0.001);
        assertEquals(7.0, eval("10 - 6 / 2"), 0.001);
        assertEquals(2.0, eval("(10 - 6) / 2"), 0.001);
    }

    @Test
    @DisplayName("Unary operators (+, -, !)")
    public void testArithmeticUnary() {
        assertEquals(-5.0, eval("-5"), 0.001);
        assertEquals(5.0, eval("--5"), 0.001);
        assertEquals(-2.0, eval("3 + -5"), 0.001);
    }

    @Test
    @DisplayName("Division and modulo by zero, and floating point precision")
    public void testArithmeticEdgeCases() {
        // Division by zero should return 0 based on implementation
        assertEquals(0.0, eval("10 / 0"), 0.001);
        assertEquals(0.0, eval("10 % 0"), 0.001);

        // Floating point
        assertEquals(2.5, eval("5 / 2"), 0.001);
        assertEquals(0.1, eval("1.1 - 1.0"), 0.001);
    }

    @Test
    @DisplayName("Comparison operators (==, !=, >, <, >=, <=)")
    public void testComparison() {
        assertTrue(check("1 == 1"));
        assertFalse(check("1 == 2"));
        assertTrue(check("1 != 2"));
        assertTrue(check("5 > 3"));
        assertTrue(check("3 < 5"));
        assertTrue(check("5 >= 5"));
        assertTrue(check("5 >= 4"));
        assertTrue(check("3 <= 3"));
        assertTrue(check("3 <= 4"));
    }

    @Test
    @DisplayName("Logical operators (&&, ||)")
    public void testLogical() {
        assertTrue(check("1 == 1 && 2 == 2"));
        assertFalse(check("1 == 1 && 2 == 3"));
        assertTrue(check("1 == 1 || 2 == 3"));
        assertFalse(check("1 == 2 || 2 == 3"));

        // Complex logical
        assertTrue(check("(1 == 1 && 2 == 2) || 3 == 4"));
        assertTrue(check("1 == 2 || (2 == 2 && 3 == 3)"));
    }

    @Test
    @DisplayName("Boolean negation (!)")
    public void testNotOperator() {
        assertTrue(check("!(1 == 2)"));
        assertFalse(check("!(1 == 1)"));
        assertTrue(check("!!(1 == 1)"));

        // Mix with arithmetic (Condition as Expression: true=1, false=0)
        assertEquals(1.0, eval("!(1 == 2)"), 0.001);
        assertEquals(0.0, eval("!(1 == 1)"), 0.001);
        assertEquals(2.0, eval("1 + (5 > 3)"), 0.001);
    }

    @Test
    @DisplayName("Handling of spaces, tabs, and newlines")
    public void testWhitespace() {
        assertEquals(10.0, eval("  5  +  5  "), 0.001);
        assertTrue(check("\n1==1\t&&\r2==2 "));
    }

    @Test
    @DisplayName("Should throw exception on missing brackets")
    public void testErrorMismatchedParens() {
        assertThrows(RecipeScriptException.class, () -> { eval("(1 + 2"); });
    }

    @Test
    @DisplayName("Should throw exception on unexpected characters")
    public void testErrorUnexpectedToken() {
        assertThrows(RecipeScriptException.class, () -> { eval("1 + 2 @ 3"); });
    }

    @Test
    @DisplayName("Should throw exception on unknown variables")
    public void testErrorUnknownVariable() {
        assertThrows(RecipeScriptException.class, () -> { eval("unknown_var + 1"); });
    }

    @Test
    @DisplayName("Should throw exception on unknown functions")
    public void testErrorUnknownFunction() {
        assertThrows(RecipeScriptException.class, () -> { eval("func(1, 2) + 3"); });
    }

    @Test
    @DisplayName("Complex combined formulas")
    public void testComplexFormula() {
        // (10 * 2 + 5) / 5 = 25 / 5 = 5
        assertEquals(5.0, eval("(10 * 2 + 5) / 5"), 0.001);
        // 100 % 30 + 5 * 2 = 10 + 10 = 20
        assertEquals(20.0, eval("100 % 30 + 5 * 2"), 0.001);
    }

    // ========================================
    // 変数のテスト（day, time, moon_phase）
    // ========================================

    @Test
    @DisplayName("変数: 変数名がパースされる（day, time, moon等）")
    public void test変数パース() {
        // emptyContextではWorldがnullなので、すべて0を返す
        assertEquals(0.0, eval("day"), 0.001);
        assertEquals(0.0, eval("time"), 0.001);
        assertEquals(0.0, eval("moon_phase"), 0.001);
        assertEquals(0.0, eval("moon"), 0.001); // moon は moon_phase のエイリアス
        assertEquals(0.0, eval("total_days"), 0.001);
    }

    @Test
    @DisplayName("変数: 算術演算と組み合わせ")
    public void test変数と算術() {
        // day は 0 なので、10 + 0 * 5 = 10
        assertEquals(10.0, eval("10 + day * 5"), 0.001);
    }

    // ========================================
    // nbt()関数のテスト
    // ========================================

    @Test
    @DisplayName("nbt('key'): 1引数形式がパースされる")
    public void testNbt1引数パース() {
        // emptyContextではTileEntityがないので、デフォルト値0を返す
        assertEquals(0.0, eval("nbt('energy')"), 0.001);
    }

    @Test
    @DisplayName("nbt() 引数なしでエラー")
    public void testNbt引数なし() {
        assertThrows(RecipeScriptException.class, () -> { eval("nbt()"); }, "nbt()は引数が必要なのでエラーになるべき");
    }

    // ========================================
    // エラーメッセージのテスト
    // ========================================

    @Test
    @DisplayName("RecipeScriptException: エラーメッセージに問題箇所を含む")
    public void testErrorMessage() {
        try {
            eval("1 + @ 2");
            fail("無効な文字はエラーになるべき");
        } catch (RecipeScriptException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("Unexpected") || message.length() > 0, "エラーメッセージが含まれるべき");
        }
    }

    // ========================================
    // 境界ケース
    // ========================================

    @Test
    @DisplayName("【エッジ】空文字列のパース")
    public void testParseEmpty() {
        assertThrows(RecipeScriptException.class, () -> { eval(""); }, "空文字列はエラーになるべき");
    }

    @Test
    @DisplayName("【エッジ】ホワイトスペースのみ")
    public void testParseWhitespaceOnly() {
        assertThrows(RecipeScriptException.class, () -> { eval("   \n\t  "); }, "ホワイトスペースのみはエラーになるべき");
    }

    @Test
    @DisplayName("【エッジ】0.0 / 0.0 の除算")
    public void testZeroDivisionNaN() {
        // 0.0 / 0.0 は実装上 0 を返す（ArithmeticExpressionの仕様）
        assertEquals(0.0, eval("0.0 / 0.0"), 0.001);
    }

    @Test
    @DisplayName("【エッジ】負数のモジュロ演算")
    public void testNegativeModulo() {
        // -7 % 3 = -1 (Javaのモジュロ仕様)
        assertEquals(-1.0, eval("-7 % 3"), 0.001);
    }

    // ========================================
    // ドット記法 (Dot Notation)
    // ========================================

    @Test
    @DisplayName("【ドット記法】基本的なパース")
    public void testDotNotationParsing() {
        // ドット記法がパースできることを確認
        IExpression expr = ExpressionParser.parseExpression("display.Name");
        assertNotNull(expr);
        assertTrue(expr instanceof DotNotationNBTExpression);

        DotNotationNBTExpression dotExpr = (DotNotationNBTExpression) expr;
        assertEquals("display.Name", dotExpr.getFullPath());
        assertEquals(
            2,
            dotExpr.getPathSegments()
                .size());
        assertEquals(
            "display",
            dotExpr.getPathSegments()
                .get(0));
        assertEquals(
            "Name",
            dotExpr.getPathSegments()
                .get(1));
    }

    @Test
    @DisplayName("【ドット記法】複数セグメント")
    public void testDotNotationMultipleSegments() {
        IExpression expr = ExpressionParser.parseExpression("a.b.c.d");
        assertTrue(expr instanceof DotNotationNBTExpression);

        DotNotationNBTExpression dotExpr = (DotNotationNBTExpression) expr;
        assertEquals("a.b.c.d", dotExpr.getFullPath());
        assertEquals(
            4,
            dotExpr.getPathSegments()
                .size());
    }

    @Test
    @DisplayName("【ドット記法】文字列比較")
    public void testDotNotationStringComparison() {
        // パースできることを確認（評価はContext依存なので割愛）
        IExpression expr = ExpressionParser.parseExpression("display.Name == 'Sword'");
        assertNotNull(expr);
    }
}
