package ruiseki.omoshiroikamo.api.structure.edge;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.ItemRequirement;

/**
 * エッジケース・ストレステスト
 *
 * ============================================
 * 極端な値や境界値でのテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - 極端な構造サイズ
 * - 境界値
 * - Null安全性
 *
 * カバーする機能:
 * - 最小/最大構造
 * - Integer境界値
 * - Null処理
 *
 * ============================================
 */
@DisplayName("エッジケース・ストレステスト")
public class EdgeCaseTest {

    // ========================================
    // 極端な値
    // ========================================

    @Test
    @DisplayName("【極端】1x1x1 最小構造")
    public void testMinimalStructure_1x1x1() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("minimal_1x1x1");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));

        IStructureEntry entry = builder.build();

        assertEquals(
            1,
            entry.getLayers()
                .size());
        assertEquals(
            1,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            1,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("【極端】100x100x100 巨大構造")
    public void testLargeStructure_100x100x100() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("large_100x100x100");

        // 100文字の行
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('A');
        }
        String row = sb.toString();

        // 100行
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(row);
        }

        // 100層
        for (int i = 0; i < 100; i++) {
            builder.addLayer(new StructureLayer("y" + i, new ArrayList<>(rows)));
        }

        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        assertEquals(
            100,
            entry.getLayers()
                .size());
        assertEquals(
            100,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            100,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("【極端】レイヤー1000層")
    public void testExtremeLayers_1000() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("1000_layers");

        for (int i = 0; i < 1000; i++) {
            builder.addLayer(new StructureLayer("y" + i, Arrays.asList("Q")));
        }

        IStructureEntry entry = builder.build();

        assertEquals(
            1000,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("【極端】行幅500文字")
    public void testExtremeRowWidth_500() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append('A');
        }
        String row = sb.toString();

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("width_500");
        builder.addLayer(new StructureLayer("y0", Arrays.asList(row)));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        assertEquals(
            500,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("【極端】マッピング256個（全ASCII）")
    public void testExtremeMappings_256() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("256_mappings");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));

        // ASCII文字全体をマッピング（現実的には52個程度が限界だが）
        for (char c = 'A'; c <= 'Z'; c++) {
            builder.addMapping(c, new BlockMapping(c, "minecraft:stone"));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            builder.addMapping(c, new BlockMapping(c, "minecraft:stone"));
        }
        for (char c = '0'; c <= '9'; c++) {
            builder.addMapping(c, new BlockMapping(c, "minecraft:stone"));
        }

        IStructureEntry entry = builder.build();

        // 26 + 26 + 10 = 62
        assertEquals(
            62,
            entry.getMappings()
                .size());
    }

    // ========================================
    // 境界値
    // ========================================

    @Test
    @DisplayName("【境界】tier = Integer.MIN_VALUE")
    public void testTier_IntegerMinValue() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("tier_min");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setTier(Integer.MIN_VALUE);

        IStructureEntry entry = builder.build();

        assertEquals(Integer.MIN_VALUE, entry.getTier());
    }

    @Test
    @DisplayName("【境界】tier = Integer.MAX_VALUE")
    public void testTier_IntegerMaxValue() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("tier_max");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setTier(Integer.MAX_VALUE);

        IStructureEntry entry = builder.build();

        assertEquals(Integer.MAX_VALUE, entry.getTier());
    }

    @Test
    @DisplayName("【境界】requirement min = 0, max = 0")
    public void testRequirement_MinMax_Zero() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("req_zero");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", 0, 0));

        IStructureEntry entry = builder.build();

        assertEquals(
            0,
            entry.getRequirements()
                .get(0)
                .getMinCount());
        assertEquals(
            0,
            entry.getRequirements()
                .get(0)
                .getMaxCount());
    }

    @Test
    @DisplayName("【境界】requirement min = Integer.MAX_VALUE")
    public void testRequirement_MaxValue() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("req_max");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", Integer.MAX_VALUE, Integer.MAX_VALUE));

        IStructureEntry entry = builder.build();

        assertEquals(
            Integer.MAX_VALUE,
            entry.getRequirements()
                .get(0)
                .getMinCount());
    }

    @Test
    @DisplayName("【境界】controllerOffset = [-1000, -1000, -1000]")
    public void testControllerOffset_Negative() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("offset_negative");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setControllerOffset(new int[] { -1000, -1000, -1000 });

        IStructureEntry entry = builder.build();

        int[] offset = entry.getControllerOffset();
        assertEquals(-1000, offset[0]);
        assertEquals(-1000, offset[1]);
        assertEquals(-1000, offset[2]);
    }

    // ========================================
    // Null安全性
    // ========================================

    @Test
    @DisplayName("【Null】displayName = null")
    public void testDisplayName_Null() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("null_display");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setDisplayName(null);

        IStructureEntry entry = builder.build();

        assertNull(entry.getDisplayName());
    }

    @Test
    @DisplayName("【Null】recipeGroup = null（空リストになる）")
    public void testRecipeGroup_Null() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("null_group");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        // recipeGroupを追加しない

        IStructureEntry entry = builder.build();

        assertNotNull(entry.getRecipeGroup());
        assertEquals(
            0,
            entry.getRecipeGroup()
                .size());
    }

    @Test
    @DisplayName("【Null】controllerOffset = null")
    public void testControllerOffset_Null() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("null_offset");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setControllerOffset(null);

        IStructureEntry entry = builder.build();

        assertNull(entry.getControllerOffset());
    }

    @Test
    @DisplayName("【Null】tintColor = null")
    public void testTintColor_Null() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("null_tint");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setTintColor(null);

        IStructureEntry entry = builder.build();

        assertNull(entry.getTintColor());
    }

    @Test
    @DisplayName("【Null】defaultFacing = null")
    public void testDefaultFacing_Null() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("null_facing");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.setDefaultFacing(null);

        IStructureEntry entry = builder.build();

        assertNull(entry.getDefaultFacing());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: メモリ使用量テスト（超巨大構造）
    // TODO: パフォーマンステスト（1000構造の登録）
    // TODO: 並行処理テスト（マルチスレッド）
}
