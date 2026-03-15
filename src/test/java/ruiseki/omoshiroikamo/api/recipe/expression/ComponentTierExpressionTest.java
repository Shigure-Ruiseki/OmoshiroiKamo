package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.ITieredMachine;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

public class ComponentTierExpressionTest {

    @BeforeAll
    public static void setup() {
        RegistryMocker.mockAll();
    }

    // Mock ITieredMachine implementation for testing
    private static class MockTieredMachine implements IRecipeContext, ITieredMachine {

        private final int glassTier;
        private final int casingTier;

        public MockTieredMachine(int glassTier, int casingTier) {
            this.glassTier = glassTier;
            this.casingTier = casingTier;
        }

        @Override
        public int getComponentTier(String componentName) {
            if ("glass".equals(componentName)) {
                return glassTier;
            }
            if ("casing".equals(componentName)) {
                return casingTier;
            }
            // Unknown components return 0 (default tier)
            return 0;
        }

        // IRecipeContext methods - not needed for this test, returning defaults
        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public ChunkCoordinates getControllerPos() {
            return null;
        }

        @Override
        public IStructureEntry getCurrentStructure() {
            return null;
        }

        @Override
        public ForgeDirection getFacing() {
            return null;
        }

        @Override
        public List<ChunkCoordinates> getSymbolPositions(char symbol) {
            return Collections.emptyList();
        }

        @Override
        public ConditionContext getConditionContext() {
            return null;
        }
    }

    @Test
    public void testParseFromString_Glass() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass");

