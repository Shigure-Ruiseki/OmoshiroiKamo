package ruiseki.omoshiroikamo.common.recipe;

import net.minecraft.init.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class ItemRecipes {

    public static void init() {
        if (BackportConfigs.useEnvironmentalTech) {

            // Crystal T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    "GFG",
                    "FDF",
                    "GFG",
                    'G',
                    "dyeCyan",
                    'F',
                    Items.flint,
                    'D',
                    "gemDiamond"));

            // Photovoltaic Cell
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.PHOTOVOLTAIC_CELL.getItem(),
                    " L ",
                    "LQL",
                    " L ",
                    'L',
                    "gemLapis",
                    'Q',
                    "gemQuartz"));

            // Stabilized Ender Pear
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.STABILIZED_ENDER_PEAR.getItem(),
                    " P ",
                    "PIP",
                    " P ",
                    'P',
                    "pearlEnder",
                    'I',
                    "blockIron"));

            // Assembler
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 1)));

            // Assembler
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1)));

            // Assembler
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));
        }
    }

}
