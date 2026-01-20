package ruiseki.omoshiroikamo.module.cable.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cable.common.network.crafting.interfacebus.ItemCraftingInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.input.ItemEnergyInput;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus.ItemEnergyInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.output.ItemEnergyOutput;
import ruiseki.omoshiroikamo.module.cable.common.network.item.input.ItemItemInput;
import ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus.ItemItemInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.item.output.ItemItemOutput;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.redstone.ItemRedstoneReader;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.ItemStorageTerminal;

public enum CableItems {

    // spotless: off

    ENERGY_INTERFACE(new ItemEnergyInterface()),
    ENERGY_INPUT(new ItemEnergyInput()),
    ENERGY_OUTPUT(new ItemEnergyOutput()),
    ITEM_INTERFACE(new ItemItemInterface()),
    ITEM_INPUT(new ItemItemInput()),
    ITEM_OUTPUT(new ItemItemOutput()),
    CABLE_TERMINAL(new ItemStorageTerminal()),
    CRAFTING_INTERFACE(new ItemCraftingInterface()),
    REDSTONE_READER(new ItemRedstoneReader()),

    ;
    // spotless: on

    public static final CableItems[] VALUES = values();

    public static void preInit() {
        for (CableItems item : VALUES) {
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

    CableItems(Item item) {
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
