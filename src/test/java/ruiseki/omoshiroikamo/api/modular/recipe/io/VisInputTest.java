package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * VisInput のユニットテスト（Thaumcraft統合用）
 */
@DisplayName("VisInput のテスト")
public class VisInputTest {

    @Test
    @DisplayName("Vis名と量から正しく作成できる")
    public void testVis作成() {
        VisInput input = new VisInput("aer", 50);

        assertEquals(50, input.getRequiredAmount());
        assertEquals(IPortType.Type.VIS, input.getPortType());
    }

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("vis", "ordo");
        json.addProperty("amount", 25);

        VisInput input = VisInput.fromJson(json);

        assertNotNull(input);
        assertEquals(25, input.getRequiredAmount());
    }

    @Test
    @DisplayName("正しいVisInputはvalidateがtrueを返す")
    public void testValidate() {
        VisInput input = new VisInput("perditio", 30);
        assertTrue(input.validate());
    }
}
