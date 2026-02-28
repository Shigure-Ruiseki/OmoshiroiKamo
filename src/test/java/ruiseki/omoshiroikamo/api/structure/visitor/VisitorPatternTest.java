package ruiseki.omoshiroikamo.api.structure.visitor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.EnergyRequirement;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.io.ItemRequirement;

/**
 * Visitor パターンのテスト
 *
 * ============================================
 * Visitor パターンの基本動作をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - Visitor が正しく呼ばれる
 * - すべての要素を訪問
 * - カスタムVisitorの実装
 *
 * カバーする機能:
 * - 基本的な訪問
 * - Requirements の訪問
 * - カスタムVisitor
 *
 * ============================================
 */
@DisplayName("Visitor パターンのテスト")
public class VisitorPatternTest {

    @Test
    @DisplayName("【最重要】StructureEntry.accept() で visitor.visit(entry) が呼ばれる")
    public void testAccept_VisitsEntry() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));

        IStructureEntry entry = builder.build();

        TestVisitor visitor = new TestVisitor();
        entry.accept(visitor);

        assertTrue(visitor.entryVisited);
        assertEquals(1, visitor.visitedEntries.size());
        assertEquals(
            "test",
            visitor.visitedEntries.get(0)
                .getName());
    }

    @Test
    @DisplayName("【最重要】Requirements も訪問される")
    public void testAccept_VisitsRequirements() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));

        IStructureEntry entry = builder.build();

        TestVisitor visitor = new TestVisitor();
        entry.accept(visitor);

        assertTrue(visitor.requirementVisited);
        assertEquals(1, visitor.visitedRequirements.size());
        assertEquals(
            "itemInput",
            visitor.visitedRequirements.get(0)
                .getType());
    }

    @Test
    @DisplayName("【最重要】複数 requirements がすべて訪問される")
    public void testAccept_VisitsMultipleRequirements() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRequirement(new ItemRequirement("itemOutput", 1, 2));
        builder.addRequirement(new EnergyRequirement("energyInput", 1, 1));

        IStructureEntry entry = builder.build();

        TestVisitor visitor = new TestVisitor();
        entry.accept(visitor);

        assertEquals(3, visitor.visitedRequirements.size());
        assertEquals(
            "itemInput",
            visitor.visitedRequirements.get(0)
                .getType());
        assertEquals(
            "itemOutput",
            visitor.visitedRequirements.get(1)
                .getType());
        assertEquals(
            "energyInput",
            visitor.visitedRequirements.get(2)
                .getType());
    }

    @Test
    @DisplayName("StructureValidationVisitor でエラー収集")
    public void testValidationVisitor_CollectsErrors() {
        // エラーのある構造（空レイヤー）
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("error_structure");
        // レイヤーを追加しない

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        // 空レイヤーはエラー
        assertTrue(validator.hasErrors());
    }

    @Test
    @DisplayName("StructureValidationVisitor.hasErrors()")
    public void testValidationVisitor_HasErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList()));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        assertTrue(validator.hasErrors());
    }

    @Test
    @DisplayName("StructureValidationVisitor.getErrors() の内容")
    public void testValidationVisitor_GetErrors() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList()));

        IStructureEntry entry = builder.build();

        StructureValidationVisitor validator = new StructureValidationVisitor();
        entry.accept(validator);

        List<String> errors = validator.getErrors();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    @DisplayName("カスタムVisitorの実装テスト")
    public void testCustomVisitor() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("custom_test");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("Q")));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));

        IStructureEntry entry = builder.build();

        TestVisitor visitor = new TestVisitor();
        entry.accept(visitor);

        assertTrue(visitor.entryVisited);
        assertTrue(visitor.requirementVisited);
    }

    @Test
    @DisplayName("Visitor で構造を走査して統計情報収集")
    public void testVisitor_CollectStatistics() {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName("stats");
        builder.addLayer(new StructureLayer("y0", Arrays.asList("AAA")));
        builder.addLayer(new StructureLayer("y1", Arrays.asList("BBB")));
        builder.addLayer(new StructureLayer("y2", Arrays.asList("CCC")));
        builder.addRequirement(new ItemRequirement("itemInput", 1, 4));
        builder.addRequirement(new ItemRequirement("itemOutput", 1, 2));

        IStructureEntry entry = builder.build();

        StatsVisitor visitor = new StatsVisitor();
        entry.accept(visitor);

        assertEquals(1, visitor.entryCount);
        assertEquals(2, visitor.requirementCount);
    }

    // ========================================
    // テスト用Visitor実装
    // ========================================

    private static class TestVisitor implements IStructureVisitor {

        boolean entryVisited = false;
        boolean requirementVisited = false;
        List<IStructureEntry> visitedEntries = new ArrayList<>();
        List<IStructureRequirement> visitedRequirements = new ArrayList<>();

        @Override
        public void visit(IStructureEntry entry) {
            entryVisited = true;
            visitedEntries.add(entry);
        }

        @Override
        public void visit(IStructureRequirement requirement) {
            requirementVisited = true;
            visitedRequirements.add(requirement);
        }
    }

    private static class StatsVisitor implements IStructureVisitor {

        int entryCount = 0;
        int requirementCount = 0;

        @Override
        public void visit(IStructureEntry entry) {
            entryCount++;
        }

        @Override
        public void visit(IStructureRequirement requirement) {
            requirementCount++;
        }
    }
}
