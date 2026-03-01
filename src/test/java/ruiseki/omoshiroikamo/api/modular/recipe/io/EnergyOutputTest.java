package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

@DisplayName("EnergyOutput のテスト")
public class EnergyOutputTest {

    @Test
    @DisplayName("perTick=trueで正しく作成できる")
    public void testPerTickTrue() {
        EnergyOutput output = new EnergyOutput(100, true);
        assertEquals(100, output.getRequiredAmount());
        assertEquals(IPortType.Type.ENERGY, output.getPortType());
        assertTrue(output.isPerTick(), "perTick フラグが true であるべき");
    }

    @Test
    @DisplayName("perTick=falseで正しく作成できる")
    public void testPerTickFalse() {
        EnergyOutput output = new EnergyOutput(10000, false);
        assertEquals(10000, output.getRequiredAmount());
        assertEquals(IPortType.Type.ENERGY, output.getPortType());
        assertFalse(output.isPerTick(), "perTick フラグが false であるべき");
    }

    @Test
    @DisplayName("デフォルトではperTick=trueになる")
    public void testデフォルトPerTick() {
        EnergyOutput output = new EnergyOutput(5000);
        assertEquals(5000, output.getRequiredAmount());
        assertTrue(output.isPerTick(), "デフォルトでは perTick=true");
    }

    @Test
    @DisplayName("JSONから正しく読み込める（perTick=true）")
    public void testJSON読み込み_perTickTrue() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 200);
        json.addProperty("perTick", true);

        EnergyOutput output = EnergyOutput.fromJson(json);

        assertNotNull(output);
        assertTrue(output.validate());
        assertEquals(200, output.getRequiredAmount());
        assertTrue(output.isPerTick());
    }

    @Test
    @DisplayName("JSONから読み込み時、perTickキーがなければtrue")
    public void testJSON読み込み_perTickなし() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 1000);

        EnergyOutput output = EnergyOutput.fromJson(json);

        assertNotNull(output);
        assertEquals(1000, output.getRequiredAmount());
        assertTrue(output.isPerTick(), "perTickキーがない場合はtrue");
    }

    @Test
    @DisplayName("JSONから読み込み時、pertick（小文字）でも正しく読み込める")
    public void testJSON読み込み_pertick小文字() {
        JsonObject json = new JsonObject();
        json.addProperty("energy", 500);
        json.addProperty("pertick", false);

        EnergyOutput output = EnergyOutput.fromJson(json);

        assertNotNull(output);
        assertEquals(500, output.getRequiredAmount());
        assertFalse(output.isPerTick(), "pertick（小文字）キーが認識されるべき");
    }

    @Test
    @DisplayName("エネルギー量が0以下の場合はvalidateがfalseを返す")
    public void testバリデーション_0以下() {
        // 0 エネルギー
        assertFalse(new EnergyOutput(0, true).validate(), "perTick=trueで0エネルギーは不正");
        assertFalse(new EnergyOutput(0, false).validate(), "perTick=falseで0エネルギーは不正");

        // 負のエネルギー
        assertFalse(new EnergyOutput(-100, true).validate(), "perTick=trueで負のエネルギーは不正");
        assertFalse(new EnergyOutput(-100, false).validate(), "perTick=falseで負のエネルギーは不正");
    }
}
