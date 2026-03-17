package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonParser;

/**
 * Unit tests for NBTListOperation.
 * Tests REQUIRE, REMOVE, MODIFY, and SET operations on NBT lists (e.g., enchantments).
 */
public class NBTListOperationTest {

    // --- matches() Tests (REQUIRE patterns) ---

    @Test
    public void testMatches_SingleRequire_Found() {
        // Require Sharpness V (id=16, lvl>=5)
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add Sharpness V
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertTrue(op.matches(nbt));
    }

    @Test
    public void testMatches_SingleRequire_NotFound() {
        // Require Sharpness V (id=16, lvl>=5)
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add Sharpness III (not enough level)
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 3);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertFalse(op.matches(nbt));
    }

    @Test
    public void testMatches_MultipleRequires_AllFound() {
        // Require both Sharpness V and Unbreaking III
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}, {\"id\": 34, \"lvl\": \">=3\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add Sharpness V
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        // Add Unbreaking III
        NBTTagCompound ench2 = new NBTTagCompound();
        ench2.setShort("id", (short) 34);
        ench2.setShort("lvl", (short) 3);
        enchList.appendTag(ench2);

        nbt.setTag("ench", enchList);

        assertTrue(op.matches(nbt));
    }

    @Test
    public void testMatches_MultipleRequires_OneMissing() {
        // Require both Sharpness V and Unbreaking III
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}, {\"id\": 34, \"lvl\": \">=3\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Only add Sharpness V
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertFalse(op.matches(nbt)); // Missing Unbreaking
    }

    @Test
    public void testMatches_EmptyList() {
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("ench", new NBTTagList());

        assertFalse(op.matches(nbt));
    }

    @Test
    public void testMatches_MissingPath() {
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \">=5\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        // No 'ench' path

        assertFalse(op.matches(nbt));
    }

    // --- apply() Tests (REMOVE operation) ---

    @Test
    public void testApply_Remove_SingleMatch() {
        // Remove Fire Aspect (id=20)
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 20, \"lvl\": 0}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add Sharpness V
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        // Add Fire Aspect II (to be removed)
        NBTTagCompound ench2 = new NBTTagCompound();
        ench2.setShort("id", (short) 20);
        ench2.setShort("lvl", (short) 2);
        enchList.appendTag(ench2);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(1, resultList.tagCount()); // Only Sharpness remains
        assertEquals(
            16,
            resultList.getCompoundTagAt(0)
                .getShort("id"));
    }

    @Test
    public void testApply_Remove_MultipleMatches() {
        // Remove all id=16 enchantments
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": 0}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add two Sharpness enchantments
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 3);
        enchList.appendTag(ench1);

        NBTTagCompound ench2 = new NBTTagCompound();
        ench2.setShort("id", (short) 16);
        ench2.setShort("lvl", (short) 5);
        enchList.appendTag(ench2);

        // Add Unbreaking
        NBTTagCompound ench3 = new NBTTagCompound();
        ench3.setShort("id", (short) 34);
        ench3.setShort("lvl", (short) 3);
        enchList.appendTag(ench3);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(1, resultList.tagCount()); // Only Unbreaking remains
        assertEquals(
            34,
            resultList.getCompoundTagAt(0)
                .getShort("id"));
    }

    @Test
    public void testApply_Remove_NoMatch() {
        // Try to remove id=99 (doesn't exist)
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 99, \"lvl\": 0}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertFalse(op.apply(nbt)); // No modifications

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(1, resultList.tagCount()); // Original remains
    }

    // --- apply() Tests (MODIFY operation) ---

    @Test
    public void testApply_Modify_IncrementLevel() {
        // Increment Unbreaking level by +1
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 34, \"lvl\": \"+1\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 34);
        ench1.setShort("lvl", (short) 2);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(
            3.0,
            resultList.getCompoundTagAt(0)
                .getDouble("lvl"),
            0.001);
    }

    @Test
    public void testApply_Modify_DecrementLevel() {
        // Decrement Sharpness level by -2
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": \"-2\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(
            3.0,
            resultList.getCompoundTagAt(0)
                .getDouble("lvl"),
            0.001);
    }

    // --- apply() Tests (SET operation) ---

    @Test
    public void testApply_Set_UpdateExisting() {
        // Set Sharpness level to 10
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": 10}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(
            10.0,
            resultList.getCompoundTagAt(0)
                .getDouble("lvl"),
            0.001);
    }

    @Test
    public void testApply_Set_AddNew() {
        // Add new Fire Aspect II
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 20, \"lvl\": 2}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Only Sharpness exists
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        nbt.setTag("ench", enchList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(2, resultList.tagCount()); // Sharpness + Fire Aspect

        // Find Fire Aspect
        boolean foundFireAspect = false;
        for (int i = 0; i < resultList.tagCount(); i++) {
            NBTTagCompound ench = resultList.getCompoundTagAt(i);
            if (ench.getShort("id") == 20) {
                foundFireAspect = true;
                assertEquals(2.0, ench.getDouble("lvl"), 0.001);
            }
        }
        assertTrue(foundFireAspect, "Fire Aspect should be added");
    }

    @Test
    public void testApply_Set_CreateListIfMissing() {
        // Add enchantment to non-existent list
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [{\"id\": 16, \"lvl\": 5}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        // No 'ench' list exists

        assertTrue(op.apply(nbt));

        assertTrue(nbt.hasKey("ench"));
        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(1, resultList.tagCount());
        assertEquals(
            16,
            resultList.getCompoundTagAt(0)
                .getShort("id"));
    }

    // --- Complex Integration Tests ---

    @Test
    public void testComplexEnchantmentManipulation() {
        // Require Sharpness III+, Remove Fire Aspect, Increment Unbreaking +1
        String json = "{\"nbtlist\": {\"path\": \"ench\", \"ops\": [" + "{\"id\": 16, \"lvl\": \">=3\"},"
            + "{\"id\": 20, \"lvl\": 0},"
            + "{\"id\": 34, \"lvl\": \"+1\"}"
            + "]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList enchList = new NBTTagList();

        // Add Sharpness V
        NBTTagCompound ench1 = new NBTTagCompound();
        ench1.setShort("id", (short) 16);
        ench1.setShort("lvl", (short) 5);
        enchList.appendTag(ench1);

        // Add Fire Aspect II (will be removed)
        NBTTagCompound ench2 = new NBTTagCompound();
        ench2.setShort("id", (short) 20);
        ench2.setShort("lvl", (short) 2);
        enchList.appendTag(ench2);

        // Add Unbreaking III (will be incremented to IV)
        NBTTagCompound ench3 = new NBTTagCompound();
        ench3.setShort("id", (short) 34);
        ench3.setShort("lvl", (short) 3);
        enchList.appendTag(ench3);

        nbt.setTag("ench", enchList);

        // Check matches
        assertTrue(op.matches(nbt), "Should match (has Sharpness >= 3)");

        // Apply modifications
        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("ench", 10);
        assertEquals(2, resultList.tagCount()); // Sharpness and Unbreaking remain

        // Verify Sharpness unchanged
        boolean foundSharpness = false;
        boolean foundUnbreaking = false;
        boolean foundFireAspect = false;

        for (int i = 0; i < resultList.tagCount(); i++) {
            NBTTagCompound ench = resultList.getCompoundTagAt(i);
            if (ench.getShort("id") == 16) {
                foundSharpness = true;
                assertEquals(5.0, ench.getDouble("lvl"), 0.001);
            } else if (ench.getShort("id") == 34) {
                foundUnbreaking = true;
                assertEquals(4.0, ench.getDouble("lvl"), 0.001); // Incremented
            } else if (ench.getShort("id") == 20) {
                foundFireAspect = true;
            }
        }

        assertTrue(foundSharpness, "Sharpness should remain");
        assertTrue(foundUnbreaking, "Unbreaking should remain and be incremented");
        assertFalse(foundFireAspect, "Fire Aspect should be removed");
    }

    @Test
    public void testCustomPath() {
        // Test with custom path (not 'ench')
        String json = "{\"nbtlist\": {\"path\": \"Items\", \"ops\": [{\"Slot\": 0, \"Count\": \"+1\"}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList itemsList = new NBTTagList();

        NBTTagCompound item = new NBTTagCompound();
        item.setByte("Slot", (byte) 0);
        item.setByte("Count", (byte) 5);
        itemsList.appendTag(item);

        nbt.setTag("Items", itemsList);

        assertTrue(op.apply(nbt));

        NBTTagList resultList = nbt.getTagList("Items", 10);
        assertEquals(
            6.0,
            resultList.getCompoundTagAt(0)
                .getDouble("Count"),
            0.001);
    }

    @Test
    public void testDefaultPath() {
        // Test default path (should be "Items" when not specified)
        String json = "{\"nbtlist\": {\"ops\": [{\"Slot\": 0, \"Count\": 10}]}}";
        NBTListOperation op = NBTListOperation.fromJson(
            new JsonParser().parse(json)
                .getAsJsonObject());

        assertEquals("Items", op.getPath());
    }
}
