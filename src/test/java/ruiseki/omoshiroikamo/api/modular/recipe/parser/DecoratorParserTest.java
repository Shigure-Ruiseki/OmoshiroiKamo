package ruiseki.omoshiroikamo.api.modular.recipe.parser;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.decorator.BonusOutputDecorator;
import ruiseki.omoshiroikamo.api.modular.recipe.decorator.ChanceRecipeDecorator;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;

/**
 * DecoratorParser のユニットテスト
 *
 * ============================================
 * JSONからデコレータを読み込む機能をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - JSONからのデコレータ適用は頻繁に使われる
 * - パースエラーでレシピが正しく読み込まれない
 * - 複数デコレータの順序が重要
 *
 * カバーする機能:
 * - 単一デコレータのパース
 * - 複数デコレータの配列パース
 * - 不正なtypeのエラーハンドリング
 *
 * ============================================
 */
@DisplayName("DecoratorParser のテスト")
public class DecoratorParserTest {

    private IModularRecipe baseRecipe;

    @BeforeEach
    public void setUp() {
        baseRecipe = ModularRecipe.builder()
            .registryName("base")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ItemOutput(new ItemStack(Items.iron_ingot, 1)))
            .build();
    }

    // ========================================
    // 単一デコレータのパース
    // ========================================

    @Test
    @DisplayName("【最重要】chance デコレータをJSONからパース")
    public void testParseChanceDecorator() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "chance");
        json.addProperty("chance", 0.75);

        IModularRecipe decorated = DecoratorParser.parse(baseRecipe, json);

        assertNotNull(decorated);
        assertTrue(decorated instanceof ChanceRecipeDecorator);
        assertEquals("base", decorated.getRegistryName());
    }

    @Test
    @DisplayName("【最重要】bonus デコレータをJSONからパース")
    public void testParseBonusDecorator() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "bonus");
        json.addProperty("chance", 0.5);

        // outputs配列
        JsonArray outputs = new JsonArray();
        JsonObject output = new JsonObject();
        output.addProperty("item", "minecraft:diamond");
        output.addProperty("amount", 1);
        outputs.add(output);
        json.add("outputs", outputs);

        IModularRecipe decorated = DecoratorParser.parse(baseRecipe, json);

        assertNotNull(decorated);
        assertTrue(decorated instanceof BonusOutputDecorator);
    }

    // ========================================
    // 複数デコレータの配列パース
    // ========================================

    @Test
    @DisplayName("【★重要★】配列で複数デコレータを順番に適用")
    public void testParseMultipleDecorators() {
        JsonArray decorators = new JsonArray();

        // 1つ目: chance 0.5
        JsonObject decorator1 = new JsonObject();
        decorator1.addProperty("type", "chance");
        decorator1.addProperty("chance", 0.5);
        decorators.add(decorator1);

        // 2つ目: chance 0.8
        JsonObject decorator2 = new JsonObject();
        decorator2.addProperty("type", "chance");
        decorator2.addProperty("chance", 0.8);
        decorators.add(decorator2);

        IModularRecipe decorated = DecoratorParser.parse(baseRecipe, decorators);

        assertNotNull(decorated);
        // 2つのデコレータが順番に適用されている
        assertTrue(decorated instanceof ChanceRecipeDecorator);
        assertEquals("base", decorated.getRegistryName());
    }

    @Test
    @DisplayName("【★重要★】chance + bonus の組み合わせをJSONからパース")
    public void testParseChanceAndBonus() {
        JsonArray decorators = new JsonArray();

        // 1つ目: chance
        JsonObject chance = new JsonObject();
        chance.addProperty("type", "chance");
        chance.addProperty("chance", 0.75);
        decorators.add(chance);

        // 2つ目: bonus
        JsonObject bonus = new JsonObject();
        bonus.addProperty("type", "bonus");
        bonus.addProperty("chance", 1.0);
        JsonArray outputs = new JsonArray();
        JsonObject output = new JsonObject();
        output.addProperty("item", "minecraft:gold_ingot");
        output.addProperty("amount", 2);
        outputs.add(output);
        bonus.add("outputs", outputs);
        decorators.add(bonus);

        IModularRecipe decorated = DecoratorParser.parse(baseRecipe, decorators);

        assertNotNull(decorated);
        assertTrue(decorated instanceof BonusOutputDecorator);
    }

    // ========================================
    // エッジケース・エラーハンドリング
    // ========================================

    @Test
    @DisplayName("null を渡すと元のレシピを返す")
    public void testParseNull() {
        IModularRecipe result = DecoratorParser.parse(baseRecipe, (JsonObject) null);

        assertNotNull(result);
        assertEquals(baseRecipe, result);
    }

    @Test
    @DisplayName("空の配列を渡すと元のレシピを返す")
    public void testParseEmptyArray() {
        JsonArray emptyArray = new JsonArray();

        IModularRecipe result = DecoratorParser.parse(baseRecipe, emptyArray);

        assertNotNull(result);
        // 空配列なので何も適用されず、元のレシピが返る
        assertEquals("base", result.getRegistryName());
    }

    @Test
    @DisplayName("【エラー】不正なtype名でエラーが発生")
    public void testParseInvalidType() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "invalid_decorator_type");
        json.addProperty("chance", 0.5);

        assertThrows(
            IllegalArgumentException.class,
            () -> { DecoratorParser.parse(baseRecipe, json); },
            "不正なデコレータtypeでエラーになるべき");
    }

    @Test
    @DisplayName("【エラー】typeキーがない場合")
    public void testParseMissingType() {
        JsonObject json = new JsonObject();
        // typeキーなし
        json.addProperty("chance", 0.5);

        assertThrows(Exception.class, () -> { DecoratorParser.parse(baseRecipe, json); }, "typeキーがない場合エラーになるべき");
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: requirement デコレータのパーステスト
    // TODO: weighted_random デコレータのパーステスト
    // TODO: カスタムデコレータの登録テスト
    // TODO: 実際のJSONファイルからの読み込みテスト
}
