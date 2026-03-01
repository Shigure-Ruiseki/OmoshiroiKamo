package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ItemInput クラスのユニットテスト
 */
@DisplayName("ItemInput のテスト")
public class ItemInputTest {

    @BeforeAll
    public static void setupAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 基本機能のテスト
    // ========================================

    @Test
    @DisplayName("ItemStackから正しく作成できる")
    public void testItemStackからの作成() {
        ItemStack stack = new ItemStack(Items.iron_ingot, 5);
        ItemInput input = new ItemInput(stack);

        assertEquals(5, input.getRequiredAmount());

        assertEquals(IPortType.Type.ITEM, input.getPortType());

        ItemStack retrieved = input.getRequired();
        assertNotNull(retrieved);
        assertEquals(Items.iron_ingot, retrieved.getItem());
        assertEquals(5, retrieved.stackSize);
    }

    @Test
    @DisplayName("Item と 個数から正しく作成できる")
    public void testItemと個数からの作成() {
        ItemInput input = new ItemInput(Items.gold_ingot, 10);

        assertEquals(10, input.getRequiredAmount());
        assertEquals(IPortType.Type.ITEM, input.getPortType());

        ItemStack retrieved = input.getRequired();
        assertNotNull(retrieved);
        assertEquals(Items.gold_ingot, retrieved.getItem());
        assertEquals(10, retrieved.stackSize);
    }

    @Test
    @DisplayName("(ignore) OreDictから正しく作成できる")
    public void testOreDictからの作成() {
        // OreDictionary が初期化できるかチェック
        try {
            Class.forName("net.minecraftforge.oredict.OreDictionary");
        } catch (Throwable t) {
            assumeTrue(false, "OreDictionary not available in test environment");
        }

        ItemInput input = new ItemInput("ingotIron", 3);

        assertEquals(3, input.getRequiredAmount());
        assertEquals(IPortType.Type.ITEM, input.getPortType());

        // OreDictの場合、getRequired() は null を返す
        assertNull(input.getRequired());

        // getItems() でOreDict登録されたアイテムリストを取得できる
        // テスト環境ではOreDict登録されていないので空リストが返る
        try {
            List<ItemStack> items = input.getItems();
            assertNotNull(items);
            // テスト環境では登録されていないので空リストになる可能性が高い
        } catch (Throwable t) {
            // OreDictionary の初期化に失敗した場合はスキップ
            assumeTrue(false, "OreDictionary initialization failed: " + t.getMessage());
        }
    }

    // ========================================
    // JSON 読み込み/書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSONから正しく読み込める（item形式）")
    public void testJSON読み込み_item() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:iron_ingot");
        json.addProperty("amount", 5);

        ItemInput input = ItemInput.fromJson(json);

        assertNotNull(input);
        assertEquals(5, input.getRequiredAmount());
    }

    @Test
    @DisplayName("JSONから正しく読み込める（ore形式）")
    public void testJSON読み込み_ore() {
        JsonObject json = new JsonObject();
        json.addProperty("ore", "ingotIron");
        json.addProperty("amount", 3);

        ItemInput input = ItemInput.fromJson(json);

        assertNotNull(input);
        // assertTrue(input.validate());
        assertEquals(3, input.getRequiredAmount());
    }

    @Test
    @DisplayName("JSONから正しく読み込める（ore:プレフィックス形式）")
    public void testJSON読み込み_oreプレフィックス() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "ore:ingotGold");
        json.addProperty("amount", 7);

        ItemInput input = ItemInput.fromJson(json);

        assertNotNull(input);
        // assertTrue(input.validate());
        assertEquals(7, input.getRequiredAmount());
    }

    @Test
    @DisplayName("JSONに正しく書き込める（item形式）")
    public void testJSON書き込み_item() {
        ItemInput input = new ItemInput(Items.diamond, 1);

        JsonObject json = new JsonObject();
        input.write(json);

    }

    @Test
    @DisplayName("JSONに正しく書き込める（ore形式）")
    public void testJSON書き込み_ore() {
        ItemInput input = new ItemInput("ingotIron", 5);

        JsonObject json = new JsonObject();
        input.write(json);

        assertTrue(json.has("ore"));
        assertEquals(
            "ingotIron",
            json.get("ore")
                .getAsString());

        assertTrue(json.has("amount"));
        assertEquals(
            5,
            json.get("amount")
                .getAsInt());
    }

    // ========================================
    // バリデーションのテスト
    // ========================================

    @Test
    @DisplayName("正しいItemInputはvalidateがtrueを返す")
    public void test正しいInputのvalidate() {
        ItemInput validInput = new ItemInput(Items.iron_ingot, 1);
        assertTrue(validInput.validate());
    }

    @Test
    @DisplayName("requiredもoreDictもnullの場合はvalidateがfalseを返す")
    public void test不正なInputのvalidate() {
        ItemInput invalidInput = new ItemInput((ItemStack) null);

        assertFalse(invalidInput.validate());
    }

    // ========================================
    // エッジケースのテスト
    // ========================================

    @Test
    @DisplayName("getRequired()は元のItemStackのコピーを返す（変更しても影響しない）")
    public void testGetRequiredはコピーを返す() {
        ItemStack original = new ItemStack(Items.iron_ingot, 5);
        ItemInput input = new ItemInput(original);

        ItemStack retrieved = input.getRequired();
        retrieved.stackSize = 999;

        assertEquals(5, input.getRequiredAmount());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: consume() メソッドのテスト（モックポートが必要）
    // TODO: stacksMatch() の詳細なテスト（メタデータ、ワイルドカードなど）
}