        assertNotNull(expr);
        assertInstanceOf(ComponentTierExpression.class, expr);
        assertEquals("glass", ((ComponentTierExpression) expr).getComponentName());
    }

    @Test
    public void testParseFromString_Casing() {
        IExpression expr = ExpressionParser.parseExpression("tier.casing");

        assertNotNull(expr);
        assertInstanceOf(ComponentTierExpression.class, expr);
        assertEquals("casing", ((ComponentTierExpression) expr).getComponentName());
    }

    @Test
    public void testEvaluate_ValidContext() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");
        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);

        assertEquals(3.0, result, 0.001);
    }

    @Test
    public void testEvaluate_DifferentComponent() {
        ComponentTierExpression expr = new ComponentTierExpression("casing");
        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);

        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testEvaluate_UnknownComponent() {
        ComponentTierExpression expr = new ComponentTierExpression("unknown");
        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);

        assertEquals(0.0, result, 0.001); // Returns 0 for unknown components
    }

    @Test
    public void testEvaluate_NullContext() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");

        double result = expr.evaluate(null);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testEvaluate_NullRecipeContext() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");
        ConditionContext context = new ConditionContext(null, 0, 0, 0, null);

        double result = expr.evaluate(context);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testEvaluate_NonTieredMachine() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");
        // Create a context that is NOT an ITieredMachine
        IRecipeContext nonTiered = new IRecipeContext() {

            @Override
            public net.minecraft.world.World getWorld() {
                return null;
            }

            @Override
            public net.minecraft.util.ChunkCoordinates getControllerPos() {
                return null;
            }

            @Override
            public ruiseki.omoshiroikamo.api.structure.core.IStructureEntry getCurrentStructure() {
                return null;
            }

            @Override
            public net.minecraftforge.common.util.ForgeDirection getFacing() {
                return null;
            }

            @Override
            public java.util.List<net.minecraft.util.ChunkCoordinates> getSymbolPositions(char symbol) {
                return java.util.Collections.emptyList();
            }

            @Override
            public ConditionContext getConditionContext() {
                return null;
            }
        };
        ConditionContext context = new ConditionContext(null, 0, 0, 0, nonTiered);

        double result = expr.evaluate(context);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testParseInComparison_GreaterThanOrEqual() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass >= 2");

        assertNotNull(expr);
        // The expression should parse successfully (as a comparison or condition wrapper)

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        // In boolean context, 1.0 = true, 0.0 = false
        assertEquals(1.0, result, 0.001); // tier.glass (3) >= 2 is true
    }

    @Test
    public void testParseInComparison_Equality() {
        IExpression expr = ExpressionParser.parseExpression("tier.casing == 3");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 3);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(1.0, result, 0.001); // tier.casing (3) == 3 is true
    }

    @Test
    public void testParseInComparison_LessThan() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass < 5");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(1.0, result, 0.001); // tier.glass (3) < 5 is true
    }

    @Test
    public void testParseInArithmetic_Addition() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass + tier.casing");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(5.0, result, 0.001); // 3 + 2 = 5
    }

    @Test
    public void testParseInArithmetic_Multiplication() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass * 10 + tier.casing");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(32.0, result, 0.001); // 3*10 + 2 = 32
    }

    @Test
    public void testParseInArithmetic_Complex() {
        IExpression expr = ExpressionParser.parseExpression("(tier.glass + tier.casing) * 2");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(10.0, result, 0.001); // (3 + 2) * 2 = 10
    }

    @Test
    public void testFromJson() {
        String json = "{\"type\": \"component_tier\", \"component\": \"glass\"}";
        JsonObject jsonObj = new JsonParser().parse(json)
            .getAsJsonObject();

        IExpression expr = ExpressionsParser.parse(jsonObj);

        assertNotNull(expr);
        assertInstanceOf(ComponentTierExpression.class, expr);
        assertEquals("glass", ((ComponentTierExpression) expr).getComponentName());
    }

    @Test
    public void testFromJson_DifferentComponent() {
        String json = "{\"type\": \"component_tier\", \"component\": \"casing\"}";
        JsonObject jsonObj = new JsonParser().parse(json)
            .getAsJsonObject();

        IExpression expr = ExpressionsParser.parse(jsonObj);

        assertNotNull(expr);
        assertInstanceOf(ComponentTierExpression.class, expr);
        assertEquals("casing", ((ComponentTierExpression) expr).getComponentName());
    }

    @Test
    public void testToString() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");

        assertEquals("tier.glass", expr.toString());
    }

    @Test
    public void testToString_DifferentComponent() {
        ComponentTierExpression expr = new ComponentTierExpression("casing");

        assertEquals("tier.casing", expr.toString());
    }

    @Test
    public void testInvalidTierExpression_NoComponent() {
        Exception exception = assertThrows(RuntimeException.class, () -> { ExpressionParser.parseExpression("tier"); });

        assertTrue(
            exception.getMessage()
                .contains("Unknown variable"));
    }

    @Test
    public void testInvalidTierExpression_TooManySegments() {
        Exception exception = assertThrows(
            RuntimeException.class,
            () -> { ExpressionParser.parseExpression("tier.glass.extra"); });

        assertTrue(
            exception.getMessage()
                .contains("Invalid tier expression"));
    }

    @Test
    public void testConstructor_NullComponentName() {
        assertThrows(IllegalArgumentException.class, () -> { new ComponentTierExpression(null); });
    }

    @Test
    public void testConstructor_EmptyComponentName() {
        assertThrows(IllegalArgumentException.class, () -> { new ComponentTierExpression(""); });
    }

    @Test
    public void testCaseInsensitiveTierKeyword() {
        // "tier" keyword should be case-insensitive
        IExpression expr1 = ExpressionParser.parseExpression("tier.glass");
        IExpression expr2 = ExpressionParser.parseExpression("Tier.glass");
        IExpression expr3 = ExpressionParser.parseExpression("TIER.glass");

        assertInstanceOf(ComponentTierExpression.class, expr1);
        assertInstanceOf(ComponentTierExpression.class, expr2);
        assertInstanceOf(ComponentTierExpression.class, expr3);
    }

    @Test
    public void testComponentNamePreservesCase() {
        ComponentTierExpression expr = new ComponentTierExpression("GlassTier");

        assertEquals("GlassTier", expr.getComponentName());
        assertEquals("tier.GlassTier", expr.toString());
    }

    @Test
    public void testLogicalAnd_WithTierExpressions() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass >= 2 && tier.casing >= 2");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 3);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(1.0, result, 0.001); // Both conditions true
    }

    @Test
    public void testLogicalOr_WithTierExpressions() {
        IExpression expr = ExpressionParser.parseExpression("tier.glass >= 5 || tier.casing >= 2");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 3);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(1.0, result, 0.001); // Second condition true
    }

    @Test
    public void testComplexLogicalExpression() {
        IExpression expr = ExpressionParser
            .parseExpression("(tier.glass >= 3 || tier.casing >= 3) && tier.glass + tier.casing >= 5");

        assertNotNull(expr);

        MockTieredMachine machine = new MockTieredMachine(3, 2);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);
        assertEquals(1.0, result, 0.001); // (3>=3 || 2>=3) && (3+2>=5) = true && true = true
    }

    @Test
    public void testEvaluate_ZeroTier() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");
        MockTieredMachine machine = new MockTieredMachine(0, 0);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    public void testEvaluate_HighTier() {
        ComponentTierExpression expr = new ComponentTierExpression("glass");
        MockTieredMachine machine = new MockTieredMachine(10, 5);
        ConditionContext context = new ConditionContext(null, 0, 0, 0, machine);

        double result = expr.evaluate(context);

        assertEquals(10.0, result, 0.001);
    }
}
