package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class OpOr implements ILogicOperator {

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        return LogicValues.of(
            in.get(0)
                .asBoolean()
                || in.get(1)
                    .asBoolean());
    }
}
