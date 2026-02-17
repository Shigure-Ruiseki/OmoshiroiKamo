package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.operator;

import java.util.HashMap;
import java.util.Map;

public class OperatorRegistry {

    private static final Map<String, ILogicOperator> OPS = new HashMap<>();

    static {
        // LOGIC
        register(new OpAnd());
        register(new OpOr());
        register(new OpNand());
        register(new OpNot());

        // COMPARISON

        // ==
        register(new OpEqual());
        // !=
        register(new OpNotEqual());

        // >
        register(new OpGreaterThan());
        // >=
        register(new OpGreaterOrEqual());
        // <
        register(new OpLessThan());
        // <=
        register(new OpLessOrEqual());

        // CONTROL
        register(new OpIf());
    }

    public static void register(ILogicOperator op) {
        if (op == null || op.getId() == null) {
            throw new IllegalArgumentException("Operator or operator ID is null");
        }
        OPS.put(op.getId(), op);
    }

    public static ILogicOperator get(String id) {
        return OPS.get(id);
    }
}
