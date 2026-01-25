package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;
import ruiseki.omoshiroikamo.module.cable.common.util.LogicOperationUtils;

public class OpNotEqual implements ILogicOperator {

    @Override
    public String getId() {
        return "NEQ";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        boolean eq = LogicOperationUtils.equals(in.get(0), in.get(1));
        return LogicValues.of(!eq);
    }
}
