package ruiseki.omoshiroikamo.core.integration.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.RecipeCatalysts;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.nei.modular.ModularMachineNEIHandler;
import ruiseki.omoshiroikamo.core.integration.nei.modular.ModularRecipeNEIHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackpackGuiContainer;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackOverlay;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackPositioner;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenThrowsRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowMilkingRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.integration.nei.LootFabricatorRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.integration.nei.SimulationChamberRecipeHandler;
import ruiseki.omoshiroikamo.module.ids.client.gui.container.TerminalGuiContainer;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalOverlay;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalPositioner;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryItems;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;
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
            // TODO: Change Void Miner structure preview to Tier-based
            // buttons do not work now

            // Register Ore Extractors
            for (int i = 0; i < 6; i++) {
                QuantumOreExtractorRecipeHandler ore = new QuantumOreExtractorRecipeHandler(i);
                registerHandler(ore);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i), ore.getRecipeID());
                registerDimensionCatalysts(ore.getRecipeID());
                sendHandlerInfo(ore.getRecipeID(), MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i), 48, 8);
            }

            // Register Res Extractors
            for (int i = 0; i < 6; i++) {
                QuantumResExtractorRecipeHandler res = new QuantumResExtractorRecipeHandler(i);
                registerHandler(res);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i), res.getRecipeID());
                registerDimensionCatalysts(res.getRecipeID());
                sendHandlerInfo(res.getRecipeID(), MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i), 48, 8);
            }
        }
        if (BackportConfigs.enableChickens) {
            registerHandler(new ChickenLayingRecipeHandler());
            sendHandlerImage(
                ChickenLayingRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/laying_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);
            sendCatalyst(ChickenLayingRecipeHandler.UID, ChickensBlocks.ROOST.newItemStack());

            registerHandler(new ChickenBreedingRecipeHandler());
            sendHandlerImage(
                ChickenBreedingRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/breeding_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);
            sendCatalyst(ChickenBreedingRecipeHandler.UID, ChickensBlocks.BREEDER.newItemStack());

            registerHandler(new ChickenDropsRecipeHandler());
            sendHandlerImage(
                ChickenDropsRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/drops_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);

            registerHandler(new ChickenThrowsRecipeHandler());
            sendHandlerImage(
                ChickenThrowsRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/throws_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);
        }

        if (BackportConfigs.enableCows) {
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
            sendHandlerInfo(LootFabricatorRecipeHandler.UID, DMLBlocks.LOOT_FABRICATOR.newItemStack(), 48, 8);

            registerHandler(new SimulationChamberRecipeHandler());
            sendHandlerInfo(SimulationChamberRecipeHandler.UID, DMLBlocks.SIMULATION_CHAMBER.newItemStack(), 48, 8);
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

            // Register Modular Machine Recipes (JSON)
            registerModularMachineryRecipes();
        }
    }

    private void registerModularMachineryRecipes() {
        // Collect all recipe groups
        List<String> groups = new ArrayList<>();
        List<ModularRecipe> allRecipes = RecipeLoader.getInstance()
            .getAllRecipes();

        if (allRecipes.isEmpty()) {
            Logger.warn("NEIConfig: No Modular Recipes found. RecipeLoader might not have loaded yet.");
        }

        for (ModularRecipe recipe : allRecipes) {
            String group = recipe.getRecipeGroup();
            if (!groups.contains(group)) {
                groups.add(group);
            }
        }

        Logger.info("NEIConfig: Registering " + groups.size() + " Modular Recipe groups.");

        for (String group : groups) {
            ModularRecipeNEIHandler handler = new ModularRecipeNEIHandler(group);
            registerHandler(handler);

            sendHandlerInfo(
                handler.getRecipeID(),
                new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock()),
                100,
                1);
        }
    }

    private void sendHandlerInfo(String handler, ItemStack stack, int height, int recipesPerPage) {
        if (stack == null) return;
        NBTTagCompound tag = new NBTTagCompound();
        GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        if (uid == null) return;

        String regName = uid.modId + ":" + uid.name;
        int meta = stack.getItemDamage();
        if (meta > 0) regName = regName + ":" + meta;

        tag.setString("handler", handler);
        tag.setString("itemName", regName);
        tag.setInteger("handlerHeight", height);
        tag.setInteger("maxRecipesPerPage", recipesPerPage);
        tag.setString("modName", LibMisc.MOD_NAME);
        tag.setString("modId", LibMisc.MOD_ID);
        tag.setBoolean("modRequired", true);
        tag.setBoolean("useCustomScroll", true);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
    }

    private static void sendHandlerImage(String handler, String imageResource, int imageX, int imageY, int imageW,
        int imageH, int handlerHeight, int recipesPerPage) {

        NBTTagCompound tag = new NBTTagCompound();

        // ID of recipe handler
        tag.setString("handler", handler);

        // === IMAGE ICON ===
        tag.setString("imageResource", imageResource);
        tag.setInteger("imageX", imageX);
        tag.setInteger("imageY", imageY);
        tag.setInteger("imageWidth", imageW);
        tag.setInteger("imageHeight", imageH);

        tag.setInteger("handlerHeight", handlerHeight);
        tag.setInteger("maxRecipesPerPage", recipesPerPage);

        tag.setString("modName", LibMisc.MOD_NAME);
        tag.setString("modId", LibMisc.MOD_ID);
        tag.setBoolean("modRequired", true);

        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
    }

    private static void sendCatalyst(String handlerID, ItemStack stack) {
        if (stack == null) return;
        NBTTagCompound nbt = new NBTTagCompound();
        GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        if (uid == null) return;
        String regName = uid.modId + ":" + uid.name;
        int meta = stack.getItemDamage();
        if (meta > 0) regName = regName + ":" + meta;

        nbt.setString("handlerID", handlerID);
        nbt.setString("itemName", regName);
        nbt.setInteger("priority", 0);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", nbt);
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
