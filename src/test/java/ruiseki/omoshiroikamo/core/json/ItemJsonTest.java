package ruiseki.omoshiroikamo.core.json;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

public class ItemJsonTest {

    @Test
    @DisplayName("Should parse standard item name and amount")
    public void testReadStandard() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "minecraft:iron_ingot");
        json.addProperty("amount", 5);

        ItemJson item = new ItemJson();
        item.read(json);

        assertEquals("minecraft:iron_ingot", item.name);
        assertEquals(5, item.amount);
        assertEquals(0, item.meta);
    }

    @Test
    @DisplayName("Should parse item with meta in name (modid:item:meta)")
    public void testReadWithMetaInName() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:wool:14"); // Red wool

        ItemJson item = new ItemJson();
        item.read(json);

        assertEquals("minecraft:wool", item.name);
        assertEquals(14, item.meta);
    }

    @Test
    @DisplayName("Should parse item with explicit meta")
    public void testReadWithExplicitMeta() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "minecraft:dye");
        json.addProperty("meta", 15); // Bone meal

        ItemJson item = new ItemJson();
        item.read(json);

        assertEquals("minecraft:dye", item.name);
        assertEquals(15, item.meta);
    }

    @Test
    @DisplayName("Should parse OreDictionary entry")
    public void testReadOre() {
        JsonObject json = new JsonObject();
        json.addProperty("ore", "ingotIron");

        ItemJson item = new ItemJson();
        item.read(json);

        assertEquals("ingotIron", item.ore);
        assertNull(item.name);
    }

    @Test
    @DisplayName("Should parse legacy 'item' field")
    public void testReadLegacyField() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:stick");

        ItemJson item = new ItemJson();
        item.read(json);

        assertEquals("minecraft:stick", item.name);
    }

    @Test
    @DisplayName("Should parse item string format (id,amount,meta)")
    public void testParseItemString() {
        ItemJson item = ItemJson.parseItemString("minecraft:stone,16,1");

        assertNotNull(item);
        assertEquals("minecraft:stone", item.name);
        assertEquals(16, item.amount);
        assertEquals(1, item.meta);
    }

    @Test
    @DisplayName("Should parse ore string format (ore:name,amount)")
    public void testParseOreString() {
        ItemJson item = ItemJson.parseItemString("ore:ingotGold,10");

        assertNotNull(item);
        assertEquals("ingotGold", item.ore);
        assertEquals(10, item.amount);
    }

    @Test
    @DisplayName("Should handle missing meta in string format")
    public void testParseStringMissingMeta() {
        ItemJson item = ItemJson.parseItemString("minecraft:dirt,5");

        assertNotNull(item);
        assertEquals("minecraft:dirt", item.name);
        assertEquals(5, item.amount);
        assertEquals(0, item.meta);
    }

    @Test
    @DisplayName("Should validate correctly")
    public void testValidation() {
        ItemJson validName = new ItemJson();
        validName.name = "test";
        assertTrue(validName.validate());

        ItemJson validOre = new ItemJson();
        validOre.ore = "test";
        assertTrue(validOre.validate());

        ItemJson invalid = new ItemJson();
        assertFalse(invalid.validate());
    }
}
