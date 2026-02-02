package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.crafting.interfacebus.CraftingInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.block.BlockReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.fluid.FluidReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.inventory.InventoryReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.redstone.RedstoneReader;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.redstone.RedstoneWriter;
import ruiseki.omoshiroikamo.module.ids.common.network.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.input.EnergyImporter;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.interfacebus.EnergyInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.output.EnergyExporter;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.input.ItemImporter;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus.ItemFilterInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus.ItemInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.output.ItemExporter;

public class CablePartRegistry {

    private static final Map<String, Supplier<? extends ICablePart>> REGISTRY = new HashMap<>();

    static {
        CablePartRegistry.register("energy_importer", EnergyImporter::new);
        CablePartRegistry.register("energy_exporter", EnergyExporter::new);
        CablePartRegistry.register("energy_interface", EnergyInterface::new);

        CablePartRegistry.register("item_importer", ItemImporter::new);
        CablePartRegistry.register("item_exporter", ItemExporter::new);
        CablePartRegistry.register("item_interface", ItemInterface::new);
        CablePartRegistry.register("item_filter_interface", ItemFilterInterface::new);

        CablePartRegistry.register("storage_terminal", StorageTerminal::new);
        CablePartRegistry.register("crafting_interface", CraftingInterface::new);

        CablePartRegistry.register("redstone_reader", RedstoneReader::new);
        CablePartRegistry.register("block_reader", BlockReader::new);
        CablePartRegistry.register("inventory_reader", InventoryReader::new);
        CablePartRegistry.register("fluid_reader", FluidReader::new);

        CablePartRegistry.register("redstone_writer", RedstoneWriter::new);
    }

    public static void register(String id, Supplier<? extends ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<? extends ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }
}
