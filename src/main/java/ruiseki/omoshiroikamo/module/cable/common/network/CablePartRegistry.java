package ruiseki.omoshiroikamo.module.cable.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.crafting.interfacebus.CraftingInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.input.EnergyInput;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus.EnergyInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.output.EnergyOutput;
import ruiseki.omoshiroikamo.module.cable.common.network.item.input.ItemInput;
import ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus.ItemInterface;
import ruiseki.omoshiroikamo.module.cable.common.network.item.output.ItemOutput;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.redstone.RedstoneReader;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.StorageTerminal;

public class CablePartRegistry {

    private static final Map<String, Supplier<? extends ICablePart>> REGISTRY = new HashMap<>();

    static {
        CablePartRegistry.register("energy_input", EnergyInput::new);
        CablePartRegistry.register("energy_output", EnergyOutput::new);
        CablePartRegistry.register("energy_interface", EnergyInterface::new);
        CablePartRegistry.register("item_input", ItemInput::new);
        CablePartRegistry.register("item_output", ItemOutput::new);
        CablePartRegistry.register("item_interface", ItemInterface::new);
        CablePartRegistry.register("storage_terminal", StorageTerminal::new);
        CablePartRegistry.register("crafting_interface", CraftingInterface::new);
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
