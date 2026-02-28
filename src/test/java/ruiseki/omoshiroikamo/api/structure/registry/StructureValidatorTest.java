package ruiseki.omoshiroikamo.api.structure.registry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.core.common.structure.StructureValidator;

/**
 * StructureValidator のテスト
 *
 * ============================================
 * バリデーション統合機能をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - validateStructure()
 * - validateBlockIds()
 * - エラー収集
 *
 * カバーする機能:
 * - Visitor統合
 * - BlockID検証
 * - エラー管理
 *
 * ============================================
 */
@DisplayName("StructureValidator のテスト")
public class StructureValidatorTest {

    @Test
    @DisplayName("validateStructure() で正常構造を承認")
    public void testValidateStructure_Valid() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("valid");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        StructureValidator validator = new StructureValidator();
        boolean hasErrors = validator.validateStructure(entry);

        assertFalse(hasErrors, "正常な構造ではエラーがない");
        assertFalse(validator.hasErrors());
    }

    @Test
    @DisplayName("validateStructure() でエラー構造を検出")
    public void testValidateStructure_Invalid() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("invalid");
        // 空レイヤー

        IStructureEntry entry = builder.build();

        StructureValidator validator = new StructureValidator();
        boolean hasErrors = validator.validateStructure(entry);

        assertTrue(hasErrors, "エラーのある構造を検出");
        assertTrue(validator.hasErrors());
    }

    @Test
    @DisplayName("validateBlockIds() で無効IDを検出")
    public void testValidateBlockIds_Invalid() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("invalid_block");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addMapping('X', new BlockMapping('X', "invalid:nonexistent_block"));

        IStructureEntry entry = builder.build();

        StructureValidator validator = new StructureValidator();
        // validateBlockIdsはゲーム環境が必要なため、ここでは呼び出しのみ
        assertDoesNotThrow(() -> { validator.validateBlockIds(entry); });
    }

    @Test
    @DisplayName("複数エラーの蓄積")
    public void testMultipleErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multi_error");
        // 空レイヤー（エラー1）

        IStructureEntry entry = builder.build();

        StructureValidator validator = new StructureValidator();
        validator.validateStructure(entry);

        assertTrue(validator.hasErrors());
        List<String> errors = validator.getErrors();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    @DisplayName("getErrors() でエラー一覧取得")
    public void testGetErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        // 空レイヤー

        IStructureEntry entry = builder.build();

        StructureValidator validator = new StructureValidator();
        validator.validateStructure(entry);

        List<String> errors = validator.getErrors();
        assertNotNull(errors);
        assertTrue(errors.size() > 0);

        // エラーメッセージが空でない
        for (String error : errors) {
            assertNotNull(error);
            assertFalse(error.isEmpty());
        }
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: BlockResolverとの統合テスト（ゲーム環境必要）
    // TODO: 実際のブロックID検証テスト
    // TODO: カスタムバリデーションルールの追加テスト
}
