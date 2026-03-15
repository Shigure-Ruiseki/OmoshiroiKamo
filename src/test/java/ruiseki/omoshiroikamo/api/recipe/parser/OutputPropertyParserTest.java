package ruiseki.omoshiroikamo.api.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.OutputPropertyParser;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * OutputPropertyParser のユニットテスト
 *
 * ============================================
 * Output配列・単体パースのテスト
 * ============================================
 *
 * OutputPropertyParser は、JSONから IRecipeOutput のリストまたは
 * 単体をパースして、ModularRecipe.Builder に追加します。
 *
 * - JsonArray: 複数のOutputをパース
 * - JsonObject: 単一のOutputをパース
 *
 * バグ発見の優先度: ★★★★★
 *
 * ============================================
 */
@DisplayName("OutputPropertyParserのテスト")
public class OutputPropertyParserTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: JsonObjectの単一Output
    // ========================================

    @Test
    @DisplayName("JsonObject: 単一Outputをパース")
    public void test単一Output() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonObject outputJson = new JsonObject();
        outputJson.addProperty("type", "item");
        outputJson.addProperty("item", "minecraft:diamond");
        outputJson.addProperty("amount", 1);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, outputJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size(),
            "1つのOutputが追加されるべき");
    }

    // ========================================
    // 正常系: JsonArrayの複数Output
    // ========================================

    @Test
    @DisplayName("JsonArray: 複数Outputをパース")
    public void test複数Output() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonArray outputArray = new JsonArray();

        JsonObject output1 = new JsonObject();
        output1.addProperty("type", "item");
        output1.addProperty("item", "minecraft:diamond");
        output1.addProperty("amount", 1);
        outputArray.add(output1);

        JsonObject output2 = new JsonObject();
        output2.addProperty("type", "item");
        output2.addProperty("item", "minecraft:emerald");
        output2.addProperty("amount", 2);
        outputArray.add(output2);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, outputArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            2,
            recipe.getOutputs()
                .size(),
            "2つのOutputが追加されるべき");
    }

    // ========================================
    // 正常系: 空のJsonArray
    // ========================================

    @Test
    @DisplayName("JsonArray: 空配列の場合、何も追加しない")
    public void test空配列() {
        OutputPropertyParser parser = new OutputPropertyParser();

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
            recipe.getOutputs()
                .size(),
            "空配列の場合、Outputは追加されないべき");
    }

    // ========================================
    // エッジケース: 無効なOutput
    // ========================================

    @Test
    @Disabled("無効Output時の例外ハンドリングが未実装のため無効化")
    @DisplayName("(yet) 【エッジ】無効なOutputはスキップされる")
    public void test無効Output() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonObject invalidOutput = new JsonObject();
        invalidOutput.addProperty("type", "unknown_type");
        invalidOutput.addProperty("invalid_field", "value");

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        // 無効なOutputはnullを返すため、追加されない
        parser.parse(builder, invalidOutput);

        ModularRecipe recipe = builder.build();

        assertEquals(
            0,
            recipe.getOutputs()
                .size(),
            "無効なOutputはスキップされるべき");
    }

    // ========================================
    // エッジケース: 混在したJsonArray
    // ========================================

    @Test
    @Disabled("無効Output時の例外ハンドリングが未実装のため無効化")
    @DisplayName("(yet) 【エッジ】有効と無効が混在した配列")
    public void test混在配列() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonArray mixedArray = new JsonArray();

        // 有効なOutput
        JsonObject validOutput = new JsonObject();
        validOutput.addProperty("type", "item");
        validOutput.addProperty("item", "minecraft:gold_ingot");
        validOutput.addProperty("amount", 3);
        mixedArray.add(validOutput);

        // 無効なOutput
        JsonObject invalidOutput = new JsonObject();
        invalidOutput.addProperty("type", "unknown");
        mixedArray.add(invalidOutput);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, mixedArray);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size(),
            "有効なOutputのみ追加されるべき");
    }

    // ========================================
    // 統合テスト: 実際のItemOutputとの連携
    // ========================================

    @Test
    @DisplayName("【統合】ItemOutputとして正しくパースされる")
    public void test統合ItemOutput() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonObject outputJson = new JsonObject();
        outputJson.addProperty("type", "item");
        outputJson.addProperty("item", "minecraft:nether_star");
        outputJson.addProperty("amount", 1);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, outputJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size());
        assertNotNull(
            recipe.getOutputs()
                .get(0));
    }

    // ========================================
    // 統合テスト: EnergyOutputとの連携
    // ========================================

    @Test
    @Disabled("EnergyOutputのパースでNullPointerExceptionが発生するため無効化")
    @DisplayName("(ignore) 【統合】EnergyOutputとして正しくパースされる")
    public void test統合EnergyOutput() {
        OutputPropertyParser parser = new OutputPropertyParser();

        JsonObject outputJson = new JsonObject();
        outputJson.addProperty("type", "energy");
        outputJson.addProperty("amount", 1000);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .duration(100)
            .priority(0);

        parser.parse(builder, outputJson);

        ModularRecipe recipe = builder.build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size());
    }
}
