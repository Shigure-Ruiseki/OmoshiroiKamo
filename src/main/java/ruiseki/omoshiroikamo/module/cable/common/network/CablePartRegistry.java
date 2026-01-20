package ruiseki.omoshiroikamo.module.cable.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.crafting.interfacebus.CraftingInterfaceBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.input.EnergyInputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus.EnergyInterfaceBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.output.EnergyOutputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.input.ItemInputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus.ItemInterfaceBus;
import ruiseki.omoshiroikamo.module.cable.common.network.item.output.ItemOutputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.redstone.RedstoneReader;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.CableTerminal;

public class CablePartRegistry {

    private static final Map<String, Supplier<? extends ICablePart>> REGISTRY = new HashMap<>();

    static {
        CablePartRegistry.register("energy_input_bus", EnergyInputBus::new);
        CablePartRegistry.register("energy_output_bus", EnergyOutputBus::new);
        CablePartRegistry.register("energy_interface_bus", EnergyInterfaceBus::new);
        CablePartRegistry.register("item_input_bus", ItemInputBus::new);
        CablePartRegistry.register("item_output_bus", ItemOutputBus::new);
        CablePartRegistry.register("item_interface_bus", ItemInterfaceBus::new);
        CablePartRegistry.register("cable_terminal", CableTerminal::new);
        CablePartRegistry.register("crafting_interface_bus", CraftingInterfaceBus::new);
        CablePartRegistry.register("redstone_reader", RedstoneReader::new);
    }

    public static void register(String id, Supplier<? extends ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<? extends ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }
}
