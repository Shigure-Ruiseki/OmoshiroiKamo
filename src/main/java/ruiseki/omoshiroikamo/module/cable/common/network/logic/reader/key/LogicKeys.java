package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class LogicKeys {

    public static LogicKey REDSTONE_VALUE;
    public static LogicKey HAS_REDSTONE;
    public static LogicKey HIGH_REDSTONE;
    public static LogicKey LOW_REDSTONE;

    public static void preInit() {
        REDSTONE_VALUE = LogicKeyRegistry.register("redstone_value", LogicTypes.INT);

        HAS_REDSTONE = LogicKeyRegistry.register("has_redstone", LogicTypes.BOOLEAN);

        HIGH_REDSTONE = LogicKeyRegistry.register("high_redstone", LogicTypes.BOOLEAN);

        LOW_REDSTONE = LogicKeyRegistry.register("low_redstone", LogicTypes.BOOLEAN);
    }
}
