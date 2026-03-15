package ruiseki.omoshiroikamo.api.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.InputPropertyParser;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * InputPropertyParser のユニットテスト
 *
 * ============================================
 * Input配列・単体パースのテスト
 * ============================================
 *
 * InputPropertyParser は、JSONから IRecipeInput のリストまたは
 * 単体をパースして、ModularRecipe.Builder に追加します。
 *
 * - JsonArray: 複数のInputをパース
 * - JsonObject: 単一のInputをパース
 *
 * バグ発見の優先度: ★★★★★
 *
 * ============================================
 */
@DisplayName("InputPropertyParserのテスト")
public class InputPropertyParserTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: JsonObjectの単一Input
    // ========================================

    @Test
    @DisplayName("JsonObject: 単一Inputをパース")
    public void test単一Input() {
        InputPropertyParser parser = new InputPropertyParser();

        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("type", "item");
        inputJson.addProperty("item", "minecraft:iron_ingot");
        inputJson.addProperty("amount", 5);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, inputJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getInputs()
                .size(),
            "1つのInputが追加されるべき");
    }

    // ========================================
    // 正常系: JsonArrayの複数Input
    // ========================================

    @Test
    @DisplayName("JsonArray: 複数Inputをパース")
    public void test複数Input() {
        InputPropertyParser parser = new InputPropertyParser();

        JsonArray inputArray = new JsonArray();

        JsonObject input1 = new JsonObject();
        input1.addProperty("type", "item");
        input1.addProperty("item", "minecraft:iron_ingot");
        input1.addProperty("amount", 5);
        inputArray.add(input1);

        JsonObject input2 = new JsonObject();
        input2.addProperty("type", "item");
        input2.addProperty("item", "minecraft:gold_ingot");
        input2.addProperty("amount", 3);
        inputArray.add(input2);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, inputArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            2,
            recipe.getInputs()
                .size(),
            "2つのInputが追加されるべき");
    }

    // ========================================
    // 正常系: 空のJsonArray
    // ========================================

    @Test
    @DisplayName("JsonArray: 空配列の場合、何も追加しない")
    public void test空配列() {
        InputPropertyParser parser = new InputPropertyParser();

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
            recipe.getInputs()
                .size(),
            "空配列の場合、Inputは追加されないべき");
    }

    // ========================================
    // エッジケース: 無効なInput
    // ========================================

    @Test
    @Disabled("無効Input時の例外ハンドリングが未実装のため無効化")
    @DisplayName("(yet) 【エッジ】無効なInputはスキップされる")
    public void test無効Input() {
        InputPropertyParser parser = new InputPropertyParser();

        JsonObject invalidInput = new JsonObject();
        invalidInput.addProperty("type", "unknown_type");
        invalidInput.addProperty("invalid_field", "value");

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        // 無効なInputはnullを返すため、追加されない
        parser.parse(builder, invalidInput);

        ModularRecipe recipe = builder.build();

        assertEquals(
            0,
            recipe.getInputs()
                .size(),
            "無効なInputはスキップされるべき");
    }

    // ========================================
    // エッジケース: 混在したJsonArray
    // ========================================

    @Test
    @Disabled("無効Input時の例外ハンドリングが未実装のため無効化")
    @DisplayName("(yet) 【エッジ】有効と無効が混在した配列")
    public void test混在配列() {
        InputPropertyParser parser = new InputPropertyParser();

        JsonArray mixedArray = new JsonArray();

        // 有効なInput
        JsonObject validInput = new JsonObject();
        validInput.addProperty("type", "item");
        validInput.addProperty("item", "minecraft:iron_ingot");
        validInput.addProperty("amount", 5);
        mixedArray.add(validInput);

        // 無効なInput
        JsonObject invalidInput = new JsonObject();
        invalidInput.addProperty("type", "unknown");
        mixedArray.add(invalidInput);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, mixedArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getInputs()
                .size(),
            "有効なInputのみ追加されるべき");
    }

    // ========================================
    // 統合テスト: 実際のItemInputとの連携
    // ========================================

    @Test
    @DisplayName("【統合】ItemInputとして正しくパースされる")
    public void test統合ItemInput() {
        InputPropertyParser parser = new InputPropertyParser();

        JsonObject inputJson = new JsonObject();
        inputJson.addProperty("type", "item");
        inputJson.addProperty("item", "minecraft:diamond");
        inputJson.addProperty("amount", 10);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, inputJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getInputs()
                .size());
        // 実際のInputの内容を確認
        assertNotNull(
            recipe.getInputs()
                .get(0));
    }
}
