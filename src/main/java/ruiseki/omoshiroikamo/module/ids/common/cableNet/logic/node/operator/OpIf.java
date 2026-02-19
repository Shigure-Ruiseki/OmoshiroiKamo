package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.LogicValues;

public class OpIf implements ILogicOperator {

    @Override
    public String getId() {
        return "IF";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        if (in.size() < 2) {
            return LogicValues.NULL;
        }

        ILogicValue cond = in.get(0);

        if (cond == null || !cond.asBoolean()) {
            return in.size() >= 3 ? in.get(2) : LogicValues.NULL;
        }

        return in.get(1);
    }
}
