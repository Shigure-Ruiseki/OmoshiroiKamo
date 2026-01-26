package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

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
    public static LogicKey TANK;

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
        HAS_REDSTONE = LogicKeyRegistry.register("has_redstone", LogicTypes.BOOLEAN);
        HIGH_REDSTONE = LogicKeyRegistry.register("high_redstone", LogicTypes.BOOLEAN);
        LOW_REDSTONE = LogicKeyRegistry.register("low_redstone", LogicTypes.BOOLEAN);
        HAS_BLOCK = LogicKeyRegistry.register("has_block", LogicTypes.BOOLEAN);
        INV_EMPTY = LogicKeyRegistry.register("inventory_empty", LogicTypes.BOOLEAN);
        INV_NOT_EMPTY = LogicKeyRegistry.register("inventory_not_empty", LogicTypes.BOOLEAN);
        INV_FULL = LogicKeyRegistry.register("inventory_full", LogicTypes.BOOLEAN);
        IS_INVENTORY = LogicKeyRegistry.register("is_inventory", LogicTypes.BOOLEAN);
        TANK_FULL = LogicKeyRegistry.register("tank_full", LogicTypes.BOOLEAN);
        TANK_EMPTY = LogicKeyRegistry.register("tank_empty", LogicTypes.BOOLEAN);
        TANK_NOT_EMPTY = LogicKeyRegistry.register("tank_not_empty", LogicTypes.BOOLEAN);
        IS_TANK = LogicKeyRegistry.register("is_tank", LogicTypes.BOOLEAN);

        X = LogicKeyRegistry.register("x", LogicTypes.INT);
        Y = LogicKeyRegistry.register("y", LogicTypes.INT);
        Z = LogicKeyRegistry.register("z", LogicTypes.INT);
        LIGHT_LEVEL = LogicKeyRegistry.register("light_level", LogicTypes.INT);
        REDSTONE_VALUE = LogicKeyRegistry.register("redstone_value", LogicTypes.INT);
        INV_COUNT = LogicKeyRegistry.register("inventory_count", LogicTypes.INT);
        INV_SLOTS = LogicKeyRegistry.register("inventory_slots", LogicTypes.INT);
        INV_SLOTS_FILLED = LogicKeyRegistry.register("inventory_slots_filled", LogicTypes.INT);
        SLOT_ITEM = LogicKeyRegistry.register("slot_item", LogicTypes.INT);
        FLUID_AMOUNT = LogicKeyRegistry.register("fluid_amount", LogicTypes.INT);
        TOTAL_FLUID_AMOUNT = LogicKeyRegistry.register("total_fluid_amount", LogicTypes.INT);
        FLUID_CAPACITY = LogicKeyRegistry.register("fluid_capacity", LogicTypes.INT);
        TOTAL_FLUID_CAPACITY = LogicKeyRegistry.register("total_fluid_capacity", LogicTypes.INT);
        TANK = LogicKeyRegistry.register("tank", LogicTypes.INT);

        INV_FILL_RATIO = LogicKeyRegistry.register("inventory_fill_ratio", LogicTypes.DOUBLE);
        FLUID_FILL_RATIO = LogicKeyRegistry.register("fluid_fill_ratio", LogicTypes.DOUBLE);

        DIMENSION = LogicKeyRegistry.register("dimension", LogicTypes.STRING);
        BIOME = LogicKeyRegistry.register("biome", LogicTypes.STRING);

        ITEMS_LIST = LogicKeyRegistry.register("items_list", LogicTypes.LIST);
        TANK_FLUIDS = LogicKeyRegistry.register("tank_fluids", LogicTypes.LIST);
        TANK_CAPACITIES = LogicKeyRegistry.register("tank_capacities", LogicTypes.LIST);

        BLOCK = LogicKeyRegistry.register("block", LogicTypes.BLOCK);

        TANK_FLUID = LogicKeyRegistry.register("tank_fluid", LogicTypes.FLUID);

    }
}
