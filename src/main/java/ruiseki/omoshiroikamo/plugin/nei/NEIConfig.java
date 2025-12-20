package ruiseki.omoshiroikamo.plugin.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.container.BackpackGuiContainer;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.plugin.nei.overlay.BackpackOverlay;
import ruiseki.omoshiroikamo.plugin.nei.positioner.BackpackPositioner;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenThrowsRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.cow.CowBreedingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.cow.CowMilkingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner.*;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (BackportConfigs.useEnvironmentalTech) {
            // Register Ore/Res Extractors - single class parameterized by tier
            for (int i = 0; i < 6; i++) {
                QuantumOreExtractorRecipeHandler ore = new QuantumOreExtractorRecipeHandler(i);
                registerHandler(ore);
                API.addRecipeCatalyst(ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i), ore.getRecipeID());

                QuantumResExtractorRecipeHandler res = new QuantumResExtractorRecipeHandler(i);
                registerHandler(res);
                API.addRecipeCatalyst(ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i), res.getRecipeID());
            }
        }
        if (BackportConfigs.useChicken) {
            registerHandler(new ChickenLayingRecipeHandler());
            registerHandler(new ChickenBreedingRecipeHandler());
            registerHandler(new ChickenDropsRecipeHandler());
            registerHandler(new ChickenThrowsRecipeHandler());
        }

        if (BackportConfigs.useCow) {
            registerHandler(new CowBreedingRecipeHandler());
            registerHandler(new CowMilkingRecipeHandler());
        }

        if (BackportConfigs.useBackpack) {
            API.registerGuiOverlay(BackpackGuiContainer.class, "crafting", new BackpackPositioner());
            API.registerGuiOverlayHandler(BackpackGuiContainer.class, new BackpackOverlay(), "crafting");
        }
    }

    protected static void registerHandler(IRecipeHandlerBase handler) {
        handler.prepare();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    @Override
    public String getName() {
        return LibMisc.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return LibMisc.VERSION;
    }
}
