package ruiseki.omoshiroikamo.core.integration.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.RecipeCatalysts;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.nei.modular.ModularMachineNEIHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
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
import ruiseki.omoshiroikamo.module.ids.client.gui.container.TerminalGuiContainer;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalOverlay;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalPositioner;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryItems;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
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
        if (BackportConfigs.enableMachinery && LibMods.BlockRenderer6343.isLoaded()) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder(ModularMachineNEIHandler.class, "Modular Machine", LibMisc.MOD_ID)
                    .setDisplayStack(new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock()))
                    .setHeight(168)
                    .setWidth(192)
                    .setShiftY(6)
                    .build());
            Logger.info("Registered Modular Machine NEI handler info");
        }
    }

    @Override
    public void loadConfig() {
        Logger.info("Loading NeiConfig: {}", getName());
        if (BackportConfigs.enableMultiBlock) {
            // TODO: Change Void Miner structure preview to Tier-based because buttons do not work now
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
        if (BackportConfigs.enableChicken) {
            registerHandler(new ChickenLayingRecipeHandler());
            registerHandler(new ChickenBreedingRecipeHandler());
            registerHandler(new ChickenDropsRecipeHandler());
            registerHandler(new ChickenThrowsRecipeHandler());
        }

        if (BackportConfigs.enableCow) {
            registerHandler(new CowBreedingRecipeHandler());
            registerHandler(new CowMilkingRecipeHandler());
        }

        if (BackportConfigs.enableBackpack) {
            API.registerGuiOverlay(BackpackGuiContainer.class, "crafting", new BackpackPositioner());
            API.registerGuiOverlayHandler(BackpackGuiContainer.class, new BackpackOverlay(), "crafting");
        }

        if (BackportConfigs.enableIDs) {
            API.registerGuiOverlay(TerminalGuiContainer.class, "crafting", new TerminalPositioner());
            API.registerGuiOverlayHandler(TerminalGuiContainer.class, new TerminalOverlay(), "crafting");
        }

        if (BackportConfigs.enableDML) {
            registerHandler(new LootFabricatorRecipeHandler());
            registerHandler(new SimulationChamberRecipeHandler());
        }

        // Register Modular Machine structure preview handlers (one per structure)
        // Only register as usage handlers - structure preview shows on Usage (U), not
        // Recipe (R)
        // TODO: Fix catalyst blueprints appear briefly in left tab then disappear.
        // TODO: Add Recipe of Modular Machines
        // TODO: Enable 'P' button in structure preview (Name is currently null)
        if (BackportConfigs.enableMachinery && LibMods.BlockRenderer6343.isLoaded()) {
            for (String structureName : CustomStructureRegistry.getRegisteredNames()) {
                ModularMachineNEIHandler handler = new ModularMachineNEIHandler(structureName);
                // Register ONLY as usage handler - structure preview is Usage-only
                GuiUsageRecipe.usagehandlers.add(handler);

                // Register blueprint and controller as catalysts
                String overlayId = handler.getOverlayIdentifier();
                List<ItemStack> catalysts = new ArrayList<>();
                catalysts.add(
                    ItemMachineBlueprint.createBlueprint(MachineryItems.MACHINE_BLUEPRINT.getItem(), structureName));
                catalysts.add(new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock()));
                RecipeCatalysts.putRecipeCatalysts(overlayId, catalysts);
            }

            Logger.info(
                "Registered {} Modular Machine NEI handlers",
                CustomStructureRegistry.getRegisteredNames()
                    .size());
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
