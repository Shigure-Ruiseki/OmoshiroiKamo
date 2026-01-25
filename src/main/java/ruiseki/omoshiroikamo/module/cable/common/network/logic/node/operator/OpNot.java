package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpNot implements ILogicOperator {

    @Override
    public String getId() {
        return "NOT";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        if (in.isEmpty()) {
            return LogicValues.NULL;
        }
        return LogicValues.of(
            !in.get(0)
                .asBoolean());
    }

}
