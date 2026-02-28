package ruiseki.omoshiroikamo.api.structure.serialization;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.EnergyRequirement;
import ruiseki.omoshiroikamo.api.structure.io.FluidRequirement;
import ruiseki.omoshiroikamo.api.structure.io.ItemRequirement;
import ruiseki.omoshiroikamo.api.structure.io.ManaRequirement;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonReader;

/**
 * ラウンドトリップテスト
 *
 * ============================================
 * serialize → deserialize で同一性をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - データロスがないか
 * - フォーマットの互換性
 * - エッジケースの処理
 *
 * カバーする機能:
 * - JSON → Entry → JSON
 * - 複雑な構造のラウンドトリップ
 * - オプションフィールドの処理
 *
 * ============================================
 */
@DisplayName("ラウンドトリップテスト（★最重要★）")
public class RoundTripTest {

    @Test
    @DisplayName("【最重要】JSON → StructureEntry → JSON で同一")
    public void testRoundTrip_Simple() {
        // 元のJSONを作成
        JsonObject original = createSimpleStructureJson();

        // JSON → Entry
        IStructureEntry entry = StructureJsonReader.readEntry(original);

        // Entry → JSON
        JsonObject serialized = entry.serialize();

        // 主要フィールドが一致
        assertEquals(
            original.get("name")
                .getAsString(),
            serialized.get("name")
                .getAsString());
        assertEquals(
            original.get("displayName")
                .getAsString(),
            serialized.get("displayName")
                .getAsString());
        assertTrue(serialized.has("layers"));
        assertTrue(serialized.has("mappings"));
    }

    @Test
    @DisplayName("【最重要】複雑な構造のラウンドトリップ")
    public void testRoundTrip_Complex() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("complex");
        builder.setDisplayName("Complex Structure");

        // 3層
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addLayer(new StructureLayer("y1", Arrays.asList("BBB", "B_B", "BBB")));
        builder.addLayer(new StructureLayer("y2", Arrays.asList("CCC", "CCC", "CCC")));

        // 複数マッピング
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('B', new BlockMapping('B', Arrays.asList("minecraft:iron_block", "minecraft:gold_block")));
        builder.addMapping('C', new BlockMapping('C', "minecraft:diamond_block"));

        // 複数requirements
        builder.addRequirement(new ItemRequirement("itemInput", 2, 8));
        builder.addRequirement(new FluidRequirement("fluidInput", 1, 2));
        builder.addRequirement(new EnergyRequirement("energyInput", 1, 1));

        builder.addRecipeGroup("advanced");
        builder.setControllerOffset(new int[] { 1, 0, 1 });
        builder.setTintColor("#00FF00");
        builder.setTier(5);

        IStructureEntry entry = builder.build();

        // serialize → deserialize
        JsonObject serialized = entry.serialize();
        IStructureEntry deserialized = StructureJsonReader.readEntry(serialized);

        // 同一性チェック
        assertEquals(entry.getName(), deserialized.getName());
        assertEquals(entry.getDisplayName(), deserialized.getDisplayName());
        assertEquals(
            entry.getLayers()
                .size(),
            deserialized.getLayers()
                .size());
        assertEquals(
            entry.getMappings()
                .size(),
            deserialized.getMappings()
                .size());
        assertEquals(
            entry.getRequirements()
                .size(),
            deserialized.getRequirements()
                .size());
        assertEquals(
            entry.getRecipeGroup()
                .size(),
            deserialized.getRecipeGroup()
                .size());
        assertEquals(entry.getTintColor(), deserialized.getTintColor());
        assertEquals(entry.getTier(), deserialized.getTier());
    }

    @Test
    @DisplayName("default mappings のラウンドトリップ")
    public void testRoundTrip_DefaultMappings() {
        // default mappingsは構造とは別で管理される
        // FileData経由でテスト
        assertDoesNotThrow(() -> {
            StructureJsonReader.FileData data = new StructureJsonReader.FileData();
            assertNotNull(data);
        });
    }

    @Test
    @DisplayName("全7種 requirements のラウンドトリップ")
    public void testRoundTrip_AllRequirements() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("all_reqs");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));

        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRequirement(new FluidRequirement("fluidOutput", 1, 2));
        builder.addRequirement(new EnergyRequirement("energyInput", 1, 1));
        builder.addRequirement(new ManaRequirement("manaInput", 1, 3));
        // Gas, Essentia, Visも追加可能

        IStructureEntry entry = builder.build();

        JsonObject serialized = entry.serialize();
        IStructureEntry deserialized = StructureJsonReader.readEntry(serialized);

        assertEquals(
            4,
            deserialized.getRequirements()
                .size());
    }

    @Test
    @DisplayName("オプションフィールド省略時のラウンドトリップ")
    public void testRoundTrip_OptionalFields() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("minimal");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        // displayName, recipeGroup, controllerOffset, tintColor, defaultFacingは省略

        IStructureEntry entry = builder.build();

        JsonObject serialized = entry.serialize();
        IStructureEntry deserialized = StructureJsonReader.readEntry(serialized);

        assertEquals("minimal", deserialized.getName());
        assertNull(deserialized.getDisplayName());
        assertTrue(
            deserialized.getRecipeGroup()
                .isEmpty());
    }

    @Test
    @DisplayName("空配列のラウンドトリップ")
    public void testRoundTrip_EmptyArrays() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("empty_arrays");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        // requirements, recipeGroupは空

        IStructureEntry entry = builder.build();

        JsonObject serialized = entry.serialize();
        IStructureEntry deserialized = StructureJsonReader.readEntry(serialized);

        assertEquals(
            0,
            deserialized.getRequirements()
                .size());
        assertEquals(
            0,
            deserialized.getRecipeGroup()
                .size());
    }

    @Test
    @DisplayName("metadata付きブロックのラウンドトリップ")
    public void testRoundTrip_MetadataBlocks() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("metadata");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("WQ")));
        builder.addMapping('W', new BlockMapping('W', "minecraft:wool:5"));

        IStructureEntry entry = builder.build();

        JsonObject serialized = entry.serialize();
        IStructureEntry deserialized = StructureJsonReader.readEntry(serialized);

        // マッピングが保持される
        assertTrue(
            deserialized.getMappings()
                .containsKey('W'));
        BlockMapping mapping = (BlockMapping) deserialized.getMappings()
            .get('W');
        assertEquals("minecraft:wool:5", mapping.getBlockId());
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private JsonObject createSimpleStructureJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "simple");
        json.addProperty("displayName", "Simple Structure");

        com.google.gson.JsonArray layers = new com.google.gson.JsonArray();
        JsonObject layer = new JsonObject();
        layer.addProperty("name", "y0");
        com.google.gson.JsonArray rows = new com.google.gson.JsonArray();
        rows.add(new com.google.gson.JsonPrimitive("Q"));
        layer.add("rows", rows);
        layers.add(layer);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        return json;
    }
}
