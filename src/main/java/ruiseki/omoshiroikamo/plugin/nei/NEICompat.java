package ruiseki.omoshiroikamo.plugin.nei;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class NEICompat {

    public static void init() {
        if (!LibMods.NotEnoughItems.isLoaded()) {
            return;
        }
        IMCSender();
    }

    public static void IMCSender() {
        sendHandler(ModObject.blockElectrolyzer.getRegistryName(), 85, 6);
        sendCatalyst(ModObject.blockElectrolyzer.getRegistryName());

        sendHandler(ModObject.blockAnvil.getRegistryName(), 64, 6);
        sendCatalyst(ModObject.blockAnvil.getRegistryName());

        sendHandler(ModObject.blockVoidOreMiner.getRegistryName(), 48, 8);
        sendCatalyst(ModObject.blockVoidOreMiner.getRegistryName(), ModBlocks.VOID_ORE_MINER.newItemStack(1, 0));
        sendCatalyst(ModObject.blockVoidOreMiner.getRegistryName(), ModBlocks.VOID_ORE_MINER.newItemStack(1, 1));
        sendCatalyst(ModObject.blockVoidOreMiner.getRegistryName(), ModBlocks.VOID_ORE_MINER.newItemStack(1, 2));
        sendCatalyst(ModObject.blockVoidOreMiner.getRegistryName(), ModBlocks.VOID_ORE_MINER.newItemStack(1, 3));

        sendHandler(ModObject.blockVoidResMiner.getRegistryName(), 48, 8);
        sendCatalyst(ModObject.blockVoidResMiner.getRegistryName(), ModBlocks.VOID_RES_MINER.newItemStack(1, 0));
        sendCatalyst(ModObject.blockVoidResMiner.getRegistryName(), ModBlocks.VOID_RES_MINER.newItemStack(1, 1));
        sendCatalyst(ModObject.blockVoidResMiner.getRegistryName(), ModBlocks.VOID_RES_MINER.newItemStack(1, 2));
        sendCatalyst(ModObject.blockVoidResMiner.getRegistryName(), ModBlocks.VOID_RES_MINER.newItemStack(1, 3));

        sendCatalyst("smelting", ModObject.blockFurnace.getRegistryName());
        sendCatalyst("fuel", ModObject.blockFurnace.getRegistryName());

        sendHandler("materialProperties", ModObject.itemItemMaterial.getRegistryName(), 85, 1);

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
