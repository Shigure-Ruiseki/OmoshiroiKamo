package ruiseki.omoshiroikamo.api.structure.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;

/**
 * シンボルマッピングの検証テスト
 *
 * ============================================
 * 構造内のシンボルとマッピングの整合性をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - 未定義シンボルでゲーム内クラッシュ
 * - コントローラーQの存在チェック
 * - 未使用シンボルの検出
 *
 * カバーする機能:
 * - シンボルの存在チェック
 * - マッピングの完全性検証
 * - 特殊シンボル（Q, _, スペース）の処理
 *
 * ============================================
 */
@DisplayName("シンボルマッピングの検証テスト（★最重要★）")
public class SymbolMappingValidationTest {

    @Test
    @DisplayName("【★最重要★】使用されていないシンボルの検出")
    public void testUnusedSymbol() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("unused_symbol");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('Z', new BlockMapping('Z', "minecraft:iron_block")); // 未使用

        IStructureEntry entry = builder.build();

        // 未使用のシンボル'Z'を検出
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertFalse(usedSymbols.contains('Z'));
        assertTrue(
            entry.getMappings()
                .containsKey('Z'));
    }

    @Test
    @DisplayName("【★最重要★】未定義シンボルの検出")
    public void testUndefinedSymbol() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("ZZZ", "ZQZ", "ZZZ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("undefined_symbol");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        // 'Z'のマッピングがない

        IStructureEntry entry = builder.build();

        // 'Z'が使用されているがマッピングされていない
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertTrue(usedSymbols.contains('Z'));
        assertFalse(
            entry.getMappings()
                .containsKey('Z'));
    }

    @Test
    @DisplayName("【★最重要★】'Q' (コントローラー) の存在チェック")
    public void testControllerQ_Exists() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("has_Q");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // 'Q'が存在する
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertTrue(usedSymbols.contains('Q'));
    }

    @Test
    @DisplayName("【★最重要★】'Q'が複数ある場合")
    public void testMultipleControllerQ() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("QQQ", "QQQ", "QQQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multiple_Q");
        builder.addLayer(layer);

        IStructureEntry entry = builder.build();

        // 'Q'が9個ある
        int qCount = countSymbol(entry, 'Q');
        assertEquals(9, qCount);
    }

    @Test
    @DisplayName("【★最重要★】'Q'が存在しない場合")
    public void testNoControllerQ() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AAA", "AAA", "AAA"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("no_Q");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // 'Q'が存在しない
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertFalse(usedSymbols.contains('Q'));
    }

    @Test
    @DisplayName("'_' (空気) の自動処理")
    public void testUnderscoreSymbol_Air() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("A_A", "___", "A_A"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("with_air");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // '_'が使用されている
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertTrue(usedSymbols.contains('_'));
    }

    @Test
    @DisplayName("' ' (スペース) の処理")
    public void testSpaceSymbol() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("A A", "   ", "A A"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("with_space");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // スペースが使用されている
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertTrue(usedSymbols.contains(' '));
    }

    @Test
    @DisplayName("BlockMapping の単一/複数切り替え")
    public void testBlockMapping_SingleVsMultiple() {
        BlockMapping single = new BlockMapping('A', "minecraft:stone");
        BlockMapping multiple = new BlockMapping('B', Arrays.asList("minecraft:stone", "minecraft:cobblestone"));

        assertNotNull(single.getBlockId());
        assertNull(single.getBlockIds());

        assertNull(multiple.getBlockId());
        assertNotNull(multiple.getBlockIds());
        assertEquals(
            2,
            multiple.getBlockIds()
                .size());
    }

    @Test
    @DisplayName("BlockMapping serialize → deserialize")
    public void testBlockMapping_Serialization() {
        BlockMapping original = new BlockMapping('A', "minecraft:stone");

        // serialize
        com.google.gson.JsonObject json = original.serialize();

        assertNotNull(json);
        assertTrue(json.has("block"));
        assertEquals(
            "minecraft:stone",
            json.get("block")
                .getAsString());

        // 複数ブロック
        BlockMapping multiple = new BlockMapping('B', Arrays.asList("minecraft:stone", "minecraft:cobblestone"));
        com.google.gson.JsonObject multiJson = multiple.serialize();

        assertTrue(multiJson.has("blocks"));
        assertTrue(
            multiJson.get("blocks")
                .isJsonArray());
        assertEquals(
            2,
            multiJson.getAsJsonArray("blocks")
                .size());
    }

    @Test
    @DisplayName("大文字小文字混在（'a' と 'A' は別物）")
    public void testCaseSensitivity() {
        IStructureLayer layer = new StructureLayer("y0", Arrays.asList("AaQ"));

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("case_sensitive");
        builder.addLayer(layer);
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('a', new BlockMapping('a', "minecraft:cobblestone"));

        IStructureEntry entry = builder.build();

        // 'A'と'a'は別のシンボル
        Set<Character> usedSymbols = findUsedSymbols(entry);
        assertTrue(usedSymbols.contains('A'));
        assertTrue(usedSymbols.contains('a'));
        assertTrue(
            entry.getMappings()
                .containsKey('A'));
        assertTrue(
            entry.getMappings()
                .containsKey('a'));
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private Set<Character> findUsedSymbols(IStructureEntry entry) {
        Set<Character> symbols = new HashSet<>();
        for (IStructureLayer layer : entry.getLayers()) {
            for (String row : layer.getRows()) {
                for (char c : row.toCharArray()) {
                    symbols.add(c);
                }
            }
        }
        return symbols;
    }

    private int countSymbol(IStructureEntry entry, char symbol) {
        int count = 0;
        for (IStructureLayer layer : entry.getLayers()) {
            for (String row : layer.getRows()) {
                for (char c : row.toCharArray()) {
                    if (c == symbol) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
