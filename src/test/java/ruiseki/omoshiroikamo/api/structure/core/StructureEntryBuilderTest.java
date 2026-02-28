package ruiseki.omoshiroikamo.api.structure.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.io.EnergyRequirement;
import ruiseki.omoshiroikamo.api.structure.io.ItemRequirement;

/**
 * StructureEntryBuilder のテスト
 *
 * ============================================
 * Builderパターンの正しい動作をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - Null安全性の確認
 * - 必須フィールドのバリデーション
 * - Immutabilityの保証
 *
 * カバーする機能:
 * - 基本的なBuilder操作
 * - デフォルト値の確認
 * - 不変性のテスト
 *
 * ============================================
 */
@DisplayName("StructureEntryBuilder のテスト")
public class StructureEntryBuilderTest {

    // ========================================
    // 3.1 基本的なBuilder操作
    // ========================================

    @Test
    @DisplayName("【最重要】最小限のビルド（name + 1 layer + 1 mapping）")
    public void testMinimalBuild() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("minimal");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        assertNotNull(entry);
        assertEquals("minimal", entry.getName());
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
    @DisplayName("【最重要】全フィールド設定")
    public void testAllFieldsSet() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("full_config");
        builder.setDisplayName("Full Configuration Test");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRecipeGroup("test_group");
        builder.setControllerOffset(new int[] { 1, 2, 3 });
        builder.setTintColor("#FF0000");
        builder.setTier(5);
        builder.setDefaultFacing("SOUTH");

        IStructureEntry entry = builder.build();

