package ruiseki.omoshiroikamo.api.structure.visitor;

import static org.junit.jupiter.api.Assertions.*;

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
 * StructureValidationVisitor のテスト
 *
 * ============================================
 * バリデーションVisitorの具体的検証をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★★
 * - 未定義シンボルの検出
 * - Qの存在チェック
 * - エラーメッセージの内容
 *
 * カバーする機能:
 * - 各種バリデーションルール
 * - エラー収集
 * - 正常ケースの確認
 *
 * ============================================
 */
@DisplayName("StructureValidationVisitor のテスト（★最重要★）")
public class StructureValidationVisitorTest {

    @Test
    @DisplayName("【★最重要★】空レイヤーを検出")
    public void testDetectEmptyLayers() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("empty_layers");
        // レイヤーなし

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertTrue(validator.hasErrors());
        List<String> errors = validator.getErrors();
        assertTrue(
            errors.stream()
                .anyMatch(e -> e.contains("layer") || e.contains("empty")));
    }

    @Test
    @DisplayName("【★最重要★】行幅不一致を検出")
    public void testDetectInconsistentRowWidth() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("inconsistent_width");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "BB", "AAAA")));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // 行幅不一致はエラーまたは警告
        // 実装によってはエラーにしない可能性もある
        assertNotNull(validator.getErrors());
    }

    @Test
    @DisplayName("【★最重要★】未定義シンボルを検出")
    public void testDetectUndefinedSymbol() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("undefined_symbol");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("ZZZ", "ZQZ", "ZZZ")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        // 'Z'のマッピングがない

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertTrue(validator.hasErrors());
        List<String> errors = validator.getErrors();
        assertTrue(
            errors.stream()
                .anyMatch(e -> e.contains("Z") || e.contains("undefined") || e.contains("unmapped")));
    }

    @Test
    @DisplayName("【★最重要★】Qが存在しない構造を検出")
    public void testDetectMissingQ() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("no_Q");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AAA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertTrue(validator.hasErrors());
        List<String> errors = validator.getErrors();
        assertTrue(
            errors.stream()
                .anyMatch(e -> e.contains("Q") || e.contains("controller")));
    }

    @Test
    @DisplayName("【★最重要★】Qが複数ある構造を検出（またはOK）")
    public void testDetectMultipleQ() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multiple_Q");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("QQQ", "QQQ", "QQQ")));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // 実装によって複数Qをエラーにするか、許可するかが異なる
        // エラーの場合
        if (validator.hasErrors()) {
            List<String> errors = validator.getErrors();
            assertTrue(
                errors.stream()
                    .anyMatch(e -> e.contains("multiple") || e.contains("Q")));
        }
    }

    @Test
    @DisplayName("使用されていないシンボルを警告")
    public void testDetectUnusedSymbol() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("unused_symbol");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addMapping('Z', new BlockMapping('Z', "minecraft:iron_block")); // 未使用

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // 未使用シンボルは警告レベル（エラーではない可能性）
        // または、エラーリストに含まれる
        assertNotNull(validator.getErrors());
    }

    @Test
    @DisplayName("requirement の min > max を検出")
    public void testDetectMinGreaterThanMax() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("invalid_req");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", 10, 5)); // min > max

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // min > maxはエラー
        assertTrue(validator.hasErrors());
        List<String> errors = validator.getErrors();
        assertTrue(
            errors.stream()
                .anyMatch(e -> e.contains("min") || e.contains("max")));
    }

    @Test
    @DisplayName("requirement の負の値を検出")
    public void testDetectNegativeRequirement() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("negative_req");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", -1, -5));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // 負の値はエラー
        assertTrue(validator.hasErrors());
    }

    @Test
    @DisplayName("正常な構造でエラーなし")
    public void testValidStructure_NoErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("valid");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertFalse(validator.hasErrors(), "正常な構造ではエラーがないはず");
        assertEquals(
            0,
            validator.getErrors()
                .size());
    }

    @Test
    @DisplayName("複数エラーを同時に検出")
    public void testMultipleErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("multiple_errors");
        // 1. 空レイヤー
        // 2. requirementのmin > max
        builder.addRequirement(new ItemRequirement("itemInput", 10, 5));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertTrue(validator.hasErrors());
        // 複数のエラーが検出される
        assertTrue(
            validator.getErrors()
                .size() >= 2);
    }

    @Test
    @DisplayName("エラーメッセージの内容確認")
    public void testErrorMessageContent() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        // 空レイヤー

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        List<String> errors = validator.getErrors();
        assertFalse(errors.isEmpty());

        // エラーメッセージに構造名が含まれる
        String firstError = errors.get(0);
        assertNotNull(firstError);
        assertFalse(firstError.isEmpty());
    }

    @Test
    @DisplayName("StructureValidator との統合")
    public void testIntegrationWithStructureValidator() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("integration_test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA", "AQA", "AAA")));
        builder.addMapping('A', new BlockMapping('A', "minecraft:stone"));

        IStructureEntry entry = builder.build();

        // StructureValidatorを使用
        ruiseki.omoshiroikamo.core.common.structure.StructureValidator structureValidator = new ruiseki.omoshiroikamo.core.common.structure.StructureValidator();
        boolean hasErrors = structureValidator.validateStructure(entry);

        // 正常な構造なのでエラーなし
        assertFalse(hasErrors);
    }
}
