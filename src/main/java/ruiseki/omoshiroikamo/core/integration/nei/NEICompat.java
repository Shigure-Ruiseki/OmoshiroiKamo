package ruiseki.omoshiroikamo.core.integration.nei;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenThrowsRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.integration.nei.LootFabricatorRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.integration.nei.SimulationChamberRecipeHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public class NEICompat {

    public static void init() {
        if (!LibMods.NotEnoughItems.isLoaded()) {
            return;
        }
        IMCSender();
    }

    public static void IMCSender() {
        if (BackportConfigs.enableMultiBlock) {
            // Register Ore Extractor handlers
            for (int i = 0; i <= 5; i++) {
                String oreId = ModObject.blockQuantumOreExtractor.getRegistryName() + ".tier" + i;
                ItemStack oreStack = MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i);
                sendHandler(oreId, oreStack, 48, 8);
                sendCatalyst(oreId, oreStack);
            }
            // Register Res Extractor handlers
            for (int i = 0; i <= 5; i++) {
                String resId = ModObject.blockQuantumResExtractor.getRegistryName() + ".tier" + i;
                ItemStack resStack = MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i);
                sendHandler(resId, resStack, 48, 8);
                sendCatalyst(resId, resStack);
            }
        }

        if (BackportConfigs.enableChicken) {

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

            sendHandlerImage(
                ChickenDropsRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/drops_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);

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

        if (BackportConfigs.enableBackpack) {
            sendCatalyst("crafting", BackpackItems.CRAFTING_UPGRADE.newItemStack());
        }

        if (BackportConfigs.enableDML) {
            sendHandler(LootFabricatorRecipeHandler.UID, DMLBlocks.LOOT_FABRICATOR.newItemStack(), 48, 8);
            sendCatalyst(LootFabricatorRecipeHandler.UID, DMLBlocks.LOOT_FABRICATOR.newItemStack());
            sendHandler(SimulationChamberRecipeHandler.UID, DMLBlocks.SIMULATION_CHAMBER.newItemStack(), 48, 8);
            sendCatalyst(SimulationChamberRecipeHandler.UID, DMLBlocks.SIMULATION_CHAMBER.newItemStack());
        }

        Logger.info("Loaded IMCForNEI");
    }

    private static void sendHandler(String handler, String itemName, int height, int recipesPerPage) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("handler", handler);
        tag.setString("itemName", itemName);
        tag.setInteger("handlerHeight", height);
        tag.setInteger("maxRecipesPerPage", recipesPerPage);
        tag.setString("modName", LibMisc.MOD_NAME);
        tag.setString("modId", LibMisc.MOD_ID);
        tag.setBoolean("modRequired", true);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
    }

    private static void sendHandler(String handler, int height, int recipesPerPage) {
        sendHandler(handler, handler, height, recipesPerPage);
    }

    private static void sendHandler(String handler, ItemStack stack, int height, int recipesPerPage) {
        if (stack == null) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        if (uid == null) {
            return;
        }
        String regName = uid.modId + ":" + uid.name;
        int meta = stack.getItemDamage();
        if (meta > 0) {
            regName = regName + ":" + meta;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("handler", handler);
        tag.setString("itemName", regName);
        tag.setInteger("handlerHeight", height);
        tag.setInteger("maxRecipesPerPage", recipesPerPage);
        tag.setString("modName", LibMisc.MOD_NAME);
        tag.setString("modId", LibMisc.MOD_ID);
        tag.setBoolean("modRequired", true);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
    }

    private static void sendHandlerImage(String handler, String imageResource, int imageX, int imageY, int imageW,
        int imageH, int handlerHeight, int recipesPerPage) {

        NBTTagCompound tag = new NBTTagCompound();

        // ID của recipe handler
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

    private static void sendCatalyst(String handlerID, ItemStack stack, int priority) {
        if (stack == null) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        if (uid == null) {
            return;
        }
        String regName = uid.modId + ":" + uid.name;
        int meta = stack.getItemDamage();
        if (meta > 0) {
            regName = regName + ":" + meta;
        }

        nbt.setString("handlerID", handlerID);
        nbt.setString("itemName", regName);
        nbt.setInteger("priority", priority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", nbt);
    }

    private static void sendCatalyst(String handlerID, ItemStack stack) {
        sendCatalyst(handlerID, stack, 0);
    }

    private static void sendCatalyst(String handler, String itemName, int priority) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("handlerID", handler);
        nbt.setString("itemName", itemName);
        nbt.setInteger("priority", priority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", nbt);
    }

    private static void sendCatalyst(String handlerName, String stack) {
        sendCatalyst(handlerName, stack, 0);
    }

    private static void sendCatalyst(String handler) {
        sendCatalyst(handler, handler, 0);
    }

}
