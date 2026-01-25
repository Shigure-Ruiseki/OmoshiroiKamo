package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class LogicKeys {

    public static LogicKey HAS_REDSTONE;
    public static LogicKey HIGH_REDSTONE;
    public static LogicKey LOW_REDSTONE;
    public static LogicKey HAS_BLOCK;
    public static LogicKey INV_EMPTY;
    public static LogicKey INV_NOT_EMPTY;
    public static LogicKey INV_FULL;
    public static LogicKey IS_INVENTORY;

    public static LogicKey X;
    public static LogicKey Y;
    public static LogicKey Z;
    public static LogicKey LIGHT_LEVEL;
    public static LogicKey REDSTONE_VALUE;
    public static LogicKey INV_COUNT;
    public static LogicKey INV_SLOTS;
    public static LogicKey INV_SLOTS_FILLED;
    public static LogicKey SLOT_ITEM;

    public static LogicKey INV_FILL_RATIO;

    public static LogicKey DIMENSION;
    public static LogicKey BIOME;

    public static LogicKey BLOCK;

    public static void preInit() {
        HAS_REDSTONE = LogicKeyRegistry.register("has_redstone", LogicTypes.BOOLEAN);
        HIGH_REDSTONE = LogicKeyRegistry.register("high_redstone", LogicTypes.BOOLEAN);
        LOW_REDSTONE = LogicKeyRegistry.register("low_redstone", LogicTypes.BOOLEAN);
        HAS_BLOCK = LogicKeyRegistry.register("has_block", LogicTypes.BOOLEAN);
        INV_EMPTY = LogicKeyRegistry.register("inventory_empty", LogicTypes.BOOLEAN);
        INV_NOT_EMPTY = LogicKeyRegistry.register("inventory_not_empty", LogicTypes.BOOLEAN);
        INV_FULL = LogicKeyRegistry.register("inventory_full", LogicTypes.BOOLEAN);
        IS_INVENTORY = LogicKeyRegistry.register("is_inventory", LogicTypes.BOOLEAN);

        X = LogicKeyRegistry.register("x", LogicTypes.INT);
        Y = LogicKeyRegistry.register("y", LogicTypes.INT);
        Z = LogicKeyRegistry.register("z", LogicTypes.INT);
        LIGHT_LEVEL = LogicKeyRegistry.register("light_level", LogicTypes.INT);
        REDSTONE_VALUE = LogicKeyRegistry.register("redstone_value", LogicTypes.INT);
        INV_COUNT = LogicKeyRegistry.register("inventory_count", LogicTypes.INT);
        INV_SLOTS = LogicKeyRegistry.register("inventory_slots", LogicTypes.INT);
        INV_SLOTS_FILLED = LogicKeyRegistry.register("inventory_slots_filled", LogicTypes.INT);
        SLOT_ITEM = LogicKeyRegistry.register("slot_item", LogicTypes.INT);

        INV_FILL_RATIO = LogicKeyRegistry.register("inventory_fill_ratio", LogicTypes.DOUBLE);

        DIMENSION = LogicKeyRegistry.register("dimension", LogicTypes.STRING);
        BIOME = LogicKeyRegistry.register("biome", LogicTypes.STRING);

        BLOCK = LogicKeyRegistry.register("block", LogicTypes.BLOCK);

    }
}
