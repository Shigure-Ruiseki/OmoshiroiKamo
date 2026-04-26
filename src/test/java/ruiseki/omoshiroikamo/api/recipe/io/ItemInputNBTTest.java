package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * Unit tests for ItemInput NBT functionality.
 * Tests JSON parsing, NBT condition checking, NBT modification, and matching logic.
 */
public class ItemInputNBTTest {

    @BeforeAll
    public static void setup() {
        RegistryMocker.mockAll();
    }

    // --- JSON Parsing Tests: NBT Expressions ---

    @Test
    public void testRead_NBTExpression_SingleString() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"display.Name == 'Legendary Sword'\""
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
    }

    @Test
    public void testRead_NBTExpression_Array() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": ["
            + "  \"display.Name == 'Legendary Sword'\","
            + "  \"display.Lore[0] == 'Line 1'\""
            + "]"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
    }

    @Test
    public void testRead_NBTExpression_EmptyArray() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1," + "\"nbt\": []" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
    }

    // --- JSON Parsing Tests: NBT List Operations ---

    @Test
    public void testRead_NBTListOperation_Basic() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": \">=5\"}"
            + "  ]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
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

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
    }

    // --- JSON Parsing Tests: Combined NBT and NBTList ---

    @Test
    public void testRead_CombinedNBTAndNBTList() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": \"Damage < 100\","
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertNotNull(input);
        assertTrue(input.validate());
    }

    // --- NBT Condition Checking Tests (using reflection) ---

    @Test
    public void testCheckNBTConditions_NoConditions() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.diamond_sword);

        Method method = ItemInput.class
            .getDeclaredMethod("checkNBTConditions", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertTrue(result); // No conditions = always match
    }

    @Test
    public void testCheckNBTConditions_NoNBTOnItem_WithConditions() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.diamond_sword);
        // Stack has no NBT

        Method method = ItemInput.class
            .getDeclaredMethod("checkNBTConditions", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertFalse(result); // Has conditions but item has no NBT
    }

    @Test
    public void testCheckNBTConditions_NBTListMatch() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Sharpness V
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 5);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class
            .getDeclaredMethod("checkNBTConditions", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertTrue(result);
    }

    @Test
    public void testCheckNBTConditions_NBTListNoMatch() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Sharpness III (< 5)
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 3);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class
            .getDeclaredMethod("checkNBTConditions", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertFalse(result);
    }

    // --- NBT Modification Tests (using reflection) ---

    @Test
    public void testApplyNBTModifications_NoModifications() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.diamond_sword);

        Method method = ItemInput.class
            .getDeclaredMethod("applyNBTModifications", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        method.invoke(input, stack, null);

        // Should not crash and not add NBT
        assertNull(stack.getTagCompound());
    }

    @Test
    public void testApplyNBTModifications_NBTListOperation_AddEnchantment() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": 5}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.diamond_sword);

        Method method = ItemInput.class
            .getDeclaredMethod("applyNBTModifications", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        method.invoke(input, stack, null);

        assertNotNull(stack.getTagCompound());
        assertTrue(
            stack.getTagCompound()
                .hasKey("ench"));

        NBTTagList enchList = (NBTTagList) stack.getTagCompound()
            .getTag("ench");
        assertEquals(1, enchList.tagCount());

        NBTTagCompound ench = enchList.getCompoundTagAt(0);
        assertEquals(16.0, ench.getDouble("id"), 0.001);
        assertEquals(5.0, ench.getDouble("lvl"), 0.001);
    }

    @Test
    public void testApplyNBTModifications_NBTListOperation_ModifyEnchantment() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \"+1\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Sharpness III
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 3);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class
            .getDeclaredMethod("applyNBTModifications", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        method.invoke(input, stack, null);

        NBTTagList modifiedEnchList = (NBTTagList) stack.getTagCompound()
            .getTag("ench");
        NBTTagCompound modifiedEnch = modifiedEnchList.getCompoundTagAt(0);
        assertEquals(4.0, modifiedEnch.getDouble("lvl"), 0.001); // 3 + 1 = 4
    }

    @Test
    public void testApplyNBTModifications_NBTListOperation_RemoveEnchantment() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 20, \"lvl\": 0}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Fire Aspect II
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 20);
        ench.setShort("lvl", (short) 2);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class
            .getDeclaredMethod("applyNBTModifications", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        method.invoke(input, stack, null);

        NBTTagList modifiedEnchList = (NBTTagList) stack.getTagCompound()
            .getTag("ench");
        assertEquals(0, modifiedEnchList.tagCount()); // Fire Aspect removed
    }

    // --- Stack Matching Tests (using reflection) ---

    @Test
    public void testStacksMatch_BasicItem_NoNBT() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.diamond_sword);

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertTrue(result);
    }

    @Test
    public void testStacksMatch_WithNBTCondition_Match() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Sharpness V
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 5);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertTrue(result);
    }

    @Test
    public void testStacksMatch_WithNBTCondition_NoMatch() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        // Create stack with Sharpness III (< 5)
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();
        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 16);
        ench.setShort("lvl", (short) 3);
        enchList.appendTag(ench);
        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertFalse(result);
    }

    @Test
    public void testStacksMatch_WrongItem() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        ItemStack stack = new ItemStack(Items.iron_sword);

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertFalse(result);
    }

    @Test
    public void testStacksMatch_NullStack() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, (ItemStack) null, null);

        assertFalse(result);
    }

    // --- Write/Serialization Tests ---

    @Test
    public void testWrite_BasicItem() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        input.write(written);

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
            + "\"nbt\": [\"display.Name == 'Test'\"]"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        input.write(written);

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
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        input.write(written);

        assertTrue(written.has("item"));
        assertTrue(written.has("_has_nbtlist"));
        assertTrue(
            written.get("_has_nbtlist")
                .getAsBoolean());
    }

    @Test
    public void testWrite_ConsumeFlag() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1," + "\"consume\": false" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        input.write(written);

        assertTrue(written.has("consume"));
        assertFalse(
            written.get("consume")
                .getAsBoolean());
    }

    // --- Validation Tests ---

    @Test
    public void testValidate_ValidItem() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\"," + "\"amount\": 1" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        assertTrue(input.validate());
    }

    @Test
    public void testValidate_ValidOreDict() {
        String json = "{" + "\"ore\": \"ingotIron\"," + "\"amount\": 4" + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        assertTrue(input.validate());
    }

    @Test
    public void testValidate_NullItem() {
        ItemInput input = new ItemInput((ItemStack) null);
        assertFalse(input.validate());
    }

    // --- Integration Tests ---

    @Test
    public void testIntegration_ComplexEnchantmentRequirement() throws Exception {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": ["
            + "    {\"id\": 16, \"lvl\": \">=5\"},"
            + "    {\"id\": 34, \"lvl\": \">=3\"}"
            + "  ]"
            + "}"
            + "}";

        ItemInput input = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        assertTrue(input.validate());

        // Create stack with Sharpness V and Unbreaking III
        ItemStack stack = new ItemStack(Items.diamond_sword);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        NBTTagCompound ench2 = new NBTTagCompound();
        ench2.setShort("id", (short) 34);
        ench2.setShort("lvl", (short) 3);
        enchList.appendTag(ench2);

        nbt.setTag("ench", enchList);
        stack.setTagCompound(nbt);

        Method method = ItemInput.class.getDeclaredMethod("stacksMatch", ItemStack.class, ConditionContext.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(input, stack, null);

        assertTrue(result);
    }

    @Test
    public void testIntegration_ReadWriteCycle() {
        String json = "{" + "\"item\": \"minecraft:diamond_sword\","
            + "\"amount\": 1,"
            + "\"nbt\": [\"display.Name == 'Test'\"],"
            + "\"nbtlist\": {"
            + "  \"path\": \"ench\","
            + "  \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]"
            + "}"
            + "}";

        ItemInput input1 = ItemInput.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());
        JsonObject written = new JsonObject();
        input1.write(written);

        // Verify basic structure is preserved
        assertTrue(written.has("item"));
        assertTrue(written.has("nbt"));
        assertTrue(written.has("_has_nbtlist"));
    }
}
