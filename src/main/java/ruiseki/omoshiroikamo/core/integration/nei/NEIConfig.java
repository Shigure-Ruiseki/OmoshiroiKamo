package ruiseki.omoshiroikamo.core.integration.nei;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.nei.modular.ModularMachineNEIHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
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
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.NEIDimensionConfig;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.QuantumOreExtractorRecipeHandler;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.QuantumResExtractorRecipeHandler;

@EventBusSubscriber
@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    /**
     * Register handler info for Modular Machine NEI tab.
     * This controls the appearance of the recipe tab in NEI.
     */
    @SubscribeEvent
    public static void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        if (BackportConfigs.useMachinery) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder(ModularMachineNEIHandler.class, "Modular Machine", LibMisc.MOD_ID)
                    .setDisplayStack(new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock()))
                    .setHeight(168)
                    .setWidth(192)
                    .setMaxRecipesPerPage(1)
                    .setShiftY(6)
                    .build());
            Logger.info("Registered Modular Machine NEI handler info");
        }
    }

    @Override
    public void loadConfig() {
        Logger.info("Loading NeiConfig: {}", getName());
        if (BackportConfigs.useMultiBlock) {
            // Register Ore Extractors
            for (int i = 0; i < 6; i++) {
                QuantumOreExtractorRecipeHandler ore = new QuantumOreExtractorRecipeHandler(i);
                registerHandler(ore);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i), ore.getRecipeID());
                registerDimensionCatalysts(ore.getRecipeID());
            }

            // Register Res Extractors
            for (int i = 0; i < 6; i++) {
                QuantumResExtractorRecipeHandler res = new QuantumResExtractorRecipeHandler(i);
                registerHandler(res);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i), res.getRecipeID());
                registerDimensionCatalysts(res.getRecipeID());
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

        // Register Modular Machine structure preview handler
        if (BackportConfigs.useMachinery) {
            ModularMachineNEIHandler modularHandler = new ModularMachineNEIHandler();
            GuiCraftingRecipe.craftinghandlers.add(modularHandler);
            GuiUsageRecipe.usagehandlers.add(modularHandler);
            Logger.info("Registered Modular Machine NEI handler");
        }
    }

    protected static void registerHandler(IRecipeHandlerBase handler) {
        handler.prepare();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    private static void registerDimensionCatalysts(String recipeId) {
        for (NEIDimensionConfig.DimensionEntry dim : NEIDimensionConfig.getDimensions()) {
            ItemStack catalyst = NEIDimensionConfig.getCatalystStack(dim.id);
            if (catalyst != null) {
                API.addRecipeCatalyst(catalyst, recipeId);
            }
        }
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
