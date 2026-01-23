package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpNand implements ILogicOperator {

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        for (ILogicValue v : in) {
            if (!v.asBoolean()) {
                return LogicValues.of(true);
            }
        }
        return LogicValues.of(false);
    }
}
