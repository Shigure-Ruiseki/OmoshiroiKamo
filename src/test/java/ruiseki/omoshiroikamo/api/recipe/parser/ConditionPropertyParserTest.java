package ruiseki.omoshiroikamo.api.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.condition.Conditions;
import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.ConditionPropertyParser;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ConditionPropertyParser のユニットテスト
 *
 * ============================================
 * Condition配列・単体パースのテスト
 * ============================================
 *
 * ConditionPropertyParser は、JSONから ICondition のリストまたは
 * 単体をパースして、ModularRecipe.Builder に追加します。
 *
 * - JsonArray: 複数のConditionをパース
 * - JsonObject: 単一のConditionをパース
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("ConditionPropertyParser（Condition配列パース）のテスト")
public class ConditionPropertyParserTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
        Conditions.registerDefaults(); // Conditionパーサーを登録
    }

    // ========================================
    // 正常系: JsonObjectの単一Condition
    // ========================================

    @Test
    @DisplayName("JsonObject: 単一Conditionをパース")
    public void test単一Condition() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "dimension");
        JsonArray idsArray = new JsonArray();
        idsArray.add(new JsonPrimitive(0)); // Overworld
        conditionJson.add("ids", idsArray);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, conditionJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getConditions()
                .size(),
            "1つのConditionが追加されるべき");
    }

    // ========================================
    // 正常系: JsonArrayの複数Condition
    // ========================================

    @Test
    @DisplayName("JsonArray: 複数Conditionをパース")
    public void test複数Condition() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonArray conditionArray = new JsonArray();

        // Condition 1: DimensionCondition
        JsonObject condition1 = new JsonObject();
        condition1.addProperty("type", "dimension");
        JsonArray ids1 = new JsonArray();
        ids1.add(new JsonPrimitive(0));
        condition1.add("ids", ids1);
        conditionArray.add(condition1);

        // Condition 2: WeatherCondition
        JsonObject condition2 = new JsonObject();
        condition2.addProperty("type", "weather");
        condition2.addProperty("weather", "clear");
        conditionArray.add(condition2);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, conditionArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            2,
            recipe.getConditions()
                .size(),
            "2つのConditionが追加されるべき");
    }

    // ========================================
    // 正常系: 空のJsonArray
    // ========================================

    @Test
    @DisplayName("JsonArray: 空配列の場合、何も追加しない")
    public void test空配列() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonArray emptyArray = new JsonArray();

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, emptyArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            0,
            recipe.getConditions()
                .size(),
            "空配列の場合、Conditionは追加されないべき");
    }

    // ========================================
    // エッジケース: 無効なCondition
    // ========================================

    @Test
    @DisplayName("【エッジ】無効なConditionはスキップされる")
    public void test無効Condition() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonObject invalidCondition = new JsonObject();
        invalidCondition.addProperty("type", "unknown_condition_type");
        invalidCondition.addProperty("invalid_field", "value");

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        // 無効なConditionはnullを返すため、追加されない
        parser.parse(builder, invalidCondition);

        ModularRecipe recipe = builder.build();

        assertEquals(
            0,
            recipe.getConditions()
                .size(),
            "無効なConditionはスキップされるべき");
    }

    // ========================================
    // エッジケース: 混在したJsonArray
    // ========================================

    @Test
    @DisplayName("【エッジ】有効と無効が混在した配列")
    public void test混在配列() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonArray mixedArray = new JsonArray();

        // 有効なCondition
        JsonObject validCondition = new JsonObject();
        validCondition.addProperty("type", "weather");
        validCondition.addProperty("weather", "rain");
        mixedArray.add(validCondition);

        // 無効なCondition
        JsonObject invalidCondition = new JsonObject();
        invalidCondition.addProperty("type", "unknown");
        mixedArray.add(invalidCondition);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, mixedArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getConditions()
                .size(),
            "有効なConditionのみ追加されるべき");
    }

    // ========================================
    // 統合テスト: 実際のConditionとの連携
    // ========================================

    @Test
    @DisplayName("【統合】DimensionConditionとして正しくパースされる")
    public void test統合DimensionCondition() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "dimension");
        JsonArray idsArray = new JsonArray();
        idsArray.add(new JsonPrimitive(-1)); // Nether
        idsArray.add(new JsonPrimitive(1)); // End
        conditionJson.add("ids", idsArray);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, conditionJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getConditions()
                .size());
        assertNotNull(
            recipe.getConditions()
                .get(0));
    }

    @Test
    @DisplayName("【統合】WeatherConditionとして正しくパースされる")
    public void test統合WeatherCondition() {
        ConditionPropertyParser parser = new ConditionPropertyParser();

        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "weather");
        conditionJson.addProperty("weather", "thunder");

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, conditionJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getConditions()
                .size());
    }
}
