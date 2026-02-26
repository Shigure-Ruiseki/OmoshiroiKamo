package ruiseki.omoshiroikamo.module.ids.common.item.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.LogicValues;
import ruiseki.omoshiroikamo.module.ids.common.util.LogicOperationUtils;

public class OpEqual implements ILogicOperator {

    @Override
    public String getId() {
        return "EQ";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        boolean eq = LogicOperationUtils.equals(in.get(0), in.get(1));
        return LogicValues.of(eq);
    }
}
