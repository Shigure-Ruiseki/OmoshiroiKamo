package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * GasInput のユニットテスト（Mekanism統合用）
 */
@DisplayName("GasInput のテスト")
public class GasInputTest {

    @Test
    @DisplayName("Gas名と量から正しく作成できる")
    public void testGas作成() {
        GasInput input = new GasInput("oxygen", 500);

        assertEquals(500, input.getRequiredAmount());
        assertEquals(IPortType.Type.GAS, input.getPortType());
    }

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("gas", "hydrogen");
        json.addProperty("amount", 1000);

        GasInput input = GasInput.fromJson(json);

        assertNotNull(input);
        assertEquals(1000, input.getRequiredAmount());
    }

    @Test
    @DisplayName("正しいGasInputはvalidateがtrueを返す")
    public void testValidate() {
        GasInput input = new GasInput("oxygen", 250);
        assertTrue(input.validate());
    }
}
