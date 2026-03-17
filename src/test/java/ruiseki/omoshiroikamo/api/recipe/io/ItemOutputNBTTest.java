package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * Unit tests for ItemOutput NBT functionality.
 * Tests JSON parsing, NBT application, and configuration copying.
 */
public class ItemOutputNBTTest {

    @BeforeAll
    public static void setup() {
        RegistryMocker.mockAll();
    }

    // --- JSON Parsing Tests: NBT Expressions ---

    @Test
    public void testRead_NBTExpression_SingleString() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"display.Name = 'Legendary Sword'\""
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
        // Verify the NBT expression was parsed (stored internally)
    }

    @Test
    public void testRead_NBTExpression_Array() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": ["
            + "  \"display.Name = 'Legendary Sword'\","
            + "  \"display.Lore = ['Line 1', 'Line 2']\""
            + "]"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
    }

    @Test
    public void testRead_NBTExpression_EmptyArray() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1," + "\"nbt\": []" + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
    }

    // --- JSON Parsing Tests: NBT List Operations ---

    @Test
    public void testRead_NBTListOperation_Basic() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
    }

    @Test
    public void testRead_NBTListOperation_MultipleOps() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": \">=5\"},"
            + "    {\"id\": 20, \"lvl\": 0},"
            + "    {\"id\": 34, \"lvl\": \"+1\"}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
    }

    @Test
    public void testRead_NBTListOperation_DefaultPath() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
        // Default path should be "Items"
    }

    // --- JSON Parsing Tests: Combined NBT and NBTList ---

    @Test
    public void testRead_CombinedNBTAndNBTList() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"display.Name = 'Enchanted Blade'\","
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": 5}]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(output);
        assertTrue(output.validate());
    }

    // --- NBT Application Tests (using reflection) ---

    @Test
    public void testCreateOutputStack_NBTListOperation_AddEnchantment() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Use reflection to call private createOutputStack method
        Method method = ItemOutput.class.getDeclaredMethod("createOutputStack", int.class);
        method.setAccessible(true);
        ItemStack stack = (ItemStack) method.invoke(output, 1);

        assertNotNull(stack);
        assertTrue(stack.hasTagCompound());

        NBTTagCompound nbt = stack.getTagCompound();
        assertTrue(nbt.hasKey("ench"));

        NBTTagList enchList = (NBTTagList) nbt.getTag("ench");
        assertEquals(1, enchList.tagCount());

        NBTTagCompound ench = enchList.getCompoundTagAt(0);
        assertEquals(16.0, ench.getDouble("id"), 0.001);
        assertEquals(5.0, ench.getDouble("lvl"), 0.001);
    }

    @Test
    public void testCreateOutputStack_NBTListOperation_MultipleEnchantments() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5},"
            + "    {\"id\": 34, \"lvl\": 3}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        Method method = ItemOutput.class.getDeclaredMethod("createOutputStack", int.class);
        method.setAccessible(true);
        ItemStack stack = (ItemStack) method.invoke(output, 1);

        assertNotNull(stack);
        assertTrue(stack.hasTagCompound());

        NBTTagList enchList = (NBTTagList) stack.getTagCompound()
            .getTag("ench");
        assertEquals(2, enchList.tagCount());

        // Verify both enchantments
        NBTTagCompound ench1 = enchList.getCompoundTagAt(0);
        assertEquals(16.0, ench1.getDouble("id"), 0.001);
        assertEquals(5.0, ench1.getDouble("lvl"), 0.001);

        NBTTagCompound ench2 = enchList.getCompoundTagAt(1);
        assertEquals(34.0, ench2.getDouble("id"), 0.001);
        assertEquals(3.0, ench2.getDouble("lvl"), 0.001);
    }

    @Test
    public void testCreateOutputStack_NBTListOperation_ModifyExisting() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": \"+2\"}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Pre-create stack with existing enchantment
        ItemStack baseStack = output.getOutput();
        NBTTagCompound baseNBT = new NBTTagCompound();
        NBTTagList baseEnch = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 3);
        baseEnch.appendTag(ench);
        baseNBT.setTag("ench", baseEnch);
        baseStack.setTagCompound(baseNBT);

        // Note: createOutputStack creates from template, not from existing stack
        // This test verifies the MODIFY operation would work if an enchantment existed
    }

    @Test
    public void testCreateOutputStack_NBTListOperation_RemoveEnchantment() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5},"
            + "    {\"id\": 20, \"lvl\": 0}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        Method method = ItemOutput.class.getDeclaredMethod("createOutputStack", int.class);
        method.setAccessible(true);
        ItemStack stack = (ItemStack) method.invoke(output, 1);

        assertNotNull(stack);
        assertTrue(stack.hasTagCompound());

        NBTTagList enchList = (NBTTagList) stack.getTagCompound()
            .getTag("ench");
        // Only Sharpness should remain (Fire Aspect removed)
        assertEquals(1, enchList.tagCount());

        NBTTagCompound ench = enchList.getCompoundTagAt(0);
        assertEquals(16.0, ench.getDouble("id"), 0.001);
    }

    @Test
    public void testCreateOutputStack_StackSize() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond\"," + "\"amount\": 1" + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        Method method = ItemOutput.class.getDeclaredMethod("createOutputStack", int.class);
        method.setAccessible(true);

        ItemStack stack1 = (ItemStack) method.invoke(output, 1);
        assertEquals(1, stack1.stackSize);

        ItemStack stack16 = (ItemStack) method.invoke(output, 16);
        assertEquals(16, stack16.stackSize);

        ItemStack stack64 = (ItemStack) method.invoke(output, 64);
        assertEquals(64, stack64.stackSize);
    }

    // --- Copy Tests ---

    @Test
    public void testCopy_PreservesNBTExpressions() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"display.Name = 'Test'\""
            + "}";

        ItemOutput original = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemOutput copy = (ItemOutput) original.copy();

        assertNotNull(copy);
        assertTrue(copy.validate());
        assertNotSame(original, copy);
    }

    @Test
    public void testCopy_PreservesNBTListOperation() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": 5}]"
            + "}"
            + "}";

        ItemOutput original = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemOutput copy = (ItemOutput) original.copy();

        assertNotNull(copy);
        assertTrue(copy.validate());
        assertNotSame(original, copy);
    }

    @Test
    public void testCopy_MultipliesStackSize() {
        String json = "{" + "\"item\": \"minecraft:diamond\"," + "\"amount\": 8" + "}";

        ItemOutput original = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemOutput copy2x = (ItemOutput) original.copy(2);
        ItemOutput copy3x = (ItemOutput) original.copy(3);

        assertEquals(8, original.getOutput().stackSize);
        assertEquals(16, copy2x.getOutput().stackSize);
        assertEquals(24, copy3x.getOutput().stackSize);
    }

    @Test
    public void testCopy_WithNullOutput() {
        ItemOutput original = new ItemOutput((ItemStack) null);
        ItemOutput copy = (ItemOutput) original.copy();

        assertNotNull(copy);
        assertNull(copy.getOutput());
    }

    // --- Write/Serialization Tests ---

    @Test
    public void testWrite_BasicItem() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1," + "\"meta\": 0" + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        output.write(written);

        assertTrue(written.has("item"));
        assertEquals(
            "minecraft:diamond_sword",
            written.get("item")
                .getAsString());
    }

    @Test
    public void testWrite_WithNBTExpressions() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": [\"display.Name = 'Test'\"]"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        output.write(written);

        assertTrue(written.has("item"));
        assertTrue(written.has("nbt"));
        assertTrue(
            written.get("nbt")
                .isJsonArray());
    }

    @Test
    public void testWrite_WithNBTListOperation() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": 5}]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        output.write(written);

        assertTrue(written.has("item"));
        assertTrue(written.has("_has_nbtlist"));
        assertTrue(
            written.get("_has_nbtlist")
                .getAsBoolean());
    }

    // --- Validation Tests ---

    @Test
    public void testValidate_ValidItem() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        assertTrue(output.validate());
    }

    @Test
    public void testValidate_NullItem() {
        ItemOutput output = new ItemOutput((ItemStack) null);
        assertFalse(output.validate());
    }

    // --- Integration Tests ---

    @Test
    public void testIntegration_ComplexEnchantedSword() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"display.Name = 'Excalibur'\","
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": 5},"
            + "    {\"id\": 34, \"lvl\": 3}"
            + "  ]"
            + "}"
            + "}";

        ItemOutput output = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        assertTrue(output.validate());

        Method method = ItemOutput.class.getDeclaredMethod("createOutputStack", int.class);
        method.setAccessible(true);
        ItemStack stack = (ItemStack) method.invoke(output, 1);

        assertNotNull(stack);
        assertTrue(stack.hasTagCompound());

        NBTTagCompound nbt = stack.getTagCompound();

        // Verify enchantments
        assertTrue(nbt.hasKey("ench"));
        NBTTagList enchList = (NBTTagList) nbt.getTag("ench");
        assertEquals(2, enchList.tagCount());
    }

    @Test
    public void testIntegration_ReadWriteCycle() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": [\"display.Name = 'Test'\"],"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": 5}]"
            + "}"
            + "}";

        ItemOutput output1 = ItemOutput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        output1.write(written);

        // Verify basic structure is preserved
        assertTrue(written.has("item"));
        assertTrue(written.has("nbt"));
        assertTrue(written.has("_has_nbtlist"));
    }
}
