package ruiseki.omoshiroikamo.plugin.compat;

import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class EtFuturumCompat {

    public static void init() {
        if (!LibMods.EtFuturum.isLoaded()) {
            return;
        }

        addRecipes();

        Logger.info("Loaded EtFuturum Compat");
    }

    public static void addRecipes() {

        // Crafting
        if (BackportConfigs.useBackpack) {
            // Stack Upgrade Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.STACK_UPGRADE.newItemStack(1, 3),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "blockNetherite",
                    'U',
                    ModItems.STACK_UPGRADE.newItemStack(1, 2)));

            // Everlasting Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.EVERLASTING_UPGRADE.getItem(),
                    "GRG",
                    "RUR",
                    "GRG",
                    'G',
                    ganymedes01.etfuturum.ModItems.END_CRYSTAL.get(),
                    'R',
                    "itemNetherStar",
                    'U',

                    ModItems.BASE_UPGRADE.getItem()));
        }

        if (BackportConfigs.useEnvironmentalTech) {

            // Flight Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_FLIGHT.get(),
                    "EFE",
                    "MNM",
                    "LfL",
                    'E',
                    ganymedes01.etfuturum.ModItems.ELYTRA.get(),
                    'F',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'f',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'L',
                    "itemLeather",
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));
        }
        // SmithingTable

    }

}
