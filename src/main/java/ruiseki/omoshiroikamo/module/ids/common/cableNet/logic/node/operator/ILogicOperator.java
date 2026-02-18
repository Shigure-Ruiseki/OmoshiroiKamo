package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;

public interface ILogicOperator {

    String getId();

    ILogicValue apply(List<ILogicValue> inputs);
}
