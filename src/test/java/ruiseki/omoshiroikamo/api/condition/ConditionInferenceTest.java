package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.test.RegistryMocker;

@DisplayName("Condition 推論とショートハンド記法のテスト")
public class ConditionInferenceTest {

    @BeforeAll
    public static void setUp() {
        RegistryMocker.mockAll();
        Conditions.registerDefaults();
    }

    @Test
    @DisplayName("Dimension: ids キーによる従来記法の推論")
    public void testDimensionInferenceOld() {
        JsonObject json = new JsonObject();
        JsonArray ids = new JsonArray();
        ids.add(new JsonPrimitive(0));
        json.add("ids", ids);

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof DimensionCondition, "idsキーがあればDimensionConditionと推論されるべき");
    }

    @Test
    @DisplayName("Dimension: dimension キーによるショートハンドの推論")
    public void testDimensionInferenceShorthand() {
        JsonObject json = new JsonObject();
        json.addProperty("dimension", 0);

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof DimensionCondition, "dimensionキーがあればDimensionConditionと推論されるべき");
    }

    @Test
    @DisplayName("Biome: biome キーによるショートハンドの推論")
    public void testBiomeInferenceShorthand() {
        JsonObject json = new JsonObject();
        json.addProperty("biome", "Plains");

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof BiomeCondition, "biomeキーがあればBiomeConditionと推論されるべき");
    }

    @Test
    @DisplayName("Block: block キーによる推論 (新設)")
    public void testBlockInference() {
        JsonObject json = new JsonObject();
        json.addProperty("block", "minecraft:gold_block");

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof BlockCondition, "blockキーがあればBlockConditionと推論されるべき");
    }

    @Test
    @DisplayName("Logical: and キーによるショートハンドの推論")
    public void testAndInferenceShorthand() {
        JsonObject json = new JsonObject();
        json.add("and", new JsonArray());

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof OpAnd, "andキーがあればOpAndと推論されるべき");
    }

    @Test
    @DisplayName("Logical: not キーによるショートハンドの推論")
    public void testNotInferenceShorthand() {
        JsonObject json = new JsonObject();
        JsonObject inner = new JsonObject();
        inner.addProperty("biome", "Plains");
        json.add("not", inner);

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof OpNot, "notキーがあればOpNotと推論されるべき");
    }

    @Test
    @DisplayName("Offset: dx/dy/dz キーによる推論")
    public void testOffsetInference() {
        JsonObject json = new JsonObject();
        json.addProperty("dy", 1);
        json.add("condition", new JsonObject()); // Empty inner condition for inference test

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof OffsetCondition, "dyキーとconditionキーがあればOffsetConditionと推論されるべき");
    }

    @Test
    @DisplayName("Weather: weather キーによる推論")
    public void testWeatherInference() {
        JsonObject json = new JsonObject();
        json.addProperty("weather", "clear");

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof WeatherCondition, "weatherキーがあればWeatherConditionと推論されるべき");
    }

    @Test
    @DisplayName("Type明示: 従来通り type キーがあれば優先される")
    public void testExplicitType() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "biome");
        json.addProperty("biome", "Plains");

        ICondition cond = ConditionParserRegistry.parse(json);
        assertTrue(cond instanceof BiomeCondition, "typeキーがあれば推論より優先されるべき");
    }
}
