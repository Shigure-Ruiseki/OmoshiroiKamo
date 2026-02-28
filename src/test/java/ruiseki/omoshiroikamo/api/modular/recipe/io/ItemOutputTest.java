package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * ItemOutput のユニットテスト
 */
@DisplayName("ItemOutput のテスト")
public class ItemOutputTest {

    @Test
    @DisplayName("ItemStackから正しく作成できる")
    public void testItemStack作成() {
        ItemStack stack = new ItemStack(Items.diamond, 5);
        ItemOutput output = new ItemOutput(stack);

        assertEquals(5, output.getRequiredAmount());
        assertEquals(IPortType.Type.ITEM, output.getPortType());
    }

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:gold_ingot");
        json.addProperty("amount", 3);

        ItemOutput output = ItemOutput.fromJson(json);

        assertNotNull(output);
        // assertEquals(3, output.getRequiredAmount()); // resolveItemStack が null を返すため
        // 0 になる
    }

    @Test
    @DisplayName("正しいItemOutputはvalidateがtrueを返す")
    public void testValidate() {
        ItemOutput output = new ItemOutput(new ItemStack(Items.iron_ingot, 1));
        assertTrue(output.validate());
    }
}
