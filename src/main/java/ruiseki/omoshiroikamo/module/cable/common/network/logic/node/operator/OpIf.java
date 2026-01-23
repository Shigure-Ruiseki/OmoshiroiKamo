package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpIf implements ILogicOperator {

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        if (in.size() < 2) {
            return LogicValues.NULL;
        }

        ILogicValue condition = in.get(0);
        if (!condition.asBoolean()) {
            return in.size() >= 3 ? in.get(2) : LogicValues.NULL;
        }

        return in.get(1);
    }
}
