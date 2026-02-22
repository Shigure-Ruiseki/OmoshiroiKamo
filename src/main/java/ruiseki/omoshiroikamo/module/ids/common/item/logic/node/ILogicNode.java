package ruiseki.omoshiroikamo.module.ids.common.item.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;

public interface ILogicNode {

    ILogicValue evaluate(EvalContext ctx);
}
