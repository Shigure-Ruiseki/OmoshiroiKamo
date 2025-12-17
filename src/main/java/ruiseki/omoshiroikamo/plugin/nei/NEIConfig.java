package ruiseki.omoshiroikamo.plugin.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.container.BackpackGuiContainer;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.Logger;
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
import ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner.QuantumOreExtractorRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner.QuantumResExtractorRecipeHandler;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        Logger.info("Loading NeiConfig: " + getName());
        if (BackportConfigs.useEnvironmentalTech) {
            for (int i = 0; i < QuantumExtractorRecipes.MAX_TIER; i++) {
                registerHandler(new QuantumOreExtractorRecipeHandler(i));
                registerHandler(new QuantumResExtractorRecipeHandler(i));
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
