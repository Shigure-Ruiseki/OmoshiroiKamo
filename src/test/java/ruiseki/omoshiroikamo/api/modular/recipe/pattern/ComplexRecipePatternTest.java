package ruiseki.omoshiroikamo.api.modular.recipe.pattern;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.*;

/**
 * 複雑なレシピパターンのテスト
 *
 * ============================================
 * 様々な複雑パターンを網羅的にテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★★☆
 * - 複雑なレシピパターンの組み合わせで予期しないバグが発生しやすい
 * - 7種類全ての入出力タイプの組み合わせをテスト
 * - OreDict、メタデータ、特殊ケースを包括的に検証
 *
 * カバーする機能:
 * - 全7種類の入力タイプ混在
 * - 全6種類の出力タイプ混在（Energyを除く）
 * - OreDict入力の様々なパターン
 * - メタデータ付きアイテム
 * - perTickフラグのバリエーション
 * - 同じタイプの複数入出力
 *
 * ============================================
 */
@DisplayName("複雑なレシピパターンテスト")
public class ComplexRecipePatternTest {

    @BeforeEach
    public void setup() {
        try {
            Assumptions.assumeTrue(Items.iron_ingot != null, "Minecraft registry not initialized");
        } catch (Throwable t) {
            Assumptions.assumeTrue(false, "Minecraft registry failed: " + t.getMessage());
        }
    }

    // ========================================
    // 全タイプ混在パターン
    // ========================================

