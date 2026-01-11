package ruiseki.omoshiroikamo.module.cable.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.input.ItemEnergyInputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus.ItemEnergyInterfaceBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.output.ItemEnergyOutputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.input.ItemItemInputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus.ItemItemInterfaceBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.output.ItemItemOutputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.ItemCableTerminal;

public enum CableItems {

    // spotless: off

    ENERGY_INTERFACE_BUS(new ItemEnergyInterfaceBus()),
    ENERGY_INPUT_BUS(new ItemEnergyInputBus()),
    ENERGY_OUTPUT_BUS(new ItemEnergyOutputBus()),
    ITEM_INTERFACE_BUS(new ItemItemInterfaceBus()),
    ITEM_INPUT_BUS(new ItemItemInputBus()),
    ITEM_OUTPUT_BUS(new ItemItemOutputBus()),
    CABLE_TERMINAL(new ItemCableTerminal()),

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
