package ruiseki.omoshiroikamo.api.structure.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;

/**
 * BlockID解決テスト
 *
 * ============================================
 * ブロックIDの形式と解決をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - 無効なIDでエラー
 * - Mod依存の処理
 * - metadata形式の対応
 *
 * カバーする機能:
 * - 様々なブロックID形式
 * - 特殊ケース（air, OreDict等）
 * - エラーハンドリング
 *
 * ============================================
 */
@DisplayName("BlockID解決テスト")
public class BlockIdResolutionTest {

    @Test
    @DisplayName("有効なバニラブロックID")
    public void testValidVanillaBlockId() {
        BlockMapping mapping = new BlockMapping('A', "minecraft:stone");

        assertNotNull(mapping);
        assertEquals("minecraft:stone", mapping.getBlockId());
    }

    @Test
    @DisplayName("無効なブロックID（形式チェックのみ）")
    public void testInvalidBlockId_Format() {
        // BlockMappingは文字列を受け入れる（バリデーションは別で行う）
        BlockMapping mapping = new BlockMapping('X', "invalid:nonexistent_block");

        assertNotNull(mapping);
        assertEquals("invalid:nonexistent_block", mapping.getBlockId());
    }

    @Test
    @DisplayName("Modブロック形式")
    public void testModBlockId() {
        BlockMapping mapping = new BlockMapping('G', "gregtech:machine");

        assertNotNull(mapping);
        assertEquals("gregtech:machine", mapping.getBlockId());
    }

    @Test
    @DisplayName("\"air\" は常に有効")
    public void testAirBlockId() {
        BlockMapping mapping1 = new BlockMapping('_', "air");
        BlockMapping mapping2 = new BlockMapping(' ', "minecraft:air");

        assertNotNull(mapping1);
        assertNotNull(mapping2);
        assertEquals("air", mapping1.getBlockId());
        assertEquals("minecraft:air", mapping2.getBlockId());
    }

    @Test
    @DisplayName("metadata付きID（minecraft:wool:5）")
    public void testBlockIdWithMetadata() {
        BlockMapping mapping = new BlockMapping('W', "minecraft:wool:5");

        assertNotNull(mapping);
        assertEquals("minecraft:wool:5", mapping.getBlockId());
        assertTrue(
            mapping.getBlockId()
                .contains(":5"));
    }

    @Test
    @DisplayName("OreDict参照形式")
    public void testOreDictReference() {
        // OreDict形式（ore:プレフィックス）
        BlockMapping mapping = new BlockMapping('I', "ore:ingotIron");

        assertNotNull(mapping);
        assertEquals("ore:ingotIron", mapping.getBlockId());
        assertTrue(
            mapping.getBlockId()
                .startsWith("ore:"));
    }

    @Test
    @DisplayName("大文字小文字の扱い")
    public void testCaseSensitivity() {
        BlockMapping mapping1 = new BlockMapping('A', "minecraft:stone");
        BlockMapping mapping2 = new BlockMapping('B', "Minecraft:Stone");

        assertNotNull(mapping1);
        assertNotNull(mapping2);

        // 文字列としては異なる
        assertNotEquals(mapping1.getBlockId(), mapping2.getBlockId());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: BlockResolver.resolve() の実テスト（ゲーム環境必要）
    // TODO: StructureValidator.validateBlockIds() の統合テスト
    // TODO: 実際のブロック解決失敗時のエラーメッセージテスト
}
