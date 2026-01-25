package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class LogicKeys {

    public static LogicKey REDSTONE_VALUE;
    public static LogicKey HAS_REDSTONE;
    public static LogicKey HIGH_REDSTONE;
    public static LogicKey LOW_REDSTONE;
    public static LogicKey HAS_BLOCK;
    public static LogicKey DIMENSION;
    public static LogicKey X;
    public static LogicKey Y;
    public static LogicKey Z;
    public static LogicKey BLOCK;
    public static LogicKey BIOME;
    public static LogicKey LIGHT_LEVEL;

    public static void preInit() {
        REDSTONE_VALUE = LogicKeyRegistry.register("redstone_value", LogicTypes.INT);

        HAS_REDSTONE = LogicKeyRegistry.register("has_redstone", LogicTypes.BOOLEAN);

        HIGH_REDSTONE = LogicKeyRegistry.register("high_redstone", LogicTypes.BOOLEAN);

        LOW_REDSTONE = LogicKeyRegistry.register("low_redstone", LogicTypes.BOOLEAN);

        HAS_BLOCK = LogicKeyRegistry.register("has_block", LogicTypes.BOOLEAN);

        DIMENSION = LogicKeyRegistry.register("dimension", LogicTypes.STRING);

        X = LogicKeyRegistry.register("x", LogicTypes.INT);

        Y = LogicKeyRegistry.register("y", LogicTypes.INT);

        Z = LogicKeyRegistry.register("z", LogicTypes.INT);

        BLOCK = LogicKeyRegistry.register("block", LogicTypes.BLOCK);

        BIOME = LogicKeyRegistry.register("biome", LogicTypes.STRING);

        LIGHT_LEVEL = LogicKeyRegistry.register("light_level", LogicTypes.INT);
    }
}
