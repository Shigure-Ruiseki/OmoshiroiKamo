package ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;
import ruiseki.omoshiroikamo.module.cable.common.util.LogicOperationUtils;

public class OpLessOrEqual implements ILogicOperator {

    @Override
    public String getId() {
        return "LEQ";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        Integer cmp = LogicOperationUtils.compare(in.get(0), in.get(1));
        return cmp == null ? LogicValues.NULL : LogicValues.of(cmp <= 0);
    }
}
