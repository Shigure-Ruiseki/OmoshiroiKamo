package ruiseki.omoshiroikamo.api.structure.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

/**
 * RequirementRegistry のテスト
 *
 * ============================================
 * Requirement の動的登録システムをテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - 不正なtypeでnull処理
 * - カスタム登録の動作
 * - min/maxのデフォルト値
 *
 * カバーする機能:
 * - 初期登録の確認
 * - パース機能
 * - カスタム登録
 *
 * ============================================
 */
@DisplayName("RequirementRegistry のテスト")
public class RequirementRegistryTest {

    @Test
    @DisplayName("【最重要】7種類全てが初期登録されている")
    public void testAllTypesRegistered() {
        // itemInput, itemOutput
        assertNotNull(RequirementRegistry.parse("itemInput", createJsonWithMinMax(1, 4)));
        assertNotNull(RequirementRegistry.parse("itemOutput", createJsonWithMinMax(1, 2)));

        // fluidInput, fluidOutput
        assertNotNull(RequirementRegistry.parse("fluidInput", createJsonWithMinMax(1, 2)));
        assertNotNull(RequirementRegistry.parse("fluidOutput", createJsonWithMinMax(1, 1)));

        // energyInput, energyOutput
        assertNotNull(RequirementRegistry.parse("energyInput", createJsonWithMinMax(1, 1)));
        assertNotNull(RequirementRegistry.parse("energyOutput", createJsonWithMinMax(0, 1)));

        // manaInput, manaOutput
        assertNotNull(RequirementRegistry.parse("manaInput", createJsonWithMinMax(1, 2)));
        assertNotNull(RequirementRegistry.parse("manaOutput", createJsonWithMinMax(1, 1)));

        // gasInput, gasOutput
        assertNotNull(RequirementRegistry.parse("gasInput", createJsonWithMinMax(0, 2)));
        assertNotNull(RequirementRegistry.parse("gasOutput", createJsonWithMinMax(0, 1)));

        // essentiaInput, essentiaOutput
        assertNotNull(RequirementRegistry.parse("essentiaInput", createJsonWithMinMax(1, 3)));
        assertNotNull(RequirementRegistry.parse("essentiaOutput", createJsonWithMinMax(1, 1)));

        // visInput, visOutput
        assertNotNull(RequirementRegistry.parse("visInput", createJsonWithMinMax(1, 2)));
        assertNotNull(RequirementRegistry.parse("visOutput", createJsonWithMinMax(0, 1)));
    }

    @Test
    @DisplayName("【最重要】正しい type で parse できる")
    public void testParseCorrectType() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 2);
        json.addProperty("max", 8);

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        assertEquals("itemInput", req.getType());
        assertEquals(2, req.getMinCount());
        assertEquals(8, req.getMaxCount());
    }

    @Test
    @DisplayName("【エラー】不正な type で null が返る")
    public void testParseInvalidType() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 1);
        json.addProperty("max", 1);

        IStructureRequirement req = RequirementRegistry.parse("invalidType", json);

        assertNull(req, "不正なtypeの場合nullが返る");
    }

    @Test
    @DisplayName("カスタム Requirement の登録")
    public void testRegisterCustomRequirement() {
        // カスタムRequirementを登録
        RequirementRegistry.register("customTest", (type, data) -> {
            int min = data.has("min") ? data.get("min")
                .getAsInt() : 0;
            int max = data.has("max") ? data.get("max")
                .getAsInt() : Integer.MAX_VALUE;
            return new ItemRequirement(type, min, max);
        });

        JsonObject json = new JsonObject();
        json.addProperty("min", 5);
        json.addProperty("max", 10);

        IStructureRequirement req = RequirementRegistry.parse("customTest", json);

        assertNotNull(req);
        assertEquals("customTest", req.getType());
        assertEquals(5, req.getMinCount());
        assertEquals(10, req.getMaxCount());
    }

    @Test
    @DisplayName("重複登録（上書き）")
    public void testRegisterDuplicate_Overwrite() {
        // 既存のitemInputを上書き
        RequirementRegistry.register("itemInput", (type, data) -> {
            // カスタム実装（常にmin=99を返す）
            return new ItemRequirement(type, 99, 99);
        });

        JsonObject json = new JsonObject();
        json.addProperty("min", 1);
        json.addProperty("max", 4);

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        // 上書きされたロジックが適用される
        assertEquals(99, req.getMinCount());
        assertEquals(99, req.getMaxCount());

        // テスト後に元に戻す
        RequirementRegistry.register("itemInput", ItemRequirement::fromJson);
    }

    @Test
    @DisplayName("parse で min/max が正しく読まれる")
    public void testParseMinMax() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 3);
        json.addProperty("max", 15);

        IStructureRequirement req = RequirementRegistry.parse("fluidInput", json);

        assertNotNull(req);
        assertEquals(3, req.getMinCount());
        assertEquals(15, req.getMaxCount());
    }

    @Test
    @DisplayName("min 未指定時のデフォルト値（0）")
    public void testMinDefault() {
        JsonObject json = new JsonObject();
        // minを指定しない
        json.addProperty("max", 10);

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        assertEquals(0, req.getMinCount());
    }

    @Test
    @DisplayName("max 未指定時のデフォルト値（Integer.MAX_VALUE）")
    public void testMaxDefault() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 1);
        // maxを指定しない

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        assertEquals(Integer.MAX_VALUE, req.getMaxCount());
    }

    @Test
    @DisplayName("負のmin/max")
    public void testNegativeMinMax() {
        JsonObject json = new JsonObject();
        json.addProperty("min", -5);
        json.addProperty("max", -1);

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        // 負の値も受け入れる（バリデーションは別で行う）
        assertEquals(-5, req.getMinCount());
        assertEquals(-1, req.getMaxCount());
    }

    @Test
    @DisplayName("min > max の場合")
    public void testMinGreaterThanMax() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 10);
        json.addProperty("max", 5);

        IStructureRequirement req = RequirementRegistry.parse("itemInput", json);

        assertNotNull(req);
        // min > maxも受け入れる（バリデーションは別で行う）
        assertEquals(10, req.getMinCount());
        assertEquals(5, req.getMaxCount());
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private JsonObject createJsonWithMinMax(int min, int max) {
        JsonObject json = new JsonObject();
        json.addProperty("min", min);
        json.addProperty("max", max);
        return json;
    }
}
