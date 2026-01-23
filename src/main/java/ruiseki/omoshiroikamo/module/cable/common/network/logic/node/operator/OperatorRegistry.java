package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.HashMap;
import java.util.Map;

public class OperatorRegistry {

    private static final Map<String, ILogicOperator> OPS = new HashMap<>();

    static {
        register("AND", new OpAnd());
        register("OR", new OpOr());
        register("NAND", new OpNand());
        register("NOT", new OpNot());

        register("EQ", new OpEqual());
        register("GT", new OpGreaterThan());

        register("IF", new OpIf());
    }

    public static void register(String id, ILogicOperator op) {
        OPS.put(id, op);
    }

    public static ILogicOperator get(String id) {
        return OPS.get(id);
    }
}
