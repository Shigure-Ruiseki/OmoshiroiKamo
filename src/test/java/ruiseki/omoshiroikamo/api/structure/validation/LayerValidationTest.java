package ruiseki.omoshiroikamo.api.structure.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;

/**
 * レイヤー構造の検証テスト
 *
 * ============================================
 * 構造のレイヤーが正しく定義されているかをテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - 不正なレイヤー定義でゲーム内エラー
 * - 行幅不一致で構造認識失敗
 * - 空レイヤーでクラッシュ
 *
 * カバーする機能:
 * - レイヤー配列の検証
 * - 行幅の一貫性チェック
 * - 極端なケース（巨大構造、最小構造）
 *
 * ============================================
 */
@DisplayName("レイヤー構造の検証テスト")
public class LayerValidationTest {

    @Test
    @DisplayName("空レイヤー配列（0層）")
    public void testEmptyLayerArray() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("empty_layers");
        // レイヤーを追加しない

        // build()は成功するか、エラーを投げるか
        IStructureEntry entry = builder.build();

        assertNotNull(entry);
        assertEquals(
            0,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("レイヤー内の行幅が一致しない")
    public void testInconsistentRowWidthInLayer() {
        List<String> rows = Arrays.asList("AAA", "BB", "AAAA");
        IStructureLayer layer = new StructureLayer("y0", rows);

        // 行幅が一致しないが、StructureLayerは受け入れる
        assertNotNull(layer);
        assertEquals(
            3,
            layer.getRows()
                .size());
        assertEquals(
            3,
            layer.getRows()
                .get(0)
                .length());
        assertEquals(
            2,
            layer.getRows()
                .get(1)
                .length());
        assertEquals(
            4,
            layer.getRows()
                .get(2)
                .length());
    }

    @Test
    @DisplayName("レイヤー間で行数が異なる（合法）")
    public void testDifferentRowCountBetweenLayers() {
        IStructureLayer layer1 = new StructureLayer("y0", Arrays.asList("AAA", "AAA"));
        IStructureLayer layer2 = new StructureLayer("y1", Arrays.asList("BBB", "BBB", "BBB"));
        IStructureLayer layer3 = new StructureLayer("y2", Arrays.asList("CCC"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("different_row_count");
        builder.addLayer(layer1);
        builder.addLayer(layer2);
        builder.addLayer(layer3);

        IStructureEntry entry = builder.build();

        assertEquals(
            3,
            entry.getLayers()
                .size());
        assertEquals(
            2,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            3,
            entry.getLayers()
                .get(1)
                .getRows()
                .size());
        assertEquals(
            1,
            entry.getLayers()
                .get(2)
                .getRows()
                .size());
    }

    @Test
    @DisplayName("空の行（\"\"）")
    public void testEmptyRow() {
        List<String> rows = Arrays.asList("", "", "");
        IStructureLayer layer = new StructureLayer("y0", rows);

        assertNotNull(layer);
        assertEquals(
            3,
            layer.getRows()
                .size());
        assertEquals(
            0,
            layer.getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("スペースのみの行（\"   \"）")
    public void testSpaceOnlyRow() {
        List<String> rows = Arrays.asList("   ", "   ", "   ");
        IStructureLayer layer = new StructureLayer("y0", rows);

        assertNotNull(layer);
        assertEquals(
            3,
            layer.getRows()
                .size());
        assertEquals(
            3,
            layer.getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("タブ文字を含む行")
    public void testRowWithTabCharacter() {
        List<String> rows = Arrays.asList("A\tB\tC");
        IStructureLayer layer = new StructureLayer("y0", rows);

        assertNotNull(layer);
        assertTrue(
            layer.getRows()
                .get(0)
                .contains("\t"));
    }

    @Test
    @DisplayName("改行を含む行")
    public void testRowWithNewline() {
        List<String> rows = Arrays.asList("A\nB\nC");
        IStructureLayer layer = new StructureLayer("y0", rows);

        // 改行を含む文字列も受け入れる（バリデーションは別で行う）
        assertNotNull(layer);
    }

    @Test
    @DisplayName("Unicode文字を含む行")
    public void testRowWithUnicode() {
        List<String> rows = Arrays.asList("あいう", "かきく");
        IStructureLayer layer = new StructureLayer("y0", rows);

        assertNotNull(layer);
        assertEquals(
            "あいう",
            layer.getRows()
                .get(0));
    }

    @Test
    @DisplayName("最大レイヤー数（100層）")
    public void testMaxLayerCount_100Layers() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("100_layers");

        for (int i = 0; i < 100; i++) {
            IStructureLayer layer = new StructureLayer("y" + i, Arrays.asList("AAA", "AAA", "AAA"));
            builder.addLayer(layer);
        }

        IStructureEntry entry = builder.build();

        assertEquals(
            100,
            entry.getLayers()
                .size());
    }

    @Test
    @DisplayName("最大行幅（100文字）")
    public void testMaxRowWidth_100Characters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('A');
        }
        String longRow = sb.toString();

        List<String> rows = Arrays.asList(longRow);
        IStructureLayer layer = new StructureLayer("y0", rows);

        assertNotNull(layer);
        assertEquals(
            100,
            layer.getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("最小構造（1x1x1）")
    public void testMinimalStructure_1x1x1() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("Q"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("minimal_1x1x1");
        builder.addLayer(layer);

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
    @DisplayName("巨大構造（50x50x50）")
    public void testLargeStructure_50x50x50() {
        StringBuilder rowBuilder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            rowBuilder.append('A');
        }
        String row = rowBuilder.toString();

        List<String> rows = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            rows.add(row);
        }

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("large_50x50x50");

        for (int i = 0; i < 50; i++) {
            IStructureLayer layer = new StructureLayer("y" + i, new ArrayList<>(rows));
            builder.addLayer(layer);
        }

        IStructureEntry entry = builder.build();

        assertEquals(
            50,
            entry.getLayers()
                .size());
        assertEquals(
            50,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            50,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("非対称構造（3x7x5）")
    public void testAsymmetricStructure_3x7x5() {
        // 3文字幅、7行、5層
        List<String> rows = Arrays.asList("AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA");

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("asymmetric_3x7x5");

        for (int i = 0; i < 5; i++) {
            IStructureLayer layer = new StructureLayer("y" + i, new ArrayList<>(rows));
            builder.addLayer(layer);
        }

        IStructureEntry entry = builder.build();

        assertEquals(
            5,
            entry.getLayers()
                .size());
        assertEquals(
            7,
            entry.getLayers()
                .get(0)
                .getRows()
                .size());
        assertEquals(
            3,
            entry.getLayers()
                .get(0)
                .getRows()
                .get(0)
                .length());
    }

    @Test
    @DisplayName("toStructureLibRows の変換テスト")
    public void testToStructureLibRows() {
        List<String> rows = Arrays.asList("AAA", "BBB", "CCC");
        IStructureLayer layer = new StructureLayer("y0", rows);

        String[][] result = layer.toStructureLibRows();

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("AAA", result[0][0]);
        assertEquals("BBB", result[1][0]);
        assertEquals("CCC", result[2][0]);
    }

    @Test
    @DisplayName("空rows配列のtoStructureLibRows")
    public void testToStructureLibRows_EmptyRows() {
        List<String> rows = new ArrayList<>();
        IStructureLayer layer = new StructureLayer("y0", rows);

        String[][] result = layer.toStructureLibRows();

        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
