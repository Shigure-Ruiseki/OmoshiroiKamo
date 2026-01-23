package ruiseki.omoshiroikamo.module.cable.common.network.logic.node;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public interface ILogicNode {

    ILogicValue evaluate(EvalContext ctx);
}
