package ruiseki.omoshiroikamo.api.structure.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

/**
 * TieredBlockMapping のユニットテスト
 * 
 * このテストは、特定のブロックIDが正しくTierに紐づけられるか、
 * およびシリアライズが正しく行われるかを検証します。
 */
@DisplayName("TieredBlockMapping のテスト")
public class TieredBlockMappingTest {

    @Test
    @DisplayName("Tierの取得と部品名の確認")
    public void testTierMapping() {
        Map<String, Integer> tiers = new HashMap<>();
        tiers.put("minecraft:stone", 1);
        tiers.put("minecraft:iron_block", 2);
        tiers.put("minecraft:gold_block", 3);

        TieredBlockMapping mapping = new TieredBlockMapping('S', "casing", tiers);

        assertEquals('S', mapping.getSymbol());
        assertEquals("casing", mapping.getComponentName());

        // 正常なマッピング
        assertEquals(1, mapping.getTier("minecraft:stone"));
        assertEquals(2, mapping.getTier("minecraft:iron_block"));
        assertEquals(3, mapping.getTier("minecraft:gold_block"));

        // 未登録のブロックは Tier 0
        assertEquals(0, mapping.getTier("minecraft:dirt"));
    }

    @Test
    @DisplayName("TierからブロックIDの逆引き")
    public void testReverseMapping() {
        Map<String, Integer> tiers = new HashMap<>();
        tiers.put("minecraft:stone", 1);
        tiers.put("minecraft:iron_block", 2);

        TieredBlockMapping mapping = new TieredBlockMapping('S', "casing", tiers);

        assertEquals("minecraft:stone", mapping.getBlockId(1));
        assertEquals("minecraft:iron_block", mapping.getBlockId(2));
        assertNull(mapping.getBlockId(3));
    }

    @Test
    @DisplayName("JSONシリアライズの検証")
    public void testSerialization() {
        Map<String, Integer> tiers = new HashMap<>();
        tiers.put("minecraft:stone", 1);

        TieredBlockMapping mapping = new TieredBlockMapping('T', "core", tiers);
        JsonObject json = mapping.serialize();

        assertEquals(
            "core",
            json.get("component")
                .getAsString());
        assertTrue(json.has("tiers"));

        JsonObject tiersObj = json.getAsJsonObject("tiers");
        assertEquals(
            1,
            tiersObj.get("minecraft:stone")
                .getAsInt());
    }
}
