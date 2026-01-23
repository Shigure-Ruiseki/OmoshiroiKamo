package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpGreaterThan implements LogicOperator {

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        ILogicValue a = in.get(0);
        ILogicValue b = in.get(1);

        if (!a.getType()
            .isComparable()
            || !b.getType()
                .isComparable()) {
            return LogicValues.NULL;
        }

        return LogicValues.of(a.asInt() > b.asInt());
    }
}
