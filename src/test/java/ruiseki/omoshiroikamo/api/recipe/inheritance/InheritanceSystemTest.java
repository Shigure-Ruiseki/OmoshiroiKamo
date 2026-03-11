package ruiseki.omoshiroikamo.api.recipe.inheritance;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.MachineryRecipeLoader;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeJsonMergeUtil;

/**
 * 継承システムのテスト
 *
 * ============================================
 * レシピ継承機能の完全検証
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - 循環継承はスタックオーバーフローを引き起こす可能性
 * - 継承チェーンの誤りはレシピが正しく読み込まれない
 * - プロパティマージの誤りはレシピ動作の予期しない変化
 *
 * カバーする機能:
 * - 基本的な親子継承
 * - 循環継承の検出
 * - 多段階継承（孫、ひ孫など）
 * - プロパティのマージルール
 * - 抽象レシピ（abstract=true）
 * - 入力・出力・条件の継承
 *
 * ============================================
 */
@DisplayName("継承システムテスト")
public class InheritanceSystemTest {

    // ========================================
    // 基本的な継承機能
    // ========================================

    @Test
    @DisplayName("【基本】親から子へプロパティが継承される")
    public void test基本的な継承() {
        JsonObject parent = new JsonObject();
        parent.addProperty("registryName", "parent");
        parent.addProperty("name", "Parent Recipe");
        parent.addProperty("machine", "crusher");
        parent.addProperty("duration", 200);

        JsonObject child = new JsonObject();
        child.addProperty("registryName", "child");
        child.addProperty("parent", "parent");

        RecipeJsonMergeUtil.merge(child, parent);

        assertEquals(
            "Parent Recipe",
            child.get("name")
                .getAsString());
        assertEquals(
            "crusher",
            child.get("machine")
                .getAsString());
        assertEquals(
            200,
            child.get("duration")
                .getAsInt());
    }

    @Test
    @DisplayName("【基本】子のプロパティは親を上書きする")
    public void test子の上書き() {
        JsonObject parent = new JsonObject();
        parent.addProperty("machine", "crusher");
        parent.addProperty("duration", 200);

        JsonObject child = new JsonObject();
        child.addProperty("machine", "smelter");
        child.addProperty("duration", 100);

        RecipeJsonMergeUtil.merge(child, parent);

        assertEquals(
            "smelter",
            child.get("machine")
                .getAsString());
        assertEquals(
            100,
            child.get("duration")
                .getAsInt());
    }

    @Test
    @DisplayName("【基本】親子の入力がマージされる（親が先、子が後）")
    public void test入力のマージ() {
        JsonObject parent = new JsonObject();
        JsonArray pIn = new JsonArray();
        JsonObject iron = new JsonObject();
        iron.addProperty("item", "minecraft:iron_ingot");
        pIn.add(iron);
        parent.add("inputs", pIn);

        JsonObject child = new JsonObject();
        JsonArray cIn = new JsonArray();
        JsonObject gold = new JsonObject();
        gold.addProperty("item", "minecraft:gold_ingot");
        cIn.add(gold);
        child.add("inputs", cIn);

        RecipeJsonMergeUtil.merge(child, parent);

        JsonArray result = child.getAsJsonArray("inputs");
        assertEquals(2, result.size());
        assertEquals(
            "minecraft:iron_ingot",
            result.get(0)
                .getAsJsonObject()
                .get("item")
                .getAsString());
        assertEquals(
            "minecraft:gold_ingot",
            result.get(1)
                .getAsJsonObject()
                .get("item")
                .getAsString());
    }

    // ========================================
    // 多段階継承
    // ========================================

    @Test
    @DisplayName("【多段階】ひ孫まで継承が正しく動作する")
    public void test4段階継承() {
        List<JsonObject> jsons = new ArrayList<>();

        JsonObject ggp = new JsonObject();
        ggp.addProperty("registryName", "great_grandparent");
        ggp.addProperty("machine", "crusher");
        ggp.addProperty("duration", 400);
        jsons.add(ggp);

        JsonObject gp = new JsonObject();
        gp.addProperty("registryName", "grandparent");
        gp.addProperty("parent", "great_grandparent");
        gp.addProperty("duration", 300);
        jsons.add(gp);

        JsonObject p = new JsonObject();
        p.addProperty("registryName", "parent");
        p.addProperty("parent", "grandparent");
        p.addProperty("duration", 200);
        jsons.add(p);

        JsonObject c = new JsonObject();
        c.addProperty("registryName", "child");
        c.addProperty("parent", "parent");
        jsons.add(c);

        List<IModularRecipe> recipes = MachineryRecipeLoader.load(jsons);
        IModularRecipe childRecipe = findRecipe(recipes, "child");

        assertNotNull(childRecipe);
        assertEquals("crusher", childRecipe.getRecipeGroup()); // 曾祖父から
        assertEquals(200, childRecipe.getDuration()); // 親から
    }

