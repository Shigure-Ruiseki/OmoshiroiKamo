package ruiseki.omoshiroikamo.api.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.DurationParser;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * DurationParser のユニットテスト
 *
 * ============================================
 * 時間パースのテスト
 * ============================================
 *
 * DurationParser は、JSONから処理時間（duration）を
 * パースして、ModularRecipe.Builder に設定します。
 *
 * - JsonPrimitive（整数）: 処理時間を設定
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("DurationParser（時間パース）のテスト")
public class DurationParserTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: 整数値のパース
    // ========================================

    @Test
    @DisplayName("正常系: duration=100をパース")
    public void test正常パース() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(100);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .priority(0);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        assertEquals(100, recipe.getDuration(), "durationが100に設定されるべき");
    }

    @Test
    @DisplayName("正常系: duration=1をパース")
    public void test最小値() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(1);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .priority(0);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        assertEquals(1, recipe.getDuration(), "durationが1に設定されるべき");
    }

    @Test
    @DisplayName("正常系: duration=10000をパース")
    public void test大きな値() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(10000);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .priority(0);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        assertEquals(10000, recipe.getDuration(), "durationが10000に設定されるべき");
    }

    // ========================================
    // エッジケース: ゼロ
    // ========================================

    @Test
    @DisplayName("【エッジ】duration=0をパース")
    public void testゼロ() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(0);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .priority(0);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        assertEquals(0, recipe.getDuration(), "durationが0に設定されるべき");
    }

    // ========================================
    // エッジケース: 負数
    // ========================================

    @Test
    @DisplayName("【エッジ】負数のdurationもパース可能")
    public void test負数() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(-10);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("test_recipe")
            .recipeGroup("test_group")
            .priority(0);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        // 負数も技術的にはパース可能（バリデーションは別レイヤー）
        assertEquals(-10, recipe.getDuration(), "負数のdurationもパースされるべき");
    }

    // ========================================
    // 統合テスト: 実際のレシピとの連携
    // ========================================

    @Test
    @DisplayName("【統合】durationがレシピに正しく反映される")
    public void test統合レシピ() {
        DurationParser parser = new DurationParser();

        JsonPrimitive durationJson = new JsonPrimitive(200);

        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("integration_recipe")
            .recipeGroup("test_group")
            .priority(5);

        parser.parse(builder, durationJson);

        ModularRecipe recipe = builder.build();

        assertEquals(200, recipe.getDuration());
        assertEquals("integration_recipe", recipe.getRegistryName());
    }
}
