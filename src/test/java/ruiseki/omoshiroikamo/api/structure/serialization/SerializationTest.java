package ruiseki.omoshiroikamo.api.structure.serialization;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.FluidRequirement;
import ruiseki.omoshiroikamo.api.structure.io.ItemRequirement;

/**
 * シリアライゼーションのテスト
 *
 * ============================================
 * serialize() メソッドの動作をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - JSON出力の正確性
 * - フィールドの完全性
 * - フォーマットの一貫性
 *
 * カバーする機能:
 * - StructureEntry.serialize()
 * - StructureLayer.serialize()
 * - BlockMapping.serialize()
 * - Requirement.serialize()
 *
 * ============================================
 */
@DisplayName("シリアライゼーションのテスト")
public class SerializationTest {

    @Test
    @DisplayName("【最重要】StructureEntry.serialize() で完全なJSON生成")
    public void testStructureEntry_Serialize() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test_structure");
        builder.setDisplayName("Test Structure");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRecipeGroup("test");
        builder.setControllerOffset(new int[] { 1, 0, 1 });
        builder.setTintColor("#FF0000");
        builder.setTier(3);

        IStructureEntry entry = builder.build();

        JsonObject json = entry.serialize();

        assertNotNull(json);
        assertEquals(
            "test_structure",
            json.get("name")
                .getAsString());
        assertEquals(
            "Test Structure",
            json.get("displayName")
                .getAsString());
        assertTrue(json.has("layers"));
        assertTrue(json.has("mappings"));
        assertTrue(json.has("requirements"));
        assertEquals(
            "#FF0000",
            json.get("tintColor")
                .getAsString());
        assertEquals(
            3,
            json.get("tier")
                .getAsInt());
    }

    @Test
    @DisplayName("StructureLayer.serialize() のテスト")
    public void testStructureLayer_Serialize() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "BBB", "CCC"));

        JsonObject json = layer.serialize();

        assertNotNull(json);
        assertEquals(
            "y0",
            json.get("name")
                .getAsString());
        assertTrue(json.has("rows"));

        JsonArray rows = json.getAsJsonArray("rows");
        assertEquals(3, rows.size());
        assertEquals(
            "AAA",
            rows.get(0)
                .getAsString());
        assertEquals(
            "BBB",
            rows.get(1)
                .getAsString());
        assertEquals(
            "CCC",
            rows.get(2)
                .getAsString());
    }

    @Test
    @DisplayName("BlockMapping.serialize()（単一ブロック）")
    public void testBlockMapping_Serialize_Single() {
        BlockMapping mapping = new BlockMapping('A', "minecraft:stone");

        JsonObject json = mapping.serialize();

        assertNotNull(json);
        assertTrue(json.has("block"));
        assertEquals(
            "minecraft:stone",
            json.get("block")
                .getAsString());
        assertFalse(json.has("blocks"));
    }

    @Test
    @DisplayName("BlockMapping.serialize()（複数ブロック）")
    public void testBlockMapping_Serialize_Multiple() {
        BlockMapping mapping = new BlockMapping(
            'B',
            Arrays.asList("minecraft:stone", "minecraft:cobblestone", "minecraft:mossy_cobblestone"));

        JsonObject json = mapping.serialize();

        assertNotNull(json);
        assertTrue(json.has("blocks"));
        assertFalse(json.has("block"));

        JsonArray blocks = json.getAsJsonArray("blocks");
        assertEquals(3, blocks.size());
        assertEquals(
            "minecraft:stone",
            blocks.get(0)
                .getAsString());
        assertEquals(
            "minecraft:cobblestone",
            blocks.get(1)
                .getAsString());
        assertEquals(
            "minecraft:mossy_cobblestone",
            blocks.get(2)
                .getAsString());
    }

    @Test
    @DisplayName("ItemRequirement.serialize() のテスト")
    public void testItemRequirement_Serialize() {
        ItemRequirement req = new ItemRequirement("itemInput", 2, 8);

        JsonObject json = req.serialize();

        assertNotNull(json);
        assertEquals(
            "itemInput",
            json.get("type")
                .getAsString());
        assertEquals(
            2,
            json.get("min")
                .getAsInt());
        assertEquals(
            8,
            json.get("max")
                .getAsInt());
    }

    @Test
    @DisplayName("FluidRequirement.serialize() のテスト")
    public void testFluidRequirement_Serialize() {
        FluidRequirement req = new FluidRequirement("fluidOutput", 1, 2);

        JsonObject json = req.serialize();

        assertNotNull(json);
        assertEquals(
            "fluidOutput",
            json.get("type")
                .getAsString());
        assertEquals(
            1,
            json.get("min")
                .getAsInt());
        assertEquals(
            2,
            json.get("max")
                .getAsInt());
    }

    @Test
    @DisplayName("controllerOffset の配列化")
    public void testControllerOffset_Serialization() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("offset_test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setControllerOffset(new int[] { 1, 2, 3 });

        IStructureEntry entry = builder.build();

        JsonObject json = entry.serialize();

        assertTrue(json.has("controllerOffset"));
        JsonArray offset = json.getAsJsonArray("controllerOffset");
        assertEquals(3, offset.size());
        assertEquals(
            1,
            offset.get(0)
                .getAsInt());
        assertEquals(
            2,
            offset.get(1)
                .getAsInt());
        assertEquals(
            3,
            offset.get(2)
                .getAsInt());
    }

    @Test
    @DisplayName("recipeGroup の配列化")
    public void testRecipeGroup_Serialization() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("group_test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRecipeGroup("basic");
        builder.addRecipeGroup("advanced");
        builder.addRecipeGroup("multiblock");

        IStructureEntry entry = builder.build();

        JsonObject json = entry.serialize();

        assertTrue(json.has("recipeGroup"));
        JsonArray groups = json.getAsJsonArray("recipeGroup");
        assertEquals(3, groups.size());
        assertEquals(
            "basic",
            groups.get(0)
                .getAsString());
        assertEquals(
            "advanced",
            groups.get(1)
                .getAsString());
        assertEquals(
            "multiblock",
            groups.get(2)
                .getAsString());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: ラウンドトリップテスト（後のテストファイル）
}
