package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public interface LogicOperator {

    ILogicValue apply(List<ILogicValue> inputs);
}
