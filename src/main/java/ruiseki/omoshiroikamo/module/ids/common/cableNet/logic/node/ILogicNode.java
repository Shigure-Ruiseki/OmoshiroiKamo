package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;

public interface ILogicNode {

    ILogicValue evaluate(EvalContext ctx);
}
