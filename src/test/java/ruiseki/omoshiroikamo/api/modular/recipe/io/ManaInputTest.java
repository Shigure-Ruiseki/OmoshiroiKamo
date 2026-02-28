package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * ManaInput のユニットテスト
 *
 * ============================================
 * Mana入力の特殊機能をテスト
 * ============================================
 *
 * ManaInputも EnergyInput と同様に perTick フラグを持ちます。
 * Botania等のマナシステムとの統合に使用されます。
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("ManaInput のテスト")
public class ManaInputTest {

    // ========================================
    // 基本機能のテスト
    // ========================================

    @Test
    @DisplayName("perTick=trueで正しく作成できる")
    public void testPerTickTrue() {
        ManaInput input = new ManaInput(50, true);

        assertEquals(50, input.getRequiredAmount());
        assertEquals(IPortType.Type.MANA, input.getPortType());
        assertTrue(input.isPerTick());
    }

    @Test
    @DisplayName("perTick=falseで正しく作成できる")
    public void testPerTickFalse() {
        ManaInput input = new ManaInput(5000, false);

        assertEquals(5000, input.getRequiredAmount());
        assertEquals(IPortType.Type.MANA, input.getPortType());
        assertFalse(input.isPerTick());
    }

    @Test
    @DisplayName("デフォルトではperTick=falseになる")
    public void testデフォルトPerTick() {
        ManaInput input = new ManaInput(2000);

        assertEquals(2000, input.getRequiredAmount());
        assertFalse(input.isPerTick());
    }

    // ========================================
    // JSON 読み込み/書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSONから正しく読み込める（perTick=true）")
    public void testJSON読み込み_perTickTrue() {
        JsonObject json = new JsonObject();
        json.addProperty("mana", 100);
        json.addProperty("perTick", true);

        ManaInput input = ManaInput.fromJson(json);

        assertNotNull(input);
        assertTrue(input.validate());
        assertEquals(100, input.getRequiredAmount());
        assertTrue(input.isPerTick());
    }

    @Test
    @DisplayName("JSONから正しく読み込める（perTick=false）")
    public void testJSON読み込み_perTickFalse() {
        JsonObject json = new JsonObject();
        json.addProperty("mana", 8000);
        json.addProperty("perTick", false);

        ManaInput input = ManaInput.fromJson(json);

        assertNotNull(input);
        assertEquals(8000, input.getRequiredAmount());
        assertFalse(input.isPerTick());
    }

    @Test
    @DisplayName("JSONに正しく書き込める")
    public void testJSON書き込み() {
        ManaInput input = new ManaInput(3000, true);

        JsonObject json = new JsonObject();
        input.write(json);

        assertTrue(json.has("mana"));
        assertEquals(
            3000,
            json.get("mana")
                .getAsInt());

        assertTrue(json.has("perTick"));
        assertTrue(
            json.get("perTick")
                .getAsBoolean());
    }

    // ========================================
    // バリデーションのテスト
    // ========================================

    @Test
    @DisplayName("正しいManaInputはvalidateがtrueを返す")
    public void test正しいInputのvalidate() {
        ManaInput validInput = new ManaInput(1000, false);
        assertTrue(validInput.validate());
    }

    @Test
    @DisplayName("マナ量が0以下の場合はvalidateがfalseを返す")
    public void test不正なInputのvalidate() {
        ManaInput invalidInput1 = new ManaInput(0, true);
        assertFalse(invalidInput1.validate());

        ManaInput invalidInput2 = new ManaInput(-500, false);
        assertFalse(invalidInput2.validate());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: consume() メソッドのテスト
    // TODO: Botania統合テスト
}
