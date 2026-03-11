package ruiseki.omoshiroikamo.api.structure.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonReader;

/**
 * JSON統合テスト
 *
 * ============================================
 * JSONからの構造読み込み機能をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - JSONパースは最も頻繁に使われる
 * - エラーで構造が正しく読み込まれない
 * - 不正なフォーマットでクラッシュ
 *
 * カバーする機能:
 * - 基本的なフィールドの読み込み
 * - Layersのパース
 * - Mappingsのパース
 * - Requirementsのパース
 *
 * ============================================
 */
@DisplayName("JSON統合テスト（★最重要★）")
public class JSONLoaderIntegrationTest {

    // ========================================
    // 1.1 基本的なJSONロード
    // ========================================

    @Test
    @DisplayName("【最重要】最小構成のJSONから構造を読み込む")
    public void testMinimalJSON() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test_structure");

        // 最小限のlayers
        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("AAA"));
        rows.add(new JsonPrimitive("AQA"));
        rows.add(new JsonPrimitive("AAA"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        // 最小限のmappings
        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertNotNull(entry);
        assertEquals("test_structure", entry.getName());
        assertEquals(
            1,
            entry.getLayers()
                .size());
        assertEquals(
            1,
            entry.getMappings()
                .size());
    }

    @Test
    @DisplayName("【最重要】displayName の読み込み")
    public void testDisplayName() {
        JsonObject json = createBasicStructureJson("test", "Test Display Name");

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals("Test Display Name", entry.getDisplayName());
    }

    @Test
    @DisplayName("【最重要】recipeGroup（単一文字列）の読み込み")
    public void testRecipeGroupSingle() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("recipeGroup", "basic_machines");

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertNotNull(entry.getRecipeGroup());
        assertEquals(
            1,
            entry.getRecipeGroup()
                .size());
        assertEquals(
            "basic_machines",
            entry.getRecipeGroup()
                .get(0));
    }

    @Test
    @DisplayName("【最重要】recipeGroup（配列）の読み込み")
    public void testRecipeGroupArray() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray groups = new JsonArray();
        groups.add(new JsonPrimitive("basic"));
        groups.add(new JsonPrimitive("advanced"));
        groups.add(new JsonPrimitive("multiblock"));
        json.add("recipeGroup", groups);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            3,
            entry.getRecipeGroup()
                .size());
        assertEquals(
            "basic",
            entry.getRecipeGroup()
                .get(0));
        assertEquals(
            "advanced",
            entry.getRecipeGroup()
                .get(1));
        assertEquals(
            "multiblock",
            entry.getRecipeGroup()
                .get(2));
    }

    @Test
    @DisplayName("【最重要】controllerOffset の読み込み")
    public void testControllerOffset() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray offset = new JsonArray();
        offset.add(new JsonPrimitive(1));
        offset.add(new JsonPrimitive(2));
        offset.add(new JsonPrimitive(3));
        json.add("controllerOffset", offset);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        int[] result = entry.getControllerOffset();
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(1, result[0]);
        assertEquals(2, result[1]);
        assertEquals(3, result[2]);
    }

    @Test
    @DisplayName("【最重要】tintColor の読み込み")
    public void testTintColor() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("tintColor", "#FF0000");

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals("#FF0000", entry.getTintColor());
    }

    @Test
    @DisplayName("【最重要】tier の読み込み")
    public void testTier() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("tier", 5);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(5, entry.getTier());
    }

    @Test
    @DisplayName("【最重要】defaultFacing の読み込み")
    public void testDefaultFacing() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("defaultFacing", "SOUTH");

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals("SOUTH", entry.getDefaultFacing());
    }

    @Test
    @DisplayName("【最重要】複数構造を含む配列JSONの読み込み")
    public void testMultipleStructuresArray() {
        JsonArray array = new JsonArray();
        array.add(createBasicStructureJson("structure1", "First"));
        array.add(createBasicStructureJson("structure2", "Second"));
        array.add(createBasicStructureJson("structure3", "Third"));

        StructureJsonReader.FileData data = StructureJsonReader.readFile(array);

        assertEquals(3, data.structures.size());
        assertTrue(data.structures.containsKey("structure1"));
        assertTrue(data.structures.containsKey("structure2"));
        assertTrue(data.structures.containsKey("structure3"));
    }

    @Test
    @DisplayName("【最重要】default mappings の読み込み")
    public void testDefaultMappings() {
        JsonArray array = new JsonArray();

        // default mappings
        JsonObject defaultObj = new JsonObject();
        defaultObj.addProperty("name", "default");
        JsonObject mappings = new JsonObject();
        mappings.addProperty("X", "minecraft:iron_block");
        mappings.addProperty("Y", "minecraft:gold_block");
        defaultObj.add("mappings", mappings);
        array.add(defaultObj);

        // actual structure
        array.add(createBasicStructureJson("test", null));

        StructureJsonReader.FileData data = StructureJsonReader.readFile(array);

        assertEquals(2, data.defaultMappings.size());
        assertTrue(data.defaultMappings.containsKey('X'));
        assertTrue(data.defaultMappings.containsKey('Y'));
    }

    // ========================================
    // 1.2 Layer パース
    // ========================================

    @Test
    @DisplayName("【最重要】単一レイヤー")
    public void testSingleLayer() {
        JsonObject json = createBasicStructureJson("test", null);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("【最重要】複数レイヤー（3層）")
    public void test3Layers() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "multi_layer");

        JsonArray layers = new JsonArray();
        for (int i = 0; i < 3; i++) {
            JsonObject layer = new JsonObject();
            layer.addProperty("name", "y" + i);
            JsonArray rows = new JsonArray();
            rows.add(new JsonPrimitive("AAA"));
            rows.add(new JsonPrimitive("AQA"));
            rows.add(new JsonPrimitive("AAA"));
            layer.add("rows", rows);
            layers.add(layer);
        }
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            3,
            entry.getLayers()
                .size());
        assertEquals(
            "y0",
            entry.getLayers()
                .get(0)
                .getName());
        assertEquals(
            "y1",
            entry.getLayers()
                .get(1)
                .getName());
        assertEquals(
            "y2",
            entry.getLayers()
                .get(2)
                .getName());
    }

    @Test
    @DisplayName("【最重要】複数レイヤー（5層）")
    public void test5Layers() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "tall_structure");

        JsonArray layers = new JsonArray();
        for (int i = 0; i < 5; i++) {
            JsonObject layer = new JsonObject();
            layer.addProperty("name", "layer_" + i);
            JsonArray rows = new JsonArray();
            rows.add(new JsonPrimitive("BB"));
            rows.add(new JsonPrimitive("BB"));
            layer.add("rows", rows);
            layers.add(layer);
        }
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("B", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            5,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("【最重要】レイヤー名付き（新形式）")
    public void testLayerWithName() {
        JsonObject json = createBasicStructureJson("test", null);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "y0",
            entry.getLayers()
                .get(0)
                .getName());
    }

    @Test
    @DisplayName("【最重要】レイヤー名なし（旧形式）")
    public void testLayerWithoutName_OldFormat() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "old_format");

        JsonArray layers = new JsonArray();
        JsonArray layer1 = new JsonArray();
        layer1.add(new JsonPrimitive("AAA"));
        layer1.add(new JsonPrimitive("AQA"));
        layer1.add(new JsonPrimitive("AAA"));
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getLayers()
                .size());
        // 旧形式では名前が自動生成される
        assertNotNull(
            entry.getLayers()
                .get(0)
                .getName());
    }

    @Test
    @DisplayName("空rows配列")
    public void testEmptyRows() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "empty_rows");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        layer1.add("rows", new JsonArray()); // 空の配列
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getLayers()
                .size());
        assertEquals(
            0,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
    }

    @Test
    @DisplayName("1文字の行")
    public void testSingleCharacterRow() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "single_char");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("Q"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        json.add("mappings", new JsonObject());

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            "Q",
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0));
    }

    @Test
    @DisplayName("長い行（50文字以上）")
    public void testLongRow() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "long_row");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        // 60文字の行
        rows.add(new JsonPrimitive("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            60,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    // ========================================
    // 1.3 Mapping パース
    // ========================================

    @Test
    @DisplayName("【最重要】単一ブロックマッピング（文字列形式）")
    public void testSingleBlockMapping_String() {
        JsonObject json = createBasicStructureJson("test", null);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        Map<Character, ISymbolMapping> mappings = entry.getMappings();
        assertTrue(mappings.containsKey('A'));
        ISymbolMapping mapping = mappings.get('A');
        assertTrue(mapping instanceof BlockMapping);
        assertEquals("minecraft:stone", ((BlockMapping) mapping).getBlockId());
    }

    @Test
    @DisplayName("【最重要】オブジェクト形式（\"block\": \"...\"）")
    public void testBlockMapping_Object() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("BBB"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        JsonObject bMapping = new JsonObject();
        bMapping.addProperty("block", "minecraft:iron_block");
        mappings.add("B", bMapping);
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        ISymbolMapping mapping = entry.getMappings()
            .get('B');
        assertTrue(mapping instanceof BlockMapping);
        assertEquals("minecraft:iron_block", ((BlockMapping) mapping).getBlockId());
    }

    @Test
    @DisplayName("【最重要】複数ブロックマッピング（\"blocks\": [...]）")
    public void testMultipleBlocksMapping() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("CCC"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        JsonObject cMapping = new JsonObject();
        JsonArray blocks = new JsonArray();
        blocks.add(new JsonPrimitive("minecraft:stone"));
        blocks.add(new JsonPrimitive("minecraft:cobblestone"));
        blocks.add(new JsonPrimitive("minecraft:mossy_cobblestone"));
        cMapping.add("blocks", blocks);
        mappings.add("C", cMapping);
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        ISymbolMapping mapping = entry.getMappings()
            .get('C');
        assertTrue(mapping instanceof BlockMapping);
        List<String> blockList = ((BlockMapping) mapping).getBlockIds();
        assertNotNull(blockList);
        assertEquals(3, blockList.size());
        assertEquals("minecraft:stone", blockList.get(0));
        assertEquals("minecraft:cobblestone", blockList.get(1));
        assertEquals("minecraft:mossy_cobblestone", blockList.get(2));
    }

    @Test
    @DisplayName("数字シンボル（'0'-'9'）")
    public void testNumericSymbols() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("123"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("1", "minecraft:stone");
        mappings.addProperty("2", "minecraft:iron_block");
        mappings.addProperty("3", "minecraft:gold_block");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            3,
            entry.getMappings()
                .size());
        assertTrue(
            entry.getMappings()
                .containsKey('1'));
        assertTrue(
            entry.getMappings()
                .containsKey('2'));
        assertTrue(
            entry.getMappings()
                .containsKey('3'));
    }

    @Test
    @DisplayName("大文字・小文字の区別")
    public void testCaseSensitiveSymbols() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("AaQ"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        mappings.addProperty("a", "minecraft:cobblestone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            2,
            entry.getMappings()
                .size());
        assertNotEquals(
            ((BlockMapping) entry.getMappings()
                .get('A')).getBlockId(),
            ((BlockMapping) entry.getMappings()
                .get('a')).getBlockId());
    }

    @Test
    @DisplayName("空のmappings")
    public void testEmptyMappings() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("___"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        json.add("mappings", new JsonObject());

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            0,
            entry.getMappings()
                .size());
    }

    @Test
    @DisplayName("マッピング大量（26個）")
    public void testManyMappings() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("ABC"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        for (char c = 'A'; c <= 'Z'; c++) {
            mappings.addProperty(String.valueOf(c), "minecraft:stone");
        }
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            26,
            entry.getMappings()
                .size());
    }

    @Test
    @DisplayName("metadata付きブロックID")
    public void testBlockIdWithMetadata() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("W"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("W", "minecraft:wool:5");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        ISymbolMapping mapping = entry.getMappings()
            .get('W');
        assertEquals("minecraft:wool:5", ((BlockMapping) mapping).getBlockId());
    }

    @Test
    @DisplayName("特殊シンボル（'Q', '_', ' '）の処理")
    public void testSpecialSymbols() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "special_symbols");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("A_A"));
        rows.add(new JsonPrimitive(" Q "));
        rows.add(new JsonPrimitive("A_A"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        // Q, _, スペース は特殊シンボル
        assertNotNull(entry);
        assertEquals("special_symbols", entry.getName());
    }

    @Test
    @DisplayName("重複シンボル（後の値で上書き）")
    public void testDuplicateSymbol() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "duplicate");

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("AAA"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        mappings.addProperty("A", "minecraft:iron_block"); // 重複

        json.add("mappings", mappings);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        // JSONの仕様上、後の値で上書きされる
        ISymbolMapping mapping = entry.getMappings()
            .get('A');
        assertEquals("minecraft:iron_block", ((BlockMapping) mapping).getBlockId());
    }

    // ========================================
    // 1.4 Requirement パース
    // ========================================

    @Test
    @DisplayName("【最重要】itemInput の読み込み")
    public void testRequirement_ItemInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "itemInput");
        req.addProperty("min", 1);
        req.addProperty("max", 4);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getRequirements()
                .size());
        IStructureRequirement requirement = entry.getRequirements()
            .get(0);
        assertEquals("itemInput", requirement.getType());
        assertEquals(1, requirement.getMinCount());
        assertEquals(4, requirement.getMaxCount());
    }

    @Test
    @DisplayName("【最重要】fluidOutput の読み込み")
    public void testRequirement_FluidOutput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "fluidOutput");
        req.addProperty("min", 2);
        req.addProperty("max", 8);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            1,
            entry.getRequirements()
                .size());
        assertEquals(
            "fluidOutput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】energyInput の読み込み")
    public void testRequirement_EnergyInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "energyInput");
        req.addProperty("min", 1);
        req.addProperty("max", 1);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "energyInput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】manaInput の読み込み")
    public void testRequirement_ManaInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "manaInput");
        req.addProperty("min", 1);
        req.addProperty("max", 2);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "manaInput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】gasInput の読み込み")
    public void testRequirement_GasInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "gasInput");
        req.addProperty("min", 0);
        req.addProperty("max", 1);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "gasInput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】essentiaInput の読み込み")
    public void testRequirement_EssentiaInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "essentiaInput");
        req.addProperty("min", 1);
        req.addProperty("max", 3);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "essentiaInput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】visInput の読み込み")
    public void testRequirement_VisInput() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "visInput");
        req.addProperty("min", 2);
        req.addProperty("max", 4);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            "visInput",
            entry.getRequirements()
                .get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】min/max の読み込み")
    public void testRequirement_MinMax() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "itemInput");
        req.addProperty("min", 10);
        req.addProperty("max", 50);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        IStructureRequirement requirement = entry.getRequirements()
            .get(0);
        assertEquals(10, requirement.getMinCount());
        assertEquals(50, requirement.getMaxCount());
    }

    @Test
    @DisplayName("【最重要】複数 requirements の配列")
    public void testMultipleRequirements() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();

        JsonObject req1 = new JsonObject();
        req1.addProperty("type", "itemInput");
        req1.addProperty("min", 2);
        req1.addProperty("max", 8);
        reqs.add(req1);

        JsonObject req2 = new JsonObject();
        req2.addProperty("type", "fluidInput");
        req2.addProperty("min", 1);
        req2.addProperty("max", 2);
        reqs.add(req2);

        JsonObject req3 = new JsonObject();
        req3.addProperty("type", "energyInput");
        req3.addProperty("min", 1);
        req3.addProperty("max", 1);
        reqs.add(req3);

        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertEquals(
            3,
            entry.getRequirements()
                .size());
        assertEquals(
            "itemInput",
            entry.getRequirements()
                .get(0)
                .getType());
        assertEquals(
            "fluidInput",
            entry.getRequirements()
                .get(1)
                .getType());
        assertEquals(
            "energyInput",
            entry.getRequirements()
                .get(2)
                .getType());
    }

    @Test
    @DisplayName("【エラー】不正な type でnullが返る")
    public void testInvalidRequirementType() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray reqs = new JsonArray();
        JsonObject req = new JsonObject();
        req.addProperty("type", "invalidType");
        req.addProperty("min", 1);
        req.addProperty("max", 1);
        reqs.add(req);
        json.add("requirements", reqs);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        // 不正なtypeはnullが返り、追加されない
        assertEquals(
            0,
            entry.getRequirements()
                .size());
    }

    // ========================================
    // 1.5 エラーハンドリング
    // ========================================

    @Test
    @DisplayName("【エラー】controllerOffset が2要素しかない")
    public void testControllerOffset_Only2Elements() {
        JsonObject json = createBasicStructureJson("test", null);
        JsonArray offset = new JsonArray();
        offset.add(new JsonPrimitive(1));
        offset.add(new JsonPrimitive(2));
        // 3要素必要だが2要素しかない
        json.add("controllerOffset", offset);

        IStructureEntry entry = StructureJsonReader.readEntry(json);

        // 2要素しかないので無視される（nullまたはデフォルト値）
        int[] result = entry.getControllerOffset();
        // 実装次第でnullまたは要素数チェックで無視
        if (result != null) {
            assertTrue(result.length < 3 || result[0] == 0);
        }
    }

    @Test
    @DisplayName("【エラー】tier が文字列（パースエラー）")
    public void testTier_StringValue() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("tier", "high"); // 数値でなく文字列

        // JsonObject.getAsInt() が例外を投げる可能性
        assertThrows(Exception.class, () -> { StructureJsonReader.readEntry(json); });
    }

    @Test
    @DisplayName("未知のフィールドは無視される")
    public void testUnknownFieldsIgnored() {
        JsonObject json = createBasicStructureJson("test", null);
        json.addProperty("unknownField1", "value1");
        json.addProperty("unknownField2", 123);
        json.addProperty("unknownField3", true);

        // エラーにならずに読み込める
        IStructureEntry entry = StructureJsonReader.readEntry(json);

        assertNotNull(entry);
        assertEquals("test", entry.getName());
    }

    @Test
    @DisplayName("【エラー】name フィールド欠落")
    public void testMissingNameField() {
        JsonObject json = new JsonObject();
        // name を設定しない

        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("Q"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        json.add("mappings", new JsonObject());

        // nameがないためエラー
        assertThrows(Exception.class, () -> { StructureJsonReader.readEntry(json); });
    }

    @Test
    @DisplayName("【エラー】layers フィールド欠落")
    public void testMissingLayersField() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "no_layers");
        // layersを設定しない

        json.add("mappings", new JsonObject());

        // JSONパース自体は成功するが、layersがないため空のリストになる
        IStructureEntry entry = StructureJsonReader.readEntry(json);
        assertNotNull(entry);
        assertEquals("no_layers", entry.getName());

        // 後のバリデーションでエラーになることを確認
        assertTrue(
            entry.getLayers()
                .isEmpty(),
            "layers がない場合は空リストになる");
    }

    @Test
    @DisplayName("【エラー】空のJSON")
    public void testEmptyJson() {
        JsonObject json = new JsonObject();
        // 何も設定しない

        // 必須フィールドがないためエラー
        assertThrows(Exception.class, () -> { StructureJsonReader.readEntry(json); });
    }

    @Test
    @DisplayName("【エラー】不正なJSON構文（null）")
    public void testInvalidJsonNull() {
        // nullを渡した場合
        assertThrows(Exception.class, () -> { StructureJsonReader.readEntry(null); });
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private JsonObject createBasicStructureJson(String name, String displayName) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        if (displayName != null) {
            json.addProperty("displayName", displayName);
        }

        // 基本的な1層構造
        JsonArray layers = new JsonArray();
        JsonObject layer1 = new JsonObject();
        layer1.addProperty("name", "y0");
        JsonArray rows = new JsonArray();
        rows.add(new JsonPrimitive("AAA"));
        rows.add(new JsonPrimitive("AQA"));
        rows.add(new JsonPrimitive("AAA"));
        layer1.add("rows", rows);
        layers.add(layer1);
        json.add("layers", layers);

        // 基本的なマッピング
        JsonObject mappings = new JsonObject();
        mappings.addProperty("A", "minecraft:stone");
        json.add("mappings", mappings);

        return json;
    }
}
