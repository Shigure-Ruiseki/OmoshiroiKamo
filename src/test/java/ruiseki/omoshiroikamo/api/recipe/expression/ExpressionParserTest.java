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
}
