package ruiseki.omoshiroikamo.plugin.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackpackGuiContainer;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackOverlay;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackPositioner;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenThrowsRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowMilkingRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.integration.nei.LootFabricatorRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.integration.nei.SimulationChamberRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.quantumExtractor.QuantumOreExtractorRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.quantumExtractor.QuantumResExtractorRecipeHandler;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        Logger.info("Loading NeiConfig: {}", getName());
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

        if (BackportConfigs.useDML) {
            registerHandler(new LootFabricatorRecipeHandler());
            registerHandler(new SimulationChamberRecipeHandler());
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
