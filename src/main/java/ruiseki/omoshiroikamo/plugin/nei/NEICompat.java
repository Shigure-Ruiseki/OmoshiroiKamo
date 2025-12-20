package ruiseki.omoshiroikamo.plugin.nei;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.chicken.ChickenThrowsRecipeHandler;

public class NEICompat {

    public static void init() {
        if (!LibMods.NotEnoughItems.isLoaded()) {
            return;
        }
        IMCSender();
    }

    public static void IMCSender() {
        if (BackportConfigs.useEnvironmentalTech) {
            for (int i = 0; i <= 5; i++) { // Environmental Tech usually defaults to 6 tiers (1-6)
                String oreId = ModObject.blockQuantumOreExtractor.getRegistryName() + ".tier" + i;
                sendHandler(oreId, "Void Ore Miner Tier " + (i + 1), 48, 8);
                sendCatalyst(oreId, ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i));

                String resId = ModObject.blockQuantumResExtractor.getRegistryName() + ".tier" + i;
                sendHandler(resId, "Void Resource Miner Tier " + (i + 1), 48, 8);
                sendCatalyst(resId, ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i));
            }
        }

        if (BackportConfigs.useChicken) {

            sendHandlerImage(
                ChickenLayingRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/laying_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);
            sendCatalyst(ChickenLayingRecipeHandler.UID, ModBlocks.ROOST.newItemStack());

            sendHandlerImage(
                ChickenBreedingRecipeHandler.UID,
                LibResources.PREFIX_GUI + "nei/chicken/breeding_icon.png",
                1,
                0,
                16,
                16,
                64,
                6);
            sendCatalyst(ChickenBreedingRecipeHandler.UID, ModBlocks.BREEDER.newItemStack());

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

        if (BackportConfigs.useBackpack) {
            sendCatalyst("crafting", ModItems.CRAFTING_UPGRADE.newItemStack());
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
