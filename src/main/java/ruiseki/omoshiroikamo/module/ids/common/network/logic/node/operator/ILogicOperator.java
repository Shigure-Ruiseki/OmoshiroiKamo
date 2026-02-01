package ruiseki.omoshiroikamo.module.ids.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;

public interface ILogicOperator {

    String getId();

    ILogicValue apply(List<ILogicValue> inputs);
}
