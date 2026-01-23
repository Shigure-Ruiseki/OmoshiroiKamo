package ruiseki.omoshiroikamo.module.cable.common.network.logic.key;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class LogicKeys {

    public static final LogicKey REDSTONE_VALUE;
    public static final LogicKey HAS_REDSTONE;
    public static final LogicKey HIGH_REDSTONE;
    public static final LogicKey LOW_REDSTONE;

    static {
        REDSTONE_VALUE = LogicKeyRegistry.register("redstone_value", LogicTypes.INT);

        HAS_REDSTONE = LogicKeyRegistry.register("has_redstone", LogicTypes.BOOLEAN);

        HIGH_REDSTONE = LogicKeyRegistry.register("high_redstone", LogicTypes.BOOLEAN);

        LOW_REDSTONE = LogicKeyRegistry.register("low_redstone", LogicTypes.BOOLEAN);
    }
}
