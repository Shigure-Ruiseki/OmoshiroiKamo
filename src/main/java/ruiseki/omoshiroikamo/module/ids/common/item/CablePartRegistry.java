package ruiseki.omoshiroikamo.module.ids.common.item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.part.crafting.interfacebus.CraftingInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.block.BlockReader;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.fluid.FluidReader;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.inventory.InventoryReader;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone.RedstoneReader;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone.RedstoneWriter;
import ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.input.EnergyImporter;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus.EnergyFilterInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus.EnergyInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.output.EnergyExporter;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.input.ItemImporter;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus.ItemFilterInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus.ItemInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.output.ItemExporter;

public class CablePartRegistry {

    private static final Map<String, Supplier<? extends ICablePart>> REGISTRY = new HashMap<>();

    public static void register(String id, Supplier<? extends ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<? extends ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        for (Supplier<? extends ICablePart> sup : REGISTRY.values()) {
            ICablePart part = sup.get();
            part.registerModel();
        }
    }

    public static void init() {
        CablePartRegistry.register("energy_importer", EnergyImporter::new);
        CablePartRegistry.register("energy_exporter", EnergyExporter::new);
        CablePartRegistry.register("energy_interface", EnergyInterface::new);
        CablePartRegistry.register("energy_filter_interface", EnergyFilterInterface::new);

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
}
