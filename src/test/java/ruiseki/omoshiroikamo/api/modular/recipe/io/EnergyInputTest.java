package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * EnergyInput のユニットテスト
 *
 * ============================================
 * Energy入力の特殊機能をテスト
 * ============================================
 *
 * EnergyInputは perTick フラグを持つ重要な入力タイプです。
 * - perTick=true: 毎tick消費（例: 100 RF/tick）
 * - perTick=false: 一括消費（例: レシピ開始時に10000 RF一括消費）
 *
 * このフラグの誤実装はレシピの動作に大きく影響します。
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("EnergyInput のテスト")
public class EnergyInputTest {

    // ========================================
    // 基本機能のテスト
    // ========================================

    @Test
    @DisplayName("perTick=trueで正しく作成できる")
    public void testPerTickTrue() {
        // 毎tick 100 RF 消費
        EnergyInput input = new EnergyInput(100, true);

        assertEquals(100, input.getRequiredAmount());
        assertEquals(IPortType.Type.ENERGY, input.getPortType());
        assertTrue(input.isPerTick(), "perTick フラグが true であるべき");
    }

    @Test
    @DisplayName("perTick=falseで正しく作成できる")
    public void testPerTickFalse() {
        // 一括 10000 RF 消費
        EnergyInput input = new EnergyInput(10000, false);

        assertEquals(10000, input.getRequiredAmount());
        assertEquals(IPortType.Type.ENERGY, input.getPortType());
        assertFalse(input.isPerTick(), "perTick フラグが false であるべき");
    }

    @Test
    @DisplayName("デフォルトではperTick=trueになる")
    public void testデフォルトPerTick() {
        // perTick 指定なし
        EnergyInput input = new EnergyInput(5000);

        assertEquals(5000, input.getRequiredAmount());
        assertTrue(input.isPerTick(), "デフォルトでは perTick=true");
    }

    // ========================================
    // JSON 読み込み/書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSONから正しく読み込める（perTick=true）")
    public void testJSON読み込み_perTickTrue() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 200);
        json.addProperty("perTick", true);

        EnergyInput input = EnergyInput.fromJson(json);

        assertNotNull(input);
        assertTrue(input.validate());
        assertEquals(200, input.getRequiredAmount());
        assertTrue(input.isPerTick());
    }

    @Test
    @DisplayName("JSONから正しく読み込める（perTick=false）")
    public void testJSON読み込み_perTickFalse() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 15000);
        json.addProperty("perTick", false);

        EnergyInput input = EnergyInput.fromJson(json);

        assertNotNull(input);
        assertTrue(input.validate());
        assertEquals(15000, input.getRequiredAmount());
        assertFalse(input.isPerTick());
    }

    @Test
    @DisplayName("JSONから読み込み時、perTickキーがなければtrue")
    public void testJSON読み込み_perTickなし() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 1000);
        // perTick キーなし

        EnergyInput input = EnergyInput.fromJson(json);

        assertNotNull(input);
        assertEquals(1000, input.getRequiredAmount());
        assertTrue(input.isPerTick(), "perTickキーがない場合はtrue");
    }

    @Test
    @DisplayName("JSONに正しく書き込める（perTick=true）")
    public void testJSON書き込み_perTickTrue() {
        EnergyInput input = new EnergyInput(300, true);

        JsonObject json = new JsonObject();
        input.write(json);

        assertTrue(json.has("energy"));
        assertEquals(
            300,
            json.get("energy")
                .getAsInt());

        assertTrue(json.has("perTick"));
        assertTrue(
            json.get("perTick")
                .getAsBoolean());
    }

    @Test
    @DisplayName("JSONに正しく書き込める（perTick=false）")
    public void testJSON書き込み_perTickFalse() {
        EnergyInput input = new EnergyInput(20000, false);

        JsonObject json = new JsonObject();
        input.write(json);

        assertTrue(json.has("energy"));
        assertEquals(
            20000,
            json.get("energy")
                .getAsInt());

        // perTick=false の場合、JSONに書き込まれない可能性もある（実装次第）
        // または明示的に false が書き込まれる
        if (json.has("perTick")) {
            assertFalse(
                json.get("perTick")
                    .getAsBoolean());
        }
    }

    // ========================================
    // バリデーションのテスト
    // ========================================

    @Test
    @DisplayName("正しいEnergyInputはvalidateがtrueを返す")
    public void test正しいInputのvalidate() {
        EnergyInput validInput = new EnergyInput(1000, true);
        assertTrue(validInput.validate());
    }

    @Test
    @DisplayName("エネルギー量が0以下の場合はvalidateがfalseを返す")
    public void test不正なInputのvalidate() {
        // 0 エネルギー
        EnergyInput invalidInput1 = new EnergyInput(0, true);
        assertFalse(invalidInput1.validate(), "0エネルギーは不正");

        // 負のエネルギー
        EnergyInput invalidInput2 = new EnergyInput(-100, true);
        assertFalse(invalidInput2.validate(), "負のエネルギーは不正");
    }

    // ========================================
    // エッジケースのテスト
    // ========================================

    @Test
    @DisplayName("【エッジ】非常に大きなエネルギー量")
    public void test大量エネルギー() {
        EnergyInput input = new EnergyInput(Integer.MAX_VALUE, false);

        assertEquals(Integer.MAX_VALUE, input.getRequiredAmount());
        assertTrue(input.validate());
    }

    @Test
    @DisplayName("【エッジ】エネルギー量=1（最小値）")
    public void test最小エネルギー() {
        EnergyInput input = new EnergyInput(1, true);

        assertEquals(1, input.getRequiredAmount());
        assertTrue(input.validate());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: consume() メソッドのテスト（モックポートが必要）
    // TODO: perTick=true の場合の総消費量計算テスト
    // TODO: JSONのpertick（小文字）とperTick（キャメルケース）の両対応
}
