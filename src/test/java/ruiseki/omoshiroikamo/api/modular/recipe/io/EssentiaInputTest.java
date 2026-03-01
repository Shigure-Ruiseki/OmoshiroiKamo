package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * EssentiaInput のユニットテスト
 */
@DisplayName("EssentiaInput のテスト")
public class EssentiaInputTest {

    @Test
    @DisplayName("Essentia名と量から正しく作成できる")
    public void testEssentia作成() {
        EssentiaInput input = new EssentiaInput("ignis", 10);

        assertEquals(10, input.getRequiredAmount());
        assertEquals(IPortType.Type.ESSENTIA, input.getPortType());
    }

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("essentia", "aqua");
        json.addProperty("amount", 5);

        EssentiaInput input = EssentiaInput.fromJson(json);

        assertNotNull(input);
        assertEquals(5, input.getRequiredAmount());
    }

    @Test
    @DisplayName("正しいEssentiaInputはvalidateがtrueを返す")
    public void testValidate() {
        EssentiaInput input = new EssentiaInput("terra", 8);
        assertTrue(input.validate());
    }
}
