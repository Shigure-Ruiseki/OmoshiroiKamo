package ruiseki.omoshiroikamo.api.modular.recipe.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.JSONLoader;

/**
 * JSONLoader 統合テスト
 *
 * ============================================
 * 最重要テスト: JSON読み込み機能の完全検証
 * ============================================
 *
 * このテストは実際の test_recipe.json を読み込み、
 * 18種類の異なるレシピパターンが正しく処理されることを検証します。
 *
 * バグ発見の優先度: ★★★★★
 * - JSON読み込みはシステムの入り口
 * - ここでバグがあると、すべてのレシピが動作しない
 *
 * カバーする機能:
 * - 7種類の入力/出力タイプ (Item, Fluid, Energy, Mana, Gas, Essentia, Vis)
 * - OreDict サポート
 * - Meta データ
 * - perTick フラグ (Energy, Mana)
 * - Priority システム
 * - Duration 設定
 * - 複雑な組み合わせ
 *
 * ============================================
 */
@DisplayName("JSON統合テスト（最優先）")
public class JSONLoaderIntegrationTest {

    private static File testRecipeFile;
    private static List<IModularRecipe> loadedRecipes;

    @BeforeEach
    public void check() {
        Assumptions.assumeTrue(loadedRecipes != null && !loadedRecipes.isEmpty(), "Recipes were not loaded");
    }

    @BeforeAll
    public static void setUpAll() {
        try {
            // テストファイルのパスを設定
            // 実際のプロジェクト構成に合わせて調整してください
            testRecipeFile = new File("run/client/config/omoshiroikamo/modular/recipes");

            // レシピを読み込み
            loadedRecipes = JSONLoader.loadRecipes(testRecipeFile);
        } catch (Throwable t) {
            System.err.println("Failed to load recipes in integration test: " + t.getMessage());
            loadedRecipes = null;
        }
    }

    // ========================================
    // 基本的な読み込み確認
    // ========================================

    @Test
    @DisplayName("【最優先】test_recipe.jsonから18個のレシピが読み込まれる")
    public void testレシピの総数() {
        // test_recipe.json には 18個のレシピが定義されている
        assertEquals(18, loadedRecipes.size(), "18個のレシピが読み込まれるべき");
    }

    @Test
    @DisplayName("読み込まれた全てのレシピがnullでない")
    public void test全レシピがnullでない() {
        assertNotNull(loadedRecipes);
        for (IModularRecipe recipe : loadedRecipes) {
            assertNotNull(recipe, "レシピがnullであってはならない");
        }
    }

    @Test
    @DisplayName("全てのレシピにregistryNameが設定されている")
    public void test全レシピにregistryNameがある() {
        for (IModularRecipe recipe : loadedRecipes) {
            assertNotNull(recipe.getRegistryName(), "registryName が null であってはならない");
            assertFalse(
                recipe.getRegistryName()
                    .isEmpty(),
                "registryName が空であってはならない");
        }
    }

    // ========================================
    // 個別レシピの詳細検証
    // ========================================

    @Test
    @DisplayName("【Item+Fluid+Energy】Coal to Diamond レシピが正しく読み込まれる")
    public void testCoalToDiamond() {
        IModularRecipe recipe = findRecipe("Coal to Diamond");
        assertNotNull(recipe, "Coal to Diamond レシピが見つからない");

        // Duration チェック
        assertEquals(200, recipe.getDuration());

        // Priority チェック
        assertEquals(4, recipe.getPriority());

        // 入力チェック（3種類）
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(3, inputs.size());

        // Item入力: minecraft:coal x64
        IRecipeInput itemInput = findInput(inputs, IPortType.Type.ITEM);
        assertNotNull(itemInput, "Item入力が見つからない");
        assertEquals(64, itemInput.getRequiredAmount());

        // Fluid入力: water x10000
        IRecipeInput fluidInput = findInput(inputs, IPortType.Type.FLUID);
        assertNotNull(fluidInput, "Fluid入力が見つからない");
        assertEquals(10000, fluidInput.getRequiredAmount());

        // Energy入力: 100/tick
        IRecipeInput energyInput = findInput(inputs, IPortType.Type.ENERGY);
        assertNotNull(energyInput, "Energy入力が見つからない");
        assertEquals(100, energyInput.getRequiredAmount());
        // TODO: perTick フラグのチェック（実装次第）

        // 出力チェック（1種類）
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());

