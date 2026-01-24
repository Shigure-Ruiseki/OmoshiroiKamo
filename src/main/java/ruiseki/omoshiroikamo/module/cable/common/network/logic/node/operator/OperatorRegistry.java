package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.HashMap;
import java.util.Map;

public class OperatorRegistry {

    private static final Map<String, ILogicOperator> OPS = new HashMap<>();

    static {
        // LOGIC
        register("AND", new OpAnd());
        register("OR", new OpOr());
        register("NAND", new OpNand());
        register("NOT", new OpNot());

        // COMPARISON

        // ==
        register("EQ", new OpEqual());
        // !=
        register("NEQ", new OpNotEqual());

        // >
        register("GTR", new OpGreaterThan());
        // >=
        register("GEQ", new OpGreaterOrEqual());
        // <
        register("LSS", new OpLessThan());
        // <=
        register("LEQ", new OpLessOrEqual());

        register("IF", new OpIf());
    }

    public static void register(String id, ILogicOperator op) {
        OPS.put(id, op);
    }

    public static ILogicOperator get(String id) {
        return OPS.get(id);
    }
}
