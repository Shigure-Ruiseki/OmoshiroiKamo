package ruiseki.omoshiroikamo.module.ids.common.network.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.LogicValues;

public class LogicEvaluator {

    private LogicEvaluator() {}

    public static ILogicValue evaluate(ILogicNode root, EvalContext ctx) {
        if (root == null) {
            return LogicValues.NULL;
        }

        try {
            return root.evaluate(ctx);
        } catch (Exception e) {
            return LogicValues.NULL;
        }
    }

}