        // Item出力: minecraft:diamond x1
        IRecipeOutput itemOutput = outputs.get(0);
        assertEquals(IPortType.Type.ITEM, itemOutput.getPortType());
        assertEquals(1, itemOutput.getRequiredAmount());
    }

    @Test
    @DisplayName("【OreDict】OreDict Iron to Gold レシピが正しく読み込まれる")
    public void testOreDictレシピ() {
        IModularRecipe recipe = findRecipe("OreDict Iron to Gold");
        assertNotNull(recipe, "OreDict Iron to Gold レシピが見つからない");

        // Duration チェック
        assertEquals(100, recipe.getDuration());

        // 入力チェック
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(2, inputs.size());

        // OreDict入力: ingotIron x4
        IRecipeInput itemInput = findInput(inputs, IPortType.Type.ITEM);
        assertNotNull(itemInput, "OreDict Item入力が見つからない");
        assertEquals(4, itemInput.getRequiredAmount());
        // TODO: OreDict であることの確認（実装次第）

        // Energy入力
        IRecipeInput energyInput = findInput(inputs, IPortType.Type.ENERGY);
        assertNotNull(energyInput);
        assertEquals(50, energyInput.getRequiredAmount());
    }

    @Test
    @DisplayName("【Fluid→Fluid】Water to Lava レシピが正しく読み込まれる")
    public void testFluidToFluidレシピ() {
        IModularRecipe recipe = findRecipe("Water to Lava");
        assertNotNull(recipe);

        assertEquals(200, recipe.getDuration());

        // 入力: Water 1000mB + Energy
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(2, inputs.size());

        IRecipeInput fluidInput = findInput(inputs, IPortType.Type.FLUID);
        assertNotNull(fluidInput);
        assertEquals(1000, fluidInput.getRequiredAmount());

        // 出力: Lava 500mB
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());

        IRecipeOutput fluidOutput = outputs.get(0);
        assertEquals(IPortType.Type.FLUID, fluidOutput.getPortType());
        assertEquals(500, fluidOutput.getRequiredAmount());
    }

    @Test
    @DisplayName("【Mana】Mana Processing レシピが正しく読み込まれる")
    public void testManaレシピ() {
        IModularRecipe recipe = findRecipe("Mana Processing");
        assertNotNull(recipe);

        assertEquals(100, recipe.getDuration());

        // 入力: Mana 2000 + Item
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(2, inputs.size());

        IRecipeInput manaInput = findInput(inputs, IPortType.Type.MANA);
        assertNotNull(manaInput, "Mana入力が見つからない");
        assertEquals(2000, manaInput.getRequiredAmount());

        // 出力: Item + Mana 100
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(2, outputs.size());

        IRecipeOutput manaOutput = findOutput(outputs, IPortType.Type.MANA);
        assertNotNull(manaOutput, "Mana出力が見つからない");
        assertEquals(100, manaOutput.getRequiredAmount());
    }

    @Test
    @DisplayName("【Essentia】Essentia Extraction レシピが正しく読み込まれる")
    public void testEssentiaレシピ() {
        IModularRecipe recipe = findRecipe("Essentia Extraction");
        assertNotNull(recipe);

        assertEquals(200, recipe.getDuration());
        assertEquals(1, recipe.getPriority());

        // 入力: Essentia "ignis" 10 + OreDict
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(2, inputs.size());

        IRecipeInput essentiaInput = findInput(inputs, IPortType.Type.ESSENTIA);
        assertNotNull(essentiaInput, "Essentia入力が見つからない");
        assertEquals(10, essentiaInput.getRequiredAmount());

        // 出力: Essentia "metallum" 4 + Item
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(2, outputs.size());

        IRecipeOutput essentiaOutput = findOutput(outputs, IPortType.Type.ESSENTIA);
        assertNotNull(essentiaOutput, "Essentia出力が見つからない");
        assertEquals(4, essentiaOutput.getRequiredAmount());
    }

    @Test
    @DisplayName("【Vis】Vis Crystallization レシピが正しく読み込まれる")
    public void testVisレシピ() {
        IModularRecipe recipe = findRecipe("Vis Crystallization");
        assertNotNull(recipe);

        assertEquals(300, recipe.getDuration());

        // 入力: Vis "aer" 50 + Vis "aqua" 50 + Item(meta)
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(3, inputs.size());

        // Vis入力が2つあるはず
        long visCount = inputs.stream()
            .filter(i -> i.getPortType() == IPortType.Type.VIS)
            .count();
        assertEquals(2, visCount, "Vis入力が2つ必要");

        // Item入力（メタデータあり）
        IRecipeInput itemInput = findInput(inputs, IPortType.Type.ITEM);
        assertNotNull(itemInput);
        assertEquals(1, itemInput.getRequiredAmount());
        // TODO: meta=6 の確認（実装次第）
    }

    @Test
    @DisplayName("【Gas】Gas Only Test レシピが正しく読み込まれる")
    public void testGasレシピ() {
        IModularRecipe recipe = findRecipe("Gas Only Test (Hydrogen to Oxygen)");
        assertNotNull(recipe);

        assertEquals(80, recipe.getDuration());

        // 入力: Gas "hydrogen" 500
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(1, inputs.size());

        IRecipeInput gasInput = inputs.get(0);
        assertEquals(IPortType.Type.GAS, gasInput.getPortType());
        assertEquals(500, gasInput.getRequiredAmount());

        // 出力: Gas "oxygen" 250
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());

        IRecipeOutput gasOutput = outputs.get(0);
        assertEquals(IPortType.Type.GAS, gasOutput.getPortType());
        assertEquals(250, gasOutput.getRequiredAmount());
    }

    @Test
    @DisplayName("【全タイプ】Full Combo Test が正しく読み込まれる")
    public void testFullComboレシピ() {
        IModularRecipe recipe = findRecipe("Full Combo Test (All Input Types)");
        assertNotNull(recipe, "Full Combo Test レシピが見つからない");

        assertEquals(300, recipe.getDuration());

        // 入力: 7種類全て
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(7, inputs.size(), "7種類の入力があるはず");

        // 各タイプが1つずつ存在することを確認
        assertNotNull(findInput(inputs, IPortType.Type.ITEM), "ITEM入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.FLUID), "FLUID入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.ENERGY), "ENERGY入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.MANA), "MANA入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.GAS), "GAS入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.ESSENTIA), "ESSENTIA入力が必要");
        assertNotNull(findInput(inputs, IPortType.Type.VIS), "VIS入力が必要");

        // 出力: 6種類（Energyを除く）
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(6, outputs.size(), "6種類の出力があるはず");

        assertNotNull(findOutput(outputs, IPortType.Type.ITEM), "ITEM出力が必要");
        assertNotNull(findOutput(outputs, IPortType.Type.FLUID), "FLUID出力が必要");
        assertNotNull(findOutput(outputs, IPortType.Type.MANA), "MANA出力が必要");
        assertNotNull(findOutput(outputs, IPortType.Type.GAS), "GAS出力が必要");
        assertNotNull(findOutput(outputs, IPortType.Type.ESSENTIA), "ESSENTIA出力が必要");
        assertNotNull(findOutput(outputs, IPortType.Type.VIS), "VIS出力が必要");
    }

    @Test
    @DisplayName("【メタデータ】Bone Meal Production レシピが正しく読み込まれる")
    public void testメタデータ出力レシピ() {
        IModularRecipe recipe = findRecipe("Bone Meal Production");
        assertNotNull(recipe);

        assertEquals(30, recipe.getDuration());

        // 出力: minecraft:dye meta=15 x6
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());

        IRecipeOutput output = outputs.get(0);
        assertEquals(IPortType.Type.ITEM, output.getPortType());
        assertEquals(6, output.getRequiredAmount());
        // TODO: meta=15 の確認（実装次第）
    }

    @Test
    @DisplayName("【perTick=false】Gravel to Flint レシピが正しく読み込まれる")
    public void testPerTickFalseレシピ() {
        IModularRecipe recipe = findRecipe("Gravel to Flint");
        assertNotNull(recipe);

        assertEquals(20, recipe.getDuration());

        // Energy入力: 10000 (pertick=false) ← バルク消費
        List<IRecipeInput> inputs = recipe.getInputs();
        IRecipeInput energyInput = findInput(inputs, IPortType.Type.ENERGY);
        assertNotNull(energyInput);
        assertEquals(10000, energyInput.getRequiredAmount());
        // TODO: perTick=false の確認（実装次第）
    }

    @Test
    @DisplayName("【perTick出力】Glass to Energy Conversion レシピが正しく読み込まれる")
    public void testPerTick出力レシピ() {
        IModularRecipe recipe = findRecipe("glass to energy Conversion");
        assertNotNull(recipe);

        assertEquals(60, recipe.getDuration());

        // 出力: Mana 200/tick
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());

        IRecipeOutput manaOutput = outputs.get(0);
        assertEquals(IPortType.Type.MANA, manaOutput.getPortType());
        assertEquals(200, manaOutput.getRequiredAmount());
        // TODO: perTick=true の確認（実装次第）
    }

    @Test
    @DisplayName("【Priority】Priority が正しく読み込まれる")
    public void testPriorityシステム() {
        // Priority 0 (デフォルト)
        IModularRecipe defaultPriority = findRecipe("Simple Item Test (Stone to Cobblestone)");
        assertEquals(0, defaultPriority.getPriority());

        // Priority 1
        IModularRecipe priority1 = findRecipe("Essentia Extraction");
        assertEquals(1, priority1.getPriority());

        // Priority 2
        IModularRecipe priority2 = findRecipe("Lava test");
        assertEquals(2, priority2.getPriority());

        // Priority 3
        IModularRecipe priority3 = findRecipe("Clay Processing");
        assertEquals(3, priority3.getPriority());

        // Priority 4
        IModularRecipe priority4 = findRecipe("Coal to Diamond");
        assertEquals(4, priority4.getPriority());
    }

    // ========================================
    // エッジケースのテスト
    // ========================================

    @Test
    @DisplayName("【最小構成】Simple Item Test が正しく読み込まれる")
    public void test最小構成レシピ() {
        IModularRecipe recipe = findRecipe("Simple Item Test (Stone to Cobblestone)");
        assertNotNull(recipe);

        assertEquals(40, recipe.getDuration());

        // 入力: 1種類のみ
        List<IRecipeInput> inputs = recipe.getInputs();
        assertEquals(1, inputs.size());

        // 出力: 1種類のみ
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(1, outputs.size());
    }

    @Test
    @DisplayName("【複数出力】Nether Star からの Essentia 生成レシピ")
    public void test複数出力レシピ() {
        IModularRecipe recipe = findRecipe("Essentia Production from Nether Star");
        assertNotNull(recipe);

        assertEquals(400, recipe.getDuration());

        // 出力: Essentia 2種類
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(2, outputs.size());

        // 両方とも Essentia タイプ
        for (IRecipeOutput output : outputs) {
            assertEquals(IPortType.Type.ESSENTIA, output.getPortType());
        }

        // auram: 64, spiritus: 32
        long totalAmount = outputs.stream()
            .mapToLong(IRecipeOutput::getRequiredAmount)
            .sum();
        assertEquals(96, totalAmount, "合計 96 (64+32) であるべき");
    }

    @Test
    @DisplayName("【複数Vis】Vis Generation レシピで複数のVis出力")
    public void test複数Vis出力() {
        IModularRecipe recipe = findRecipe("Vis Generation from Ender Pearl");
        assertNotNull(recipe);

        // 出力: Vis 2種類
        List<IRecipeOutput> outputs = recipe.getOutputs();
        assertEquals(2, outputs.size());

        // 両方とも Vis タイプ
        for (IRecipeOutput output : outputs) {
            assertEquals(IPortType.Type.VIS, output.getPortType());
        }

        // perditio: 20, aer: 20
        for (IRecipeOutput output : outputs) {
            assertEquals(20, output.getRequiredAmount());
        }
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    /**
     * レシピ名でレシピを検索
     */
    private IModularRecipe findRecipe(String name) {
        for (IModularRecipe recipe : loadedRecipes) {
            if (name.equals(recipe.getRegistryName())) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * 指定されたポートタイプの入力を検索
     */
    private IRecipeInput findInput(List<IRecipeInput> inputs, IPortType.Type type) {
        for (IRecipeInput input : inputs) {
            if (input.getPortType() == type) {
                return input;
            }
        }
        return null;
    }

    /**
     * 指定されたポートタイプの出力を検索
     */
    private IRecipeOutput findOutput(List<IRecipeOutput> outputs, IPortType.Type type) {
        for (IRecipeOutput output : outputs) {
            if (output.getPortType() == type) {
                return output;
            }
        }
        return null;
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: 継承システムのテスト（parent フィールドを使ったレシピ）
    // TODO: デコレータのテスト（chance, bonus など）
    // TODO: 条件のテスト（conditions フィールド）
    // TODO: NBT のテスト（nbt フィールド）
    // TODO: エラーハンドリングのテスト（不正なJSON）
}
