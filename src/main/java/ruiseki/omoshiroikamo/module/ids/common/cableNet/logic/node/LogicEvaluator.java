package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.LogicValues;

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
