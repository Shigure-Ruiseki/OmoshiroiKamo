package ruiseki.omoshiroikamo.api.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;

/**
 * レシピの Tier 指定に関するパースのテスト
 * 
 * JSON の "tier" または "tiers" キーが正しく IModularRecipe に
 * 変換されることを検証します（仕様の明文化）。
 */
@DisplayName("Recipe Tier パースのテスト")
public class RecipeTierParserTest {

    @Test
    @DisplayName("tiers キーによる複数コンポーネントの Tier 指定")
    public void testTiersParsing() {
        JsonObject json = new JsonObject();
        JsonObject tiers = new JsonObject();
        tiers.addProperty("casing", 2);
        tiers.addProperty("core", 3);
        json.add("tiers", tiers);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("test");

        // Registry を使用してパース
        RecipeParserRegistry.parse(builder, "tiers", json.get("tiers"));

        ModularRecipe recipe = builder.build();

        // 仕様の確認: addRequiredComponentTier が正しく呼ばれているか
        assertEquals(
            2,
            recipe.getRequiredComponentTiers()
                .getOrDefault("casing", 0));
        assertEquals(
            3,
            recipe.getRequiredComponentTiers()
                .getOrDefault("core", 0));
    }

    @Test
    @DisplayName("tier キー（単数形）による指定もサポートされる（後方互換性/柔軟性）")
    public void testTierParsing() {
        JsonObject json = new JsonObject();
        JsonObject tiers = new JsonObject();
        tiers.addProperty("casing", 5);
        json.add("tier", tiers);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("test");

        RecipeParserRegistry.parse(builder, "tier", json.get("tier"));

        ModularRecipe recipe = builder.build();
        assertEquals(
            5,
            recipe.getRequiredComponentTiers()
                .getOrDefault("casing", 0));
    }

    @Test
    @DisplayName("不正な形式（オブジェクトでない）の場合は無視される")
    public void testInvalidTierFormat() {
        JsonObject json = new JsonObject();
        json.addProperty("tier", 1); // 数値はダメ。 {"name": tier} の必要がある

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test")
            .recipeGroup("test");

        RecipeParserRegistry.parse(builder, "tier", json.get("tier"));

        ModularRecipe recipe = builder.build();
        // 指定がない場合はデフォルトの 0
        assertEquals(
            0,
            recipe.getRequiredComponentTiers()
                .getOrDefault("casing", 0));
    }
}
