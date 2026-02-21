package ruiseki.omoshiroikamo.module.ids.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.ItemVariableCard;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.crafting.interfacebus.ItemCraftingInterface;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.block.ItemBlockReader;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.fluid.ItemFluidReader;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.inventory.ItemInventoryReader;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.redstone.ItemRedstoneReader;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.redstone.ItemRedstoneWriter;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.terminal.storage.ItemStorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.input.ItemEnergyImporter;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.interfacebus.ItemEnergyFilterInterface;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.interfacebus.ItemEnergyInterface;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.output.ItemEnergyExporter;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.input.ItemItemImporter;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.interfacebus.ItemItemFilterInterface;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.interfacebus.ItemItemInterface;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.output.ItemItemExporter;

public enum IDsItems {

    // spotless: off

    LOGIC_CARD(new ItemVariableCard()),

    ENERGY_INTERFACE(new ItemEnergyInterface()),
    ENERGY_FILTER_INTERFACE(new ItemEnergyFilterInterface()),
    ENERGY_IMPORTER(new ItemEnergyImporter()),
    ENERGY_EXPORTER(new ItemEnergyExporter()),

    ITEM_INTERFACE(new ItemItemInterface()),
    ITEM_FILTER_INTERFACE(new ItemItemFilterInterface()),
    ITEM_IMPORTER(new ItemItemImporter()),
    ITEM_EXPORTER(new ItemItemExporter()),

    STORAGE_TERMINAL(new ItemStorageTerminal()),
    CRAFTING_INTERFACE(new ItemCraftingInterface()),

    REDSTONE_READER(new ItemRedstoneReader()),
    BLOCK_READER(new ItemBlockReader()),
    INVENTORY_READER(new ItemInventoryReader()),
    FLUID_READER(new ItemFluidReader()),

    REDSTONE_WRITER(new ItemRedstoneWriter()),

    ;
    // spotless: on

    public static final IDsItems[] VALUES = values();

    public static void preInit() {
        for (IDsItems item : VALUES) {
            try {
                GameRegistry.registerItem(item.getItem(), item.getName());
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    @Getter
    private final Item item;

    IDsItems(Item item) {
        this.item = item;
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }

}
