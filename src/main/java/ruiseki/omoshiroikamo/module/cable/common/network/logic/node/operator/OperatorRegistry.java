package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.HashMap;
import java.util.Map;

public final class OperatorRegistry {

    private static final Map<String, LogicOperator> OPS = new HashMap<>();

    static {
        register("AND", new OpAnd());
        register("EQ", new OpEqual());
        register("GT", new OpGreaterThan());
    }

    public static void register(String id, LogicOperator op) {
        OPS.put(id, op);
    }

    public static LogicOperator get(String id) {
        return OPS.get(id);
    }
}