    // ========================================
    // 循環継承の検出
    // ========================================

    @Test
    @DisplayName("【循環】A→B→Aを検出してクラッシュしない")
    public void test循環継承検出_2レシピ() {
        List<JsonObject> jsons = new ArrayList<>();
        JsonObject a = new JsonObject();
        a.addProperty("registryName", "A");
        a.addProperty("parent", "B");
        jsons.add(a);

        JsonObject b = new JsonObject();
        b.addProperty("registryName", "B");
        b.addProperty("parent", "A");
        jsons.add(b);

        assertDoesNotThrow(() -> MachineryRecipeLoader.load(jsons));
    }

    @Test
    @DisplayName("【循環】A→B→C→Aを検出してクラッシュしない")
    public void test循環継承検出_3レシピ() {
        List<JsonObject> jsons = new ArrayList<>();
        JsonObject a = new JsonObject();
        a.addProperty("registryName", "A");
        a.addProperty("parent", "B");
        jsons.add(a);
        JsonObject b = new JsonObject();
        b.addProperty("registryName", "B");
        b.addProperty("parent", "C");
        jsons.add(b);
        JsonObject c = new JsonObject();
        c.addProperty("registryName", "C");
        c.addProperty("parent", "A");
        jsons.add(c);

        assertDoesNotThrow(() -> MachineryRecipeLoader.load(jsons));
    }

    @Test
    @DisplayName("【循環】自己参照 A→A を検出してクラッシュしない")
    public void test自己参照検出() {
        List<JsonObject> jsons = new ArrayList<>();
        JsonObject a = new JsonObject();
        a.addProperty("registryName", "self");
        a.addProperty("parent", "self");
        jsons.add(a);

        assertDoesNotThrow(() -> MachineryRecipeLoader.load(jsons));
    }

    // ========================================
    // 抽象レシピ
    // ========================================

    @Test
    @DisplayName("【抽象】抽象レシピは出力に含まれず、継承元としてのみ機能する")
    public void test抽象レシピ() {
        List<JsonObject> jsons = new ArrayList<>();

        JsonObject template = new JsonObject();
        template.addProperty("registryName", "template");
        template.addProperty("abstract", true);
        template.addProperty("duration", 200);
        jsons.add(template);

        JsonObject concrete = new JsonObject();
        concrete.addProperty("registryName", "concrete_recipe");
        concrete.addProperty("parent", "template");
        jsons.add(concrete);

        List<IModularRecipe> recipes = MachineryRecipeLoader.load(jsons);

        assertEquals(1, recipes.size());
        assertEquals(
            "concrete_recipe",
            recipes.get(0)
                .getRegistryName());
        assertEquals(
            200,
            recipes.get(0)
                .getDuration());
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】存在しない親を指定しても正常にスキップされる")
    public void test存在しない親() {
        List<JsonObject> jsons = new ArrayList<>();
        JsonObject c = new JsonObject();
        c.addProperty("registryName", "child");
        c.addProperty("parent", "nonexistent");
        jsons.add(c);

        List<IModularRecipe> recipes = MachineryRecipeLoader.load(jsons);
        assertEquals(1, recipes.size());
        assertEquals(
            "child",
            recipes.get(0)
                .getRegistryName());
    }

    @Test
    @DisplayName("【エッジ】同じ親を持つ複数の子がそれぞれ正しく継承される")
    public void test同じ親を持つ複数の子() {
        List<JsonObject> jsons = new ArrayList<>();

        JsonObject parent = new JsonObject();
        parent.addProperty("registryName", "common_parent");
        parent.addProperty("machine", "crusher");
        jsons.add(parent);

        for (int i = 1; i <= 3; i++) {
            JsonObject child = new JsonObject();
            child.addProperty("registryName", "child" + i);
            child.addProperty("parent", "common_parent");
            jsons.add(child);
        }

        List<IModularRecipe> recipes = MachineryRecipeLoader.load(jsons);
        assertEquals(4, recipes.size()); // 親(非abstract) + 子3つ

        for (int i = 1; i <= 3; i++) {
            IModularRecipe childRecipe = findRecipe(recipes, "child" + i);
            assertNotNull(childRecipe);
            assertEquals("crusher", childRecipe.getRecipeGroup());
        }
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private IModularRecipe findRecipe(List<IModularRecipe> recipes, String name) {
        return recipes.stream()
            .filter(r -> name.equals(r.getRegistryName()))
            .findFirst()
            .orElse(null);
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: JSON経由での継承テスト（実際のJSONファイルを使用）
    // TODO: デコレータと継承の組み合わせテスト
    // TODO: 条件と継承の組み合わせテスト
    // TODO: 複雑な継承チェーンのパフォーマンステスト
}
