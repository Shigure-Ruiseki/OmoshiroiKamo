package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.recipe.expression.NBTPattern.OperationType;

/**
 * Unit tests for NBTPattern and ValuePattern.
 * Tests operation auto-inference, pattern matching, and NBT modifications.
 */
public class NBTPatternTest {

    // --- Operation Auto-Inference Tests ---

    // --- Operation Auto-Inference Tests ---
    // These tests verify operation types via NBTPattern's getOperationType() method

    @Test
    public void testOperationInference_REQUIRE_GreaterThanOrEqual() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REQUIRE_GreaterThan() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \">5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REQUIRE_LessThan() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"<5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REQUIRE_LessThanOrEqual() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"<=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REQUIRE_Equal() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"==5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REQUIRE_NotEqual() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"!=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_REMOVE_Zero() {
        JsonObject json = new JsonParser().parse("{\"lvl\": 0}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.REMOVE, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_MODIFY_Plus() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"+5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.MODIFY, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_MODIFY_Minus() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"-3\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.MODIFY, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_SET_AbsoluteValue() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"10\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.SET, pattern.getOperationType());
    }

    @Test
    public void testOperationInference_SET_NumericValue() {
        JsonObject json = new JsonParser().parse("{\"lvl\": 42}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);
        assertEquals(OperationType.SET, pattern.getOperationType());
    }

    // --- Pattern Matching Tests (via NBTPattern.matches()) ---

    @Test
    public void testPattern_matches_GreaterThanOrEqual_True() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("lvl", 7);

        assertTrue(pattern.matches(nbt));
    }

    @Test
    public void testPattern_matches_GreaterThanOrEqual_False() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("lvl", 3);

        assertFalse(pattern.matches(nbt));
    }

    @Test
    public void testPattern_matches_LessThan_True() {
        JsonObject json = new JsonParser().parse("{\"durability\": \"<10\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("durability", 5);

        assertTrue(pattern.matches(nbt));
    }

    @Test
    public void testPattern_matches_Equal_True() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);

        assertTrue(pattern.matches(nbt));
    }

    @Test
    public void testPattern_matches_NotEqual_True() {
        JsonObject json = new JsonParser().parse("{\"id\": \"!=20\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);

        assertTrue(pattern.matches(nbt));
    }

    // --- Pattern Application Tests (via NBTPattern.apply()) ---

    @Test
    public void testPattern_apply_REMOVE() {
        JsonObject json = new JsonParser().parse("{\"toRemove\": 0}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("toRemove", 100);

        assertTrue(pattern.apply(nbt));
        assertFalse(nbt.hasKey("toRemove"));
    }

    @Test
    public void testPattern_apply_MODIFY_Add() {
        JsonObject json = new JsonParser().parse("{\"value\": \"+5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("value", 10);

        assertTrue(pattern.apply(nbt));
        assertEquals(15.0, nbt.getDouble("value"), 0.001);
    }

    @Test
    public void testPattern_apply_MODIFY_Subtract() {
        JsonObject json = new JsonParser().parse("{\"value\": \"-3\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("value", 10);

        assertTrue(pattern.apply(nbt));
        assertEquals(7.0, nbt.getDouble("value"), 0.001);
    }

    @Test
    public void testPattern_apply_SET_AbsoluteValue() {
        JsonObject json = new JsonParser().parse("{\"newValue\": \"42\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();

        assertTrue(pattern.apply(nbt));
        assertTrue(nbt.hasKey("newValue"));
        assertEquals(42.0, nbt.getDouble("newValue"), 0.001);
    }

    @Test
    public void testPattern_apply_SET_OverwriteExisting() {
        JsonObject json = new JsonParser().parse("{\"value\": 100}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("value", 50);

        assertTrue(pattern.apply(nbt));
        assertEquals(100.0, nbt.getDouble("value"), 0.001);
    }

    // --- NBTPattern.matches() Tests ---

    @Test
    public void testNBTPattern_matches_SingleKey() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);

        assertTrue(pattern.matches(nbt));
    }

    @Test
    public void testNBTPattern_matches_MultipleKeys_AllMatch() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\", \"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);
        nbt.setInteger("lvl", 7);

        assertTrue(pattern.matches(nbt));
    }

    @Test
    public void testNBTPattern_matches_MultipleKeys_OneMismatch() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\", \"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);
        nbt.setInteger("lvl", 3); // Doesn't meet >=5

        assertFalse(pattern.matches(nbt));
    }

    @Test
    public void testNBTPattern_matches_MissingKey() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        // Missing 'id' key

        assertFalse(pattern.matches(nbt));
    }

    // --- NBTPattern.apply() Tests ---

    @Test
    public void testNBTPattern_apply_SingleModification() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"+1\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 34);
        nbt.setInteger("lvl", 2);

        assertTrue(pattern.apply(nbt));
        assertEquals(3.0, nbt.getDouble("lvl"), 0.001);
    }

    @Test
    public void testNBTPattern_apply_MultipleModifications() {
        JsonObject json = new JsonParser().parse("{\"id\": 16, \"lvl\": 5}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();

        assertTrue(pattern.apply(nbt));
        assertEquals(16.0, nbt.getDouble("id"), 0.001);
        assertEquals(5.0, nbt.getDouble("lvl"), 0.001);
    }

    @Test
    public void testNBTPattern_apply_RemoveKey() {
        JsonObject json = new JsonParser().parse("{\"toRemove\": 0}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", 16);
        nbt.setInteger("toRemove", 999);

        assertTrue(pattern.apply(nbt));
        assertTrue(nbt.hasKey("id"));
        assertFalse(nbt.hasKey("toRemove"));
    }

    // --- NBTPattern.getOperationType() Tests ---

    @Test
    public void testNBTPattern_primaryOperation_REQUIRE() {
        JsonObject json = new JsonParser().parse("{\"id\": \"==16\", \"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        assertEquals(OperationType.REQUIRE, pattern.getOperationType());
    }

    @Test
    public void testNBTPattern_primaryOperation_REMOVE() {
        JsonObject json = new JsonParser().parse("{\"id\": 16, \"lvl\": 0}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        assertEquals(OperationType.REMOVE, pattern.getOperationType());
    }

    @Test
    public void testNBTPattern_primaryOperation_MODIFY() {
        JsonObject json = new JsonParser().parse("{\"lvl\": \"+1\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        assertEquals(OperationType.MODIFY, pattern.getOperationType());
    }

    @Test
    public void testNBTPattern_primaryOperation_SET() {
        JsonObject json = new JsonParser().parse("{\"id\": 16, \"lvl\": 5}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        assertEquals(OperationType.SET, pattern.getOperationType());
    }

    // --- Integration Tests ---

    @Test
    public void testEnchantmentPattern_SharpnessV_Require() {
        // Require Sharpness (id=16) level >= 5
        JsonObject json = new JsonParser().parse("{\"id\": 16, \"lvl\": \">=5\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        // Test matching enchantment
        NBTTagCompound enchMatch = new NBTTagCompound();
        enchMatch.setShort("id", (short) 16);
        enchMatch.setShort("lvl", (short) 5);
        assertTrue(pattern.matches(enchMatch));

        // Test non-matching enchantment (wrong level)
        NBTTagCompound enchNoMatch = new NBTTagCompound();
        enchNoMatch.setShort("id", (short) 16);
        enchNoMatch.setShort("lvl", (short) 3);
        assertFalse(pattern.matches(enchNoMatch));
    }

    @Test
    public void testEnchantmentPattern_RemoveFireAspect() {
        // Remove Fire Aspect (id=20)
        JsonObject json = new JsonParser().parse("{\"id\": 20, \"lvl\": 0}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 20);
        ench.setShort("lvl", (short) 2);

        assertTrue(pattern.matches(ench));
        assertTrue(pattern.apply(ench));
        assertFalse(ench.hasKey("lvl"));
    }

    @Test
    public void testEnchantmentPattern_IncrementUnbreaking() {
        // Increment Unbreaking (id=34) by +1
        JsonObject json = new JsonParser().parse("{\"id\": 34, \"lvl\": \"+1\"}")
            .getAsJsonObject();
        NBTPattern pattern = NBTPattern.fromJson(json);

        NBTTagCompound ench = new NBTTagCompound();
        ench.setShort("id", (short) 34);
        ench.setShort("lvl", (short) 2);

        assertTrue(pattern.matches(ench));
        assertTrue(pattern.apply(ench));
        assertEquals(3.0, ench.getDouble("lvl"), 0.001);
    }
}
