package ruiseki.omoshiroikamo.api.structure.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStreamReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.TierStructureRef;

@DisplayName("StructureJsonReader の Tier 構造体パーステスト")
public class StructureJsonReaderTierTest {

    @Test
    @DisplayName("複数スロット・アンカー方式のパース検証 (GodForge型)")
    public void testParseTierStructures() {
        JsonParser parser = new JsonParser();
        JsonElement json = parser
            .parse(new InputStreamReader(getClass().getResourceAsStream("/structures/with_tier_structures.json")));

        StructureJsonReader.FileData data = StructureJsonReader.readFile(json);

        IStructureEntry godforge = data.structures.get("godforge_like_machine");
        assertNotNull(godforge, "godforge_like_machine should be parsed");

        List<TierStructureRef> tierRefs = godforge.getTierStructures();
        assertEquals(1, tierRefs.size(), "Should have 1 tier structure reference");

        TierStructureRef ref = tierRefs.get(0);
        assertEquals("module_headless", ref.getStructureName());
        assertEquals("module_count", ref.getComponentName());
        assertTrue(ref.isCountMode(), "Should be in count mode");

        List<TierStructureRef.OffsetPair> offsets = ref.getOffsetPairs();
        assertEquals(2, offsets.size(), "Should have 2 offset pairs");

        // Check first offset: { "target": [3, 0, 0], "anchor": [1, 1, 0] }
        TierStructureRef.OffsetPair pair1 = offsets.get(0);
        assertArrayEquals(new int[] { 3, 0, 0 }, pair1.getTarget());
        assertArrayEquals(new int[] { 1, 1, 0 }, pair1.getAnchor());

        // Check second offset: { "target": [-3, 0, 0], "anchor": [1, 1, 0] }
        TierStructureRef.OffsetPair pair2 = offsets.get(1);
        assertArrayEquals(new int[] { -3, 0, 0 }, pair2.getTarget());
        assertArrayEquals(new int[] { 1, 1, 0 }, pair2.getAnchor());
    }

    @Test
    @DisplayName("基本的な Tier 構造体リストのパース検証")
    public void testParseBasicTierMachine() {
        JsonParser parser = new JsonParser();
        JsonElement json = parser
            .parse(new InputStreamReader(getClass().getResourceAsStream("/structures/with_tier_structures.json")));

        StructureJsonReader.FileData data = StructureJsonReader.readFile(json);

        IStructureEntry tierMachine = data.structures.get("tier_machine");
        assertNotNull(tierMachine);

        List<TierStructureRef> tierRefs = tierMachine.getTierStructures();
        assertEquals(2, tierRefs.size());

        // Tier 2
        TierStructureRef t2 = tierRefs.get(0);
        assertEquals("tier_machine_tier2", t2.getStructureName());
        assertEquals(2, t2.getTier());
        assertEquals("structure", t2.getComponentName());
        assertFalse(t2.isCountMode());

        // Tier 1
        TierStructureRef t1 = tierRefs.get(1);
        assertEquals("tier_machine_base", t1.getStructureName());
        assertEquals(1, t1.getTier());
    }
}
