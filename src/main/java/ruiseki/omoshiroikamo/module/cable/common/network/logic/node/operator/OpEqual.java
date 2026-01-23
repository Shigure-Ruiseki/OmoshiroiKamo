package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;
import java.util.Objects;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpEqual implements LogicOperator {

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        ILogicValue a = in.get(0);
        ILogicValue b = in.get(1);

        return LogicValues.of(Objects.equals(a.raw(), b.raw()));
    }
}
