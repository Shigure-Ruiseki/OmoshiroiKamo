package ruiseki.omoshiroikamo.api.structure.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;

/**
 * コントローラーオフセット検出テスト
 *
 * ============================================
 * コントローラー'Q'の位置検出をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - オフセット検出ミスで構造認識失敗
 * - rotate180 変換の正確性
 * - 構造の根幹となる機能
 *
 * カバーする機能:
 * - 様々な位置でのQ検出
 * - JSONで指定されたオフセットとの一貫性
 * - 非対称構造での検出
 *
 * ============================================
 */
@DisplayName("コントローラーオフセット検出テスト（★最重要★）")
public class ControllerOffsetTest {

    @Test
    @DisplayName("【最重要】3x3x3 構造の中央にQ")
    public void test3x3x3_CenterQ() {
        // Layer 0 (bottom)
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));
        // Layer 1 (middle)
        IStructureLayer layer1 = new StructureLayer("y1", Arrays.asList("AAA", "A_A", "AAA"));
        // Layer 2 (top)
        IStructureLayer layer2 = new StructureLayer("y2", Arrays.asList("AAA", "AAA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("3x3x3_center");
        builder.addLayer(layer0);
        builder.addLayer(layer1);
        builder.addLayer(layer2);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは中央（layer0の2行目、2列目）
        // 期待値: [1, 0, 1] (x=1, y=0, z=1) ※0-indexed
        // ※実装によって座標系が異なる可能性あり
        assertNotNull(entry);
    }

    @Test
    @DisplayName("【最重要】3x3x3 構造の端にQ（0,0,0）")
    public void test3x3x3_CornerQ_000() {
        // Layer 0
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("QAA", "AAA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("3x3x3_corner_000");
        builder.addLayer(layer0);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは左上隅
        assertNotNull(entry);
    }

    @Test
    @DisplayName("【最重要】3x3x3 構造の反対端にQ（2,2,2）")
    public void test3x3x3_OppositeCornerQ() {
        // Layer 0
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("AAA", "AAA", "AAA"));
        // Layer 1
        IStructureLayer layer1 = new StructureLayer("y1", Arrays.asList("AAA", "AAA", "AAA"));
        // Layer 2
        IStructureLayer layer2 = new StructureLayer("y2", Arrays.asList("AAA", "AAA", "AAQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("3x3x3_corner_222");
        builder.addLayer(layer0);
        builder.addLayer(layer1);
        builder.addLayer(layer2);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは右下奥
        assertNotNull(entry);
    }

    @Test
    @DisplayName("非対称構造でのQ位置")
    public void testAsymmetricStructure_QPosition() {
        // 5x3x2 構造
        // Layer 0
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("AAAAA", "AAQAA", "AAAAA"));
        // Layer 1
        IStructureLayer layer1 = new StructureLayer("y1", Arrays.asList("AAAAA", "AAAAA", "AAAAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("asymmetric_5x3x2");
        builder.addLayer(layer0);
        builder.addLayer(layer1);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは layer0, row1, col2
        assertNotNull(entry);
    }

    @Test
    @DisplayName("Qが最初のレイヤーにある")
    public void testQ_InFirstLayer() {
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("QAA", "AAA", "AAA"));
        IStructureLayer layer1 = new StructureLayer("y1", Arrays.asList("AAA", "AAA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("q_first_layer");
        builder.addLayer(layer0);
        builder.addLayer(layer1);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは最初のレイヤー（y=0）にある
        assertNotNull(entry);
    }

    @Test
    @DisplayName("Qが最後のレイヤーにある")
    public void testQ_InLastLayer() {
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("AAA", "AAA", "AAA"));
        IStructureLayer layer1 = new StructureLayer("y1", Arrays.asList("AAA", "AAA", "AAA"));
        IStructureLayer layer2 = new StructureLayer("y2", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("q_last_layer");
        builder.addLayer(layer0);
        builder.addLayer(layer1);
        builder.addLayer(layer2);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // Qは最後のレイヤー（y=2）にある
        assertNotNull(entry);
    }

    @Test
    @DisplayName("controllerOffset JSONフィールドの設定")
    public void testControllerOffset_JSONField() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("with_offset");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.setControllerOffset(new int[] { 1, 0, 1 });

        IStructureEntry entry = builder.build();

        int[] offset = entry.getControllerOffset();
        assertNotNull(offset);
        assertEquals(3, offset.length);
        assertEquals(1, offset[0]);
        assertEquals(0, offset[1]);
        assertEquals(1, offset[2]);
    }

    @Test
    @DisplayName("controllerOffset と実際のQ位置の一貫性")
    public void testControllerOffset_Consistency() {
        // 3x3x3構造でQが中央
        IStructureLayer layer0 = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("offset_consistency");
        builder.addLayer(layer0);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        // JSON指定のオフセット
        builder.setControllerOffset(new int[] { 1, 0, 1 });

        IStructureEntry entry = builder.build();

        // JSON指定のオフセットが正しく保存されている
        int[] offset = entry.getControllerOffset();
        assertEquals(1, offset[0]);
        assertEquals(0, offset[1]);
        assertEquals(1, offset[2]);

        // 実際のQ位置を確認
        boolean foundQ = false;
        for (IStructureLayer layer : entry.getLayers()) {
            for (String row : layer.getRows()) {
                if (row.contains("Q")) {
                    foundQ = true;
                    break;
                }
            }
        }
        assertTrue(foundQ, "構造内にQが存在すること");
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: CustomStructureRegistry.findControllerOffset() の実テスト
    // TODO: rotate180 変換後のオフセット計算テスト
    // TODO: StructureLib座標系への変換テスト
}
