package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * ManaOutput のユニットテスト（perTickフラグ対応）
 */
@DisplayName("ManaOutput のテスト")
public class ManaOutputTest {

    @Test
    @DisplayName("perTick=trueで正しく作成できる")
    public void testPerTickTrue() {
        ManaOutput output = new ManaOutput(200, true);

        assertEquals(200, output.getAmount());
        assertEquals(IPortType.Type.MANA, output.getPortType());
        assertTrue(output.isPerTick());
    }

    @Test
    @DisplayName("perTick=falseで正しく作成できる")
    public void testPerTickFalse() {
        ManaOutput output = new ManaOutput(5000, false);

        assertEquals(5000, output.getAmount());
        assertFalse(output.isPerTick());
    }

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("mana", 1000);
        json.addProperty("perTick", true);

        ManaOutput output = ManaOutput.fromJson(json);

        assertNotNull(output);
        assertEquals(1000, output.getAmount());
        assertTrue(output.isPerTick());
    }
}
