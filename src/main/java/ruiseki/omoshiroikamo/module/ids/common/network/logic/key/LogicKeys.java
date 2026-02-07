package ruiseki.omoshiroikamo.module.ids.common.network.logic.key;

import net.minecraft.client.renderer.texture.IIconRegister;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypes;

public class LogicKeys {

    // Boolean
    public static LogicKey HAS_REDSTONE;
    public static LogicKey HIGH_REDSTONE;
    public static LogicKey LOW_REDSTONE;
    public static LogicKey HAS_BLOCK;
    public static LogicKey INV_EMPTY;
    public static LogicKey INV_NOT_EMPTY;
    public static LogicKey INV_FULL;
    public static LogicKey IS_INVENTORY;
    public static LogicKey IS_TANK;
    public static LogicKey TANK_EMPTY;
    public static LogicKey TANK_NOT_EMPTY;
    public static LogicKey TANK_FULL;

    // Int
    public static LogicKey X;
    public static LogicKey Y;
    public static LogicKey Z;
    public static LogicKey LIGHT_LEVEL;
    public static LogicKey REDSTONE_VALUE;
    public static LogicKey INV_COUNT;
    public static LogicKey INV_SLOTS;
    public static LogicKey INV_SLOTS_FILLED;
    public static LogicKey SLOT_ITEM;
    public static LogicKey FLUID_AMOUNT;
    public static LogicKey TOTAL_FLUID_AMOUNT;
    public static LogicKey FLUID_CAPACITY;
    public static LogicKey TOTAL_FLUID_CAPACITY;
    public static LogicKey TANKS;

    // Double
    public static LogicKey INV_FILL_RATIO;
    public static LogicKey FLUID_FILL_RATIO;

    // String
    public static LogicKey DIMENSION;
    public static LogicKey BIOME;

    // List
    public static LogicKey ITEMS_LIST;
    public static LogicKey TANK_FLUIDS;
    public static LogicKey TANK_CAPACITIES;

    // Block
    public static LogicKey BLOCK;

    // Fluid
    public static LogicKey TANK_FLUID;

    public static void preInit() {

        // Block keys
        BLOCK = register("block", "block/block", LogicTypes.BLOCK);

        // Boolean keys
        HAS_BLOCK = register("has_block", "block/block", LogicTypes.BOOLEAN);
        IS_TANK = register("is_tank", "fluid/applicable", LogicTypes.BOOLEAN);
        TANK_EMPTY = register("tank_empty", "fluid/empty", LogicTypes.BOOLEAN);
        TANK_FULL = register("tank_full", "fluid/full", LogicTypes.BOOLEAN);
        TANK_NOT_EMPTY = register("tank_not_empty", "fluid/nonempty", LogicTypes.BOOLEAN);
        IS_INVENTORY = register("is_inventory", "inventory/applicable", LogicTypes.BOOLEAN);
        INV_EMPTY = register("inventory_empty", "inventory/empty", LogicTypes.BOOLEAN);
        INV_FULL = register("inventory_full", "inventory/full", LogicTypes.BOOLEAN);
        INV_NOT_EMPTY = register("inventory_not_empty", "inventory/nonempty", LogicTypes.BOOLEAN);
        HIGH_REDSTONE = register("high_redstone", "redstone/high", LogicTypes.BOOLEAN);
        LOW_REDSTONE = register("low_redstone", "redstone/low", LogicTypes.BOOLEAN);
        HAS_REDSTONE = register("has_redstone", "redstone/nonlow", LogicTypes.BOOLEAN);

        // Double keys
        INV_FILL_RATIO = register("inventory_fill_ratio", "inventory/fillratio", LogicTypes.DOUBLE);
        FLUID_FILL_RATIO = register("fluid_fill_ratio", "fluid/fillratio", LogicTypes.DOUBLE);

        // Fluid keys
        TANK_FLUID = register("tank_fluid", "fluid", LogicTypes.FLUID);

        // Int keys
        X = register("x", "block/posx", LogicTypes.INT);
        Y = register("y", "block/posy", LogicTypes.INT);
        Z = register("z", "block/posz", LogicTypes.INT);
        FLUID_AMOUNT = register("fluid_amount", "fluid/amount", LogicTypes.INT);
        FLUID_CAPACITY = register("fluid_capacity", "fluid/capacity", LogicTypes.INT);
        TANKS = register("tanks", "fluid/tanks", LogicTypes.INT);
        TOTAL_FLUID_AMOUNT = register("total_fluid_amount", "fluid/totalamount", LogicTypes.INT);
        TOTAL_FLUID_CAPACITY = register("total_fluid_capacity", "fluid/totalcapacity", LogicTypes.INT);
        LIGHT_LEVEL = register("light_level", "block/light", LogicTypes.INT);
        INV_COUNT = register("inventory_count", "inventory/count", LogicTypes.INT);
        INV_SLOTS = register("inventory_slots", "inventory/slots", LogicTypes.INT);
        INV_SLOTS_FILLED = register("inventory_slots_filled", "inventory/slotsfilled", LogicTypes.INT);
        REDSTONE_VALUE = register("redstone_value", "redstone/value", LogicTypes.INT);

        // Item keys
        SLOT_ITEM = register("slot_item", "item/itemstack", LogicTypes.ITEM);

        // List keys
        TANK_CAPACITIES = register("tank_capacities", "fluid/capacities", LogicTypes.LIST);
        TANK_FLUIDS = register("tank_fluids", "fluid/fluidstacks", LogicTypes.LIST);
        ITEMS_LIST = register("items_list", "inventory/itemstacks", LogicTypes.LIST);

        // String keys
        BIOME = register("biome", "block/biome", LogicTypes.STRING);
        DIMENSION = register("dimension", "block/dimension", LogicTypes.STRING);
    }

    public static LogicKey register(String id, String texture, LogicType<?> type) {
        return LogicKeyRegistry.register(new LogicKey(id, type) {

            @Override
            public void registerIcons(IIconRegister register) {
                String typeFolder = type.getId();
                IconRegistry.addIcon(
                    "logickey." + id,
                    register.registerIcon(LibResources.PREFIX_MOD + "ids/logickey/" + typeFolder + "/" + texture));
            }
        });
    }

}
