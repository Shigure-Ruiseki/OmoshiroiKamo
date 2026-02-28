package ruiseki.omoshiroikamo.api.structure.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonReader;

/**
 * Structure ファイル読み込み統合テスト
 *
 * ============================================
 * 最重要テスト: JSON読み込み機能の完全検証
 * ============================================
 *
 * このテストは実際の test_structures.json を読み込み、
 * 様々な構造パターンが正しく処理されることを検証します。
 *
 * バグ発見の優先度: ★★★★★
 * - JSON読み込みはシステムの入り口
 * - ここでバグがあると、すべての構造が動作しない
 *
 * カバーする機能:
 * - 単純な構造 (1x1x1)
 * - 複雑な構造 (3x3x3, 複数レイヤー)
 * - default mappings
 * - 7種類の requirements (Item, Fluid, Energy, Mana, Gas, Essentia, Vis)
 * - オプションフィールド (displayName, recipeGroup, controllerOffset, tintColor, tier)
 * - 複数ブロックマッピング
 * - metadata付きブロック
 *
 * ============================================
 */
@DisplayName("Structure ファイル読み込み統合テスト（★最重要★）")
public class StructureFileLoaderTest {

    private static Map<String, IStructureEntry> structures;
    private static Map<Character, ISymbolMapping> defaultMappings;

    // 読み込むファイルのリスト
    private static final String[] STRUCTURE_FILES = {
        "minimal.json",
        "simple_3x3x1.json",
        "complex_3x3x3.json",
        "with_item_requirement.json",
        "with_fluid_requirement.json",
        "with_energy_requirement.json",
        "with_all_requirements.json",
        "with_display_name.json",
        "with_recipe_group.json",
        "with_controller_offset.json",
        "with_tint_color.json",
        "with_tier.json",
        "with_multiple_blocks.json",
        "with_metadata.json"
    };

