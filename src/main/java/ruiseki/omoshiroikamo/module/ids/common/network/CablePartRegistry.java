package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.crafting.interfacebus.CraftingInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.block.BlockReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.fluid.FluidReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.inventory.InventoryReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.redstone.RedstoneReader;
import ruiseki.omoshiroikamo.module.ids.common.network.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.input.EnergyInput;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.interfacebus.EnergyInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.output.EnergyOutput;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.input.ItemInput;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus.ItemInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.output.ItemOutput;

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
        CablePartRegistry.register("block_reader", BlockReader::new);
        CablePartRegistry.register("inventory_reader", InventoryReader::new);
        CablePartRegistry.register("fluid_reader", FluidReader::new);
    }

    public static void register(String id, Supplier<? extends ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<? extends ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }
}