        assertEquals("full_config", entry.getName());
        assertEquals("Full Configuration Test", entry.getDisplayName());
        assertEquals(
            1,
            entry.getLayers()
                .size());
        assertEquals(
            1,
            entry.getMappings()
                .size());
        assertEquals(
            1,
            entry.getRequirements()
                .size());
        assertEquals(
            1,
            entry.getRecipeGroup()
                .size());
        assertArrayEquals(new int[] { 1, 2, 3 }, entry.getControllerOffset());
        assertEquals("#FF0000", entry.getTintColor());
        assertEquals(5, entry.getTier());
        assertEquals("SOUTH", entry.getDefaultFacing());
    }

    @Test
    @DisplayName("addLayer を複数回呼び出し")
    public void testMultipleAddLayer() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multi_layer");

        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA")));
        builder.addLayer(new StructureLayer("y1", Arrays.asList("BBB")));
        builder.addLayer(new StructureLayer("y2", Arrays.asList("CCC")));

        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('B', new BlockMapping('B', "minecraft:iron_block"));
        builder.addMapping('C', new BlockMapping('C', "minecraft:gold_block"));

        IStructureEntry entry = builder.build();

        assertEquals(
            3,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("addMapping を複数回呼び出し")
    public void testMultipleAddMapping() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("ABCQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multi_mapping");
        builder.addLayer(layer);

        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('B', new BlockMapping('B', "minecraft:iron_block"));
        builder.addMapping('C', new BlockMapping('C', "minecraft:gold_block"));

        IStructureEntry entry = builder.build();

        assertEquals(
            3,
            entry.getMappings()
                .size());
    }

    @Test
    @DisplayName("addRequirement を複数回呼び出し")
    public void testMultipleAddRequirement() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multi_req");
        builder.addLayer(layer);

        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRequirement(new ItemRequirement("itemOutput", 1, 2));
        builder.addRequirement(new EnergyRequirement("energyInput", 1, 1));

        IStructureEntry entry = builder.build();

        assertEquals(
            3,
            entry.getRequirements()
                .size());
    }

    @Test
    @DisplayName("addRecipeGroup を複数回呼び出し")
    public void testMultipleAddRecipeGroup() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multi_group");
        builder.addLayer(layer);

        builder.addRecipeGroup("basic");
        builder.addRecipeGroup("advanced");
        builder.addRecipeGroup("multiblock");

        IStructureEntry entry = builder.build();

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
    @DisplayName("【エラー】setName(null) でエラー")
    public void testSetName_Null() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName(null);
        builder.addLayer(layer);

        // build()時にエラーが投げられる
        assertThrows(IllegalStateException.class, () -> { builder.build(); });
    }

    @Test
    @DisplayName("【エラー】build() 前に name が未設定でエラー")
    public void testBuild_WithoutName() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        // setName() を呼ばない
        builder.addLayer(layer);

        assertThrows(IllegalStateException.class, () -> { builder.build(); }, "Structure name must be set");
    }

    @Test
    @DisplayName("build() を2回呼んでも安全")
    public void testBuild_TwiceSafe() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("twice_build");
        builder.addLayer(layer);

        IStructureEntry entry1 = builder.build();
        IStructureEntry entry2 = builder.build();

        assertNotNull(entry1);
        assertNotNull(entry2);
        // 両方とも有効なエントリ
        assertEquals("twice_build", entry1.getName());
        assertEquals("twice_build", entry2.getName());
    }

    @Test
    @DisplayName("builder の再利用（同じbuilderで複数回build）")
    public void testBuilderReuse() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("reuse_test");
        builder.addLayer(layer);

        IStructureEntry entry1 = builder.build();

        // 再度build
        IStructureEntry entry2 = builder.build();

        assertEquals("reuse_test", entry1.getName());
        assertEquals("reuse_test", entry2.getName());
    }

    // ========================================
    // 3.2 Builderのデフォルト値
    // ========================================

    @Test
    @DisplayName("displayName 未設定時は null")
    public void testDisplayName_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_display");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNull(entry.getDisplayName());
    }

    @Test
    @DisplayName("recipeGroup 未設定時は空リスト")
    public void testRecipeGroup_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_group");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNotNull(entry.getRecipeGroup());
        assertEquals(
            0,
            entry.getRecipeGroup()
                .size());
    }

    @Test
    @DisplayName("controllerOffset 未設定時は null")
    public void testControllerOffset_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_offset");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNull(entry.getControllerOffset());
    }

    @Test
    @DisplayName("tintColor 未設定時は null")
    public void testTintColor_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_tint");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNull(entry.getTintColor());
    }

    @Test
    @DisplayName("tier 未設定時は 0")
    public void testTier_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_tier");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertEquals(0, entry.getTier());
    }

    @Test
    @DisplayName("defaultFacing 未設定時は null")
    public void testDefaultFacing_Default() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("default_facing");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNull(entry.getDefaultFacing());
    }

    @Test
    @DisplayName("layers 未追加時のbuild")
    public void testLayers_Empty() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("empty_layers");

        IStructureEntry entry = builder.build();

        assertNotNull(entry);
        assertEquals(
            0,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("mappings 未追加時のbuild")
    public void testMappings_Empty() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("empty_mappings");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        assertNotNull(entry);
        assertEquals(
            0,
            entry.getMappings()
                .size());
    }

    // ========================================
    // 3.3 Immutability テスト
    // ========================================

    @Test
    @DisplayName("build後にlayersを変更しても元のStructureEntryに影響しない")
    public void testImmutability_Layers() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("immutable_layers");

        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA")));
        builder.addLayer(new StructureLayer("y1", Arrays.asList("BBB")));

        IStructureEntry entry = builder.build();

        // build後にbuilderへ追加
        builder.addLayer(new StructureLayer("y2", Arrays.asList("CCC")));

        // 元のentryは変わらない
        assertEquals(
            2,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("build後にmappingsを変更しても影響しない")
    public void testImmutability_Mappings() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("ABQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("immutable_mappings");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('B', new BlockMapping('B', "minecraft:iron_block"));

        IStructureEntry entry = builder.build();

        // build後にbuilderへ追加
        builder.addMapping('C', new BlockMapping('C', "minecraft:gold_block"));

        // 元のentryは変わらない
        assertEquals(
            2,
            entry.getMappings()
                .size());
    }

    @Test
    @DisplayName("build後にrequirementsを変更しても影響しない")
    public void testImmutability_Requirements() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("immutable_reqs");
        builder.addLayer(layer);
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));

        IStructureEntry entry = builder.build();

        // build後にbuilderへ追加
        builder.addRequirement(new EnergyRequirement("energyInput", 1, 1));

        // 元のentryは変わらない
        assertEquals(
            1,
            entry.getRequirements()
                .size());
    }

    @Test
    @DisplayName("build後にrecipeGroupを変更しても影響しない")
    public void testImmutability_RecipeGroup() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("immutable_group");
        builder.addLayer(layer);
        builder.addRecipeGroup("basic");

        IStructureEntry entry = builder.build();

        // build後にbuilderへ追加
        builder.addRecipeGroup("advanced");

        // 元のentryは変わらない
        assertEquals(
            1,
            entry.getRecipeGroup()
                .size());
    }

    @Test
    @DisplayName("controllerOffset配列のclone")
    public void testImmutability_ControllerOffset() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        int[] offset = new int[] { 1, 2, 3 };

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("immutable_offset");
        builder.addLayer(layer);
        builder.setControllerOffset(offset);

        IStructureEntry entry = builder.build();

        // 元の配列を変更
        offset[0] = 999;

        // entryは影響を受けない
        assertEquals(1, entry.getControllerOffset()[0]);
    }

    @Test
    @DisplayName("getLayers() が unmodifiable")
    public void testGetLayers_Unmodifiable() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("unmodifiable_layers");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        // getしたリストは変更不可
        assertThrows(
            UnsupportedOperationException.class,
            () -> {
                entry.getLayers()
                    .add(new StructureLayer("y1", Arrays.asList("A")));
            });
    }

    @Test
    @DisplayName("getMappings() が unmodifiable")
    public void testGetMappings_Unmodifiable() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("unmodifiable_mappings");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // getしたマップは変更不可
        assertThrows(
            UnsupportedOperationException.class,
            () -> {
                entry.getMappings()
                    .put('B', new BlockMapping('B', "minecraft:iron_block"));
            });
    }
}