    @Test
    @DisplayName("【最重要】全7種類の入力タイプが混在するレシピ")
    public void test全入力タイプ混在() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("all_inputs")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.diamond, 1))
            .addInput(new FluidInput(null)) // テスト環境では FluidRegistry が使えないため null
            .addInput(new EnergyInput(500, true))
            .addInput(new ManaInput(2000, false))
            .addInput(new GasInput("oxygen", 500))
            .addInput(new EssentiaInput("ignis", 10))
            .addInput(new VisInput("aer", 50))
            .build();

        // 7種類全て存在する
        assertEquals(
            7,
            recipe.getInputs()
                .size());

        // 各タイプが1つずつ存在
        long itemCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.ITEM)
            .count();
        assertEquals(1, itemCount);

        long fluidCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.FLUID)
            .count();
        assertEquals(1, fluidCount);

        long energyCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.ENERGY)
            .count();
        assertEquals(1, energyCount);

        long manaCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.MANA)
            .count();
        assertEquals(1, manaCount);

        long gasCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.GAS)
            .count();
        assertEquals(1, gasCount);

        long essentiaCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.ESSENTIA)
            .count();
        assertEquals(1, essentiaCount);

        long visCount = recipe.getInputs()
            .stream()
            .filter(i -> i.getPortType() == IPortType.Type.VIS)
            .count();
        assertEquals(1, visCount);
    }

    @Test
    @DisplayName("【最重要】全6種類の出力タイプが混在するレシピ")
    public void test全出力タイプ混在() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("all_outputs")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ItemOutput(new ItemStack(Items.diamond, 1)))
            .addOutput(new FluidOutput("lava", 500))
            .addOutput(new ManaOutput(1000, false))
            .addOutput(new GasOutput("hydrogen", 250))
            .addOutput(new EssentiaOutput("metallum", 5))
            .addOutput(new VisOutput("ordo", 25))
            .build();

        // 6種類全て存在する（Energyは出力に含まれないのが通常）
        assertEquals(
            6,
            recipe.getOutputs()
                .size());

        // 各タイプが1つずつ存在
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.ITEM)
                .count());
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.FLUID)
                .count());
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.MANA)
                .count());
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.GAS)
                .count());
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.ESSENTIA)
                .count());
        assertEquals(
            1,
            recipe.getOutputs()
                .stream()
                .filter(o -> o.getPortType() == IPortType.Type.VIS)
                .count());
    }

    // ========================================
    // 同一タイプの複数入出力
    // ========================================

    @Test
    @DisplayName("同じタイプ（Item）の入力が複数ある")
    public void test複数Item入力() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("multi_items")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, 4))
            .addInput(new ItemInput(Items.gold_ingot, 2))
            .addInput(new ItemInput(Items.diamond, 1))
            .build();

        // 3つのItem入力
        assertEquals(
            3,
            recipe.getInputs()
                .size());

        // 全てItemタイプ
        for (IRecipeInput input : recipe.getInputs()) {
            assertEquals(IPortType.Type.ITEM, input.getPortType());
        }
    }

    @Test
    @DisplayName("同じタイプ（Essentia）の入力が複数ある")
    public void test複数Essentia入力() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("multi_essentia")
            .recipeGroup("test")
            .duration(100)
            .addInput(new EssentiaInput("ignis", 10))
            .addInput(new EssentiaInput("aqua", 10))
            .addInput(new EssentiaInput("terra", 10))
            .build();

        assertEquals(
            3,
            recipe.getInputs()
                .size());

        // 全てEssentiaタイプ
        for (IRecipeInput input : recipe.getInputs()) {
            assertEquals(IPortType.Type.ESSENTIA, input.getPortType());
        }
    }

    @Test
    @DisplayName("同じタイプ（Vis）の出力が複数ある")
    public void test複数Vis出力() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("multi_vis_output")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new VisOutput("aer", 20))
            .addOutput(new VisOutput("ignis", 20))
            .addOutput(new VisOutput("aqua", 20))
            .addOutput(new VisOutput("terra", 20))
            .addOutput(new VisOutput("ordo", 20))
            .addOutput(new VisOutput("perditio", 20))
            .build();

        // 6種類のVis出力
        assertEquals(
            6,
            recipe.getOutputs()
                .size());

        // 全てVisタイプ
        for (IRecipeOutput output : recipe.getOutputs()) {
            assertEquals(IPortType.Type.VIS, output.getPortType());
        }
    }

    // ========================================
    // OreDictパターン
    // ========================================

    @Test
    @DisplayName("【OreDict】複数のOreDict入力")
    public void test複数OreDict入力() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("multi_oredict")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput("ingotIron", 4))
            .addInput(new ItemInput("ingotGold", 2))
            .addInput(new ItemInput("gemDiamond", 1))
            .build();

        assertEquals(
            3,
            recipe.getInputs()
                .size());

        // 全てItemタイプ（OreDictもItemInputとして扱われる）
        for (IRecipeInput input : recipe.getInputs()) {
            assertEquals(IPortType.Type.ITEM, input.getPortType());
        }
    }

    @Test
    @DisplayName("【OreDict】通常アイテムとOreDictの混在")
    public void testOreDictと通常アイテムの混在() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("mixed_oredict")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.diamond, 1)) // 通常アイテム
            .addInput(new ItemInput("ingotIron", 4)) // OreDict
            .addInput(new ItemInput(Items.redstone, 8)) // 通常アイテム
            .addInput(new ItemInput("dustGold", 2)) // OreDict
            .build();

        assertEquals(
            4,
            recipe.getInputs()
                .size());
    }

    // ========================================
    // メタデータパターン
    // ========================================

    @Test
    @DisplayName("【メタ】メタデータ付きアイテム入力")
    public void testメタデータ入力() {
        // Wool (meta 0-15) など、メタデータを持つアイテム
        ItemStack whiteWool = new ItemStack(Blocks.wool, 1, 0);
        ItemStack orangeWool = new ItemStack(Blocks.wool, 1, 1);

        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("meta_input")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(whiteWool))
            .addInput(new ItemInput(orangeWool))
            .build();

        assertEquals(
            2,
            recipe.getInputs()
                .size());
    }

    @Test
    @DisplayName("【メタ】メタデータ付きアイテム出力")
    public void testメタデータ出力() {
        // Dye (meta 15 = Bone Meal) など
        ItemStack boneMeal = new ItemStack(Items.dye, 6, 15);

        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("meta_output")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ItemOutput(boneMeal))
            .build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size());

        IRecipeOutput output = recipe.getOutputs()
            .get(0);
        assertEquals(6, output.getRequiredAmount());
    }

    // ========================================
    // perTickフラグのバリエーション
    // ========================================

    @Test
    @DisplayName("【perTick】Energy入力でperTick=trueとfalseの混在")
    public void testEnergyPerTickバリエーション() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("energy_mixed")
            .recipeGroup("test")
            .duration(100)
            .addInput(new EnergyInput(100, true)) // 毎tick 100
            .addInput(new EnergyInput(10000, false)) // 一括 10000
            .build();

        assertEquals(
            2,
            recipe.getInputs()
                .size());

        // 両方ともEnergyタイプ
        for (IRecipeInput input : recipe.getInputs()) {
            assertEquals(IPortType.Type.ENERGY, input.getPortType());
        }
    }

    @Test
    @DisplayName("【perTick】Mana入力でperTick=trueとfalseの混在")
    public void testManaPerTickバリエーション() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("mana_mixed")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ManaInput(50, true)) // 毎tick 50
            .addInput(new ManaInput(5000, false)) // 一括 5000
            .build();

        assertEquals(
            2,
            recipe.getInputs()
                .size());

        // 両方ともManaタイプ
        for (IRecipeInput input : recipe.getInputs()) {
            assertEquals(IPortType.Type.MANA, input.getPortType());
        }
    }

    @Test
    @DisplayName("【perTick】Mana出力でperTick=true")
    public void testMana出力PerTick() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("mana_output_pertick")
            .recipeGroup("test")
            .duration(100)
            .addOutput(new ManaOutput(200, true)) // 毎tick 200生成
            .build();

        assertEquals(
            1,
            recipe.getOutputs()
                .size());

        IRecipeOutput output = recipe.getOutputs()
            .get(0);
        assertEquals(IPortType.Type.MANA, output.getPortType());
        assertEquals(200, output.getRequiredAmount());
    }

    // ========================================
    // 極端なケース
    // ========================================

    @Test
    @DisplayName("【極端】大量の入力（10個）")
    public void test大量入力() {
        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("many_inputs")
            .recipeGroup("test")
            .duration(100);

        // 10個の異なる入力
        builder.addInput(new ItemInput(Items.iron_ingot, 1));
        builder.addInput(new ItemInput(Items.gold_ingot, 1));
        builder.addInput(new ItemInput(Items.diamond, 1));
        builder.addInput(
            new FluidInput(
                new net.minecraftforge.fluids.FluidStack(
                    net.minecraftforge.fluids.FluidRegistry.getFluid("water"),
                    1000)));
        builder.addInput(new FluidInput(null));
        builder.addInput(new EnergyInput(100, true));
        builder.addInput(new ManaInput(500, false));
        builder.addInput(new GasInput("oxygen", 100));
        builder.addInput(new EssentiaInput("ignis", 5));
        builder.addInput(new VisInput("aer", 10));

        IModularRecipe recipe = builder.build();

        assertEquals(
            10,
            recipe.getInputs()
                .size());
    }

    @Test
    @DisplayName("【極端】大量の出力（10個）")
    public void test大量出力() {
        ModularRecipe.Builder builder = ModularRecipe.builder()
            .registryName("many_outputs")
            .recipeGroup("test")
            .duration(100);

        // 10個の異なる出力
        builder.addOutput(new ItemOutput(new ItemStack(Items.diamond, 1)));
        builder.addOutput(new ItemOutput(new ItemStack(Items.gold_ingot, 2)));
        builder.addOutput(new ItemOutput(new ItemStack(Items.iron_ingot, 4)));
        builder.addOutput(new FluidOutput("water", 500));
        builder.addOutput(new FluidOutput("lava", 250));
        builder.addOutput(new ManaOutput(1000, false));
        builder.addOutput(new GasOutput("hydrogen", 200));
        builder.addOutput(new GasOutput("oxygen", 100));
        builder.addOutput(new EssentiaOutput("metallum", 3));
        builder.addOutput(new VisOutput("ordo", 15));

        IModularRecipe recipe = builder.build();

        assertEquals(
            10,
            recipe.getOutputs()
                .size());
    }

    @Test
    @DisplayName("【極端】同じアイテムの入力が5個")
    public void test同一アイテム複数入力() {
        IModularRecipe recipe = ModularRecipe.builder()
            .registryName("same_item_multi")
            .recipeGroup("test")
            .duration(100)
            .addInput(new ItemInput(Items.iron_ingot, 1))
            .addInput(new ItemInput(Items.iron_ingot, 2))
            .addInput(new ItemInput(Items.iron_ingot, 4))
            .addInput(new ItemInput(Items.iron_ingot, 8))
            .addInput(new ItemInput(Items.iron_ingot, 16))
            .build();

        // 5つの入力
        assertEquals(
            5,
            recipe.getInputs()
                .size());

        // 全て鉄インゴット
        // 合計: 1 + 2 + 4 + 8 + 16 = 31個
        long totalAmount = recipe.getInputs()
            .stream()
            .mapToLong(IRecipeInput::getRequiredAmount)
            .sum();
        assertEquals(31L, totalAmount);
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: NBT付きアイテムのテスト
    // TODO: 確率付き出力（BonusOutputDecorator）との組み合わせ
    // TODO: 条件付きレシピとの組み合わせ
    // TODO: 継承を使った複雑パターン
}