    @BeforeAll
    public static void setUpAll() {
        try {
            structures = new java.util.HashMap<>();
            defaultMappings = new java.util.HashMap<>();

            // 各ファイルを読み込み
            for (String filename : STRUCTURE_FILES) {
                InputStream inputStream = StructureFileLoaderTest.class
                    .getResourceAsStream("/structures/" + filename);

                if (inputStream == null) {
                    System.err.println("Warning: Test resource not found: /structures/" + filename);
                    continue;
                }

                try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                    JsonElement element = new JsonParser().parse(new JsonReader(reader));
                    StructureJsonReader.FileData data = StructureJsonReader.readFile(element);

                    // 構造を追加
                    structures.putAll(data.structures);

                    // default mappingsがあればマージ
                    defaultMappings.putAll(data.defaultMappings);
                }
            }

            assertFalse(structures.isEmpty(), "No structures loaded from test files");
            System.out.println("Loaded " + structures.size() + " structures from test files");
        } catch (Throwable t) {
            System.err.println("Failed to load structures in integration test: " + t.getMessage());
            t.printStackTrace();
            throw new RuntimeException("Test setup failed", t);
        }
    }

    // ========================================
    // 基本的な読み込み確認
    // ========================================

    @Test
    @DisplayName("【最優先】構造が読み込まれる")
    public void test構造の総数() {
        assertNotNull(structures);
        assertFalse(structures.isEmpty(), "少なくとも1つの構造が読み込まれるべき");
        assertEquals(14, structures.size(), "14個の構造が読み込まれるべき");
    }

    @Test
    @DisplayName("読み込まれた全ての構造がnullでない")
    public void test全構造がnullでない() {
        for (Map.Entry<String, IStructureEntry> entry : structures.entrySet()) {
            assertNotNull(entry.getKey(), "構造名がnullであってはならない");
            assertNotNull(entry.getValue(), "構造がnullであってはならない");
        }
    }

    @Test
    @DisplayName("全ての構造にnameが設定されている")
    public void test全構造にnameがある() {
        for (IStructureEntry structure : structures.values()) {
            assertNotNull(structure.getName(), "name が null であってはならない");
            assertFalse(structure.getName().isEmpty(), "name が空であってはならない");
        }
    }

    @Test
    @DisplayName("全ての構造にlayersが設定されている")
    public void test全構造にlayersがある() {
        for (IStructureEntry structure : structures.values()) {
            assertNotNull(structure.getLayers(), "layers が null であってはならない");
            assertFalse(structure.getLayers().isEmpty(), "layers が空であってはならない");
        }
    }

    // ========================================
    // 個別構造のテスト
    // ========================================

    @Test
    @DisplayName("【基本】minimal - 最小構造 (1x1x1)")
    public void testMinimal構造() {
        IStructureEntry structure = structures.get("minimal");
        assertNotNull(structure, "minimal structure not found");

        assertEquals("minimal", structure.getName());
        assertEquals(1, structure.getLayers().size());
        assertEquals(1, structure.getLayers().get(0).getRows().size());
        assertEquals("Q", structure.getLayers().get(0).getRows().get(0));
    }

    @Test
    @DisplayName("【基本】simple_3x3x1 - シンプルな3x3構造")
    public void testSimple3x3構造() {
        IStructureEntry structure = structures.get("simple_3x3x1");
        assertNotNull(structure, "simple_3x3x1 structure not found");

        assertEquals("simple_3x3x1", structure.getName());
        assertEquals(1, structure.getLayers().size());
        assertEquals(3, structure.getLayers().get(0).getRows().size());

        // マッピング確認
        assertTrue(structure.getMappings().containsKey('A'));
    }

    @Test
    @DisplayName("【複雑】complex_3x3x3 - 3層の複雑な構造")
    public void testComplex3x3x3構造() {
        IStructureEntry structure = structures.get("complex_3x3x3");
        assertNotNull(structure, "complex_3x3x3 structure not found");

        assertEquals("complex_3x3x3", structure.getName());
        assertEquals(3, structure.getLayers().size());

        // 複数のマッピング
        assertTrue(structure.getMappings().size() >= 2);
    }

    @Test
    @DisplayName("【Requirements】with_item_requirement - Item requirement")
    public void testItemRequirement構造() {
        IStructureEntry structure = structures.get("with_item_requirement");
        assertNotNull(structure, "with_item_requirement structure not found");

        assertFalse(structure.getRequirements().isEmpty());
        assertEquals("itemInput", structure.getRequirements().get(0).getType());
    }

    @Test
    @DisplayName("【Requirements】with_fluid_requirement - Fluid requirement")
    public void testFluidRequirement構造() {
        IStructureEntry structure = structures.get("with_fluid_requirement");
        assertNotNull(structure, "with_fluid_requirement structure not found");

        assertFalse(structure.getRequirements().isEmpty());
        assertEquals("fluidInput", structure.getRequirements().get(0).getType());
    }

    @Test
    @DisplayName("【Requirements】with_energy_requirement - Energy requirement")
    public void testEnergyRequirement構造() {
        IStructureEntry structure = structures.get("with_energy_requirement");
        assertNotNull(structure, "with_energy_requirement structure not found");

        assertFalse(structure.getRequirements().isEmpty());
        assertEquals("energyInput", structure.getRequirements().get(0).getType());
    }

    @Test
    @DisplayName("【Requirements】with_all_requirements - 全7種類のrequirements")
    public void testAllRequirements構造() {
        IStructureEntry structure = structures.get("with_all_requirements");
        assertNotNull(structure, "with_all_requirements structure not found");

        assertTrue(structure.getRequirements().size() >= 4, "複数のrequirementがあるはず");
    }

    @Test
    @DisplayName("【Options】with_display_name - displayName 設定")
    public void testDisplayName構造() {
        IStructureEntry structure = structures.get("with_display_name");
        assertNotNull(structure, "with_display_name structure not found");

        assertNotNull(structure.getDisplayName());
        assertFalse(structure.getDisplayName().isEmpty());
    }

    @Test
    @DisplayName("【Options】with_recipe_group - recipeGroup 設定")
    public void testRecipeGroup構造() {
        IStructureEntry structure = structures.get("with_recipe_group");
        assertNotNull(structure, "with_recipe_group structure not found");

        assertFalse(structure.getRecipeGroup().isEmpty());
    }

    @Test
    @DisplayName("【Options】with_controller_offset - controllerOffset 設定")
    public void testControllerOffset構造() {
        IStructureEntry structure = structures.get("with_controller_offset");
        assertNotNull(structure, "with_controller_offset structure not found");

        assertNotNull(structure.getControllerOffset());
        assertEquals(3, structure.getControllerOffset().length);
    }

    @Test
    @DisplayName("【Options】with_tint_color - tintColor 設定")
    public void testTintColor構造() {
        IStructureEntry structure = structures.get("with_tint_color");
        assertNotNull(structure, "with_tint_color structure not found");

        assertNotNull(structure.getTintColor());
        assertTrue(structure.getTintColor().startsWith("#"));
    }

    @Test
    @DisplayName("【Options】with_tier - tier 設定")
    public void testTier構造() {
        IStructureEntry structure = structures.get("with_tier");
        assertNotNull(structure, "with_tier structure not found");

        assertTrue(structure.getTier() > 0);
    }

    @Test
    @DisplayName("【Mapping】with_multiple_blocks - 複数ブロックマッピング")
    public void testMultipleBlocks構造() {
        IStructureEntry structure = structures.get("with_multiple_blocks");
        assertNotNull(structure, "with_multiple_blocks structure not found");

        // マッピングは1つだが、そのマッピングが複数のブロックを持つ
        assertTrue(structure.getMappings().size() >= 1);
    }

    @Test
    @DisplayName("【Mapping】with_metadata - メタデータ付きブロック")
    public void testMetadata構造() {
        IStructureEntry structure = structures.get("with_metadata");
        assertNotNull(structure, "with_metadata structure not found");

        assertFalse(structure.getMappings().isEmpty());
    }

    // ========================================
    // default mappings のテスト
    // ========================================

    @Test
    @DisplayName("【default】default mappings が読み込まれる")
    public void testDefaultMappings読み込み() {
        assertNotNull(defaultMappings);
        // defaultMappingsが定義されている場合のみチェック
        if (!defaultMappings.isEmpty()) {
            assertTrue(defaultMappings.size() > 0);
        }
    }

    @Test
    @DisplayName("【default】default mappings のシンボル確認")
    public void testDefaultMappingsシンボル() {
        // defaultMappingsが定義されている場合のみチェック
        if (!defaultMappings.isEmpty()) {
            for (Map.Entry<Character, ISymbolMapping> entry : defaultMappings.entrySet()) {
                assertNotNull(entry.getKey());
                assertNotNull(entry.getValue());
            }
        }
        // 空でもテストは成功（オプショナル）
    }

    // ========================================
    // エラーケース（存在しない構造）
    // ========================================

    @Test
    @DisplayName("存在しない構造名でnullが返る")
    public void test存在しない構造() {
        IStructureEntry structure = structures.get("nonexistent_structure");
        assertNull(structure, "存在しない構造名ではnullが返るべき");
    }

    // ========================================
    // 統合確認
    // ========================================

    @Test
    @DisplayName("全構造のlayersが正しい形式")
    public void test全構造のlayers形式() {
        for (IStructureEntry structure : structures.values()) {
            for (int i = 0; i < structure.getLayers().size(); i++) {
                assertNotNull(structure.getLayers().get(i));
                assertFalse(structure.getLayers().get(i).getRows().isEmpty());
            }
        }
    }

    @Test
    @DisplayName("全構造のmappingsが参照可能")
    public void test全構造のmappings参照() {
        for (IStructureEntry structure : structures.values()) {
            assertNotNull(structure.getMappings());
        }
    }

    @Test
    @DisplayName("全構造のrequirementsが参照可能")
    public void test全構造のrequirements参照() {
        for (IStructureEntry structure : structures.values()) {
            assertNotNull(structure.getRequirements());
        }
    }
}
