package ruiseki.omoshiroikamo.module.ids.common.network.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;

public interface ILogicNode {

    ILogicValue evaluate(EvalContext ctx);
}
