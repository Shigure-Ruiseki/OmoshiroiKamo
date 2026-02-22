package ruiseki.omoshiroikamo.module.ids.common.item.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;

public interface ILogicOperator {

    String getId();

    ILogicValue apply(List<ILogicValue> inputs);
}
