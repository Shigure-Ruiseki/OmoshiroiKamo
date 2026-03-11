package ruiseki.omoshiroikamo.module.dml.common.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.recipe.parser.InputParserRegistry;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLRecipeRegistry;
import ruiseki.omoshiroikamo.module.dml.recipe.DataModelInput;
import ruiseki.omoshiroikamo.module.dml.recipe.LootFabricatorRecipe;
import ruiseki.omoshiroikamo.module.dml.recipe.PristineMatterInput;
import ruiseki.omoshiroikamo.module.dml.recipe.SimulationChamberRecipe;

public class DMLRecipes {

    public static void init() {
        // Register custom DML recipe input parsers to the common API
        InputParserRegistry.register("data_model", DataModelInput::fromJson);
        InputParserRegistry.register("pristine_matter", PristineMatterInput::fromJson);

        // DML data loading is now handled by ModModels.init()

        blockRecipes();
        itemRecipes();

        // Generate recipes for registered models
        generateDynamicRecipes();
    }

    private static void generateDynamicRecipes() {
        ModelRegistry.INSTANCE.getItems()
            .forEach(model -> {
                if (model.isEnabled()) {
                    // Generate for Simulation Chamber
                    DMLRecipeRegistry.INSTANCE.addSimulationRecipe(new SimulationChamberRecipe(model));

                    // Generate for Loot Fabricator
                    DMLRecipeRegistry.INSTANCE.addFabricationRecipe(new LootFabricatorRecipe(model));
                }
            });
    }

    public static void blockRecipes() {

        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLBlocks.MACHINE_CASING.getItem(),
                "PIP",
                "ICI",
                "PIP",
                'P',
                DMLItems.SOOT_COVERED_PLATE.getItem(),
                'I',
                "ingotIron",
                'C',
                DMLItems.SOOT_COVERED_REDSTONE.getItem()));

        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLBlocks.LOOT_FABRICATOR.getItem(),
                " G ",
                "DMD",
                "YCY",
                'M',
                DMLBlocks.MACHINE_CASING.getBlock(),
                'G',
                "ingotGold",
                'D',
                "gemDiamond",
                'Y',
                "dyeYellow",
                'C',
                Items.comparator));

        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLBlocks.SIMULATION_CHAMBER.getItem(),
                " G ",
                "PMP",
                "DCD",
                'M',
                DMLBlocks.MACHINE_CASING.getBlock(),
                'P',
                "pearlEnder",
                'G',
                Blocks.glass_pane,
                'C',
                Items.comparator,
                'D',
                "gemLapis"));
    }

    public static void itemRecipes() {

        // Deep Learner
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLItems.DEEP_LEARNER.newItemStack(),
                "ORO",
                "RSR",
                "ODO",
                'O',
                DMLItems.SOOT_COVERED_PLATE.getItem(),
                'D',
                DMLItems.SOOT_COVERED_REDSTONE.getItem(),
                'S',
                Blocks.glass_pane,
                'R',
                Items.repeater));

        // Data Model Blank
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLItems.DATA_MODEL_BLANK.newItemStack(),
                "CEC",
                "RSR",
                "CGC",
                'E',
                Items.repeater,
                'R',
                DMLItems.SOOT_COVERED_REDSTONE.getItem(),
                'G',
                "ingotGold",
                'C',
                "gemLapis",
                'S',
                "stone"));

        // Polymer Clay
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                DMLItems.POLYMER_CLAY.newItemStack(16),
                "GC ",
                "CDC",
                " CI",
                'I',
                "ingotIron",
                'G',
                "ingotGold",
                'D',
                "gemLapis",
                'C',
                "itemClay"));

        // Soot Covered Plate
        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                DMLItems.SOOT_COVERED_PLATE.newItemStack(8),
                DMLItems.SOOT_COVERED_REDSTONE.getItem(),
                "blockObsidian",
                "blockObsidian",
                "blockObsidian"));
    }
}
