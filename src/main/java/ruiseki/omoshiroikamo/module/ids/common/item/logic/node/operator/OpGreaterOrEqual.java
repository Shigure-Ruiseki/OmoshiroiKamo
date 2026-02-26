package ruiseki.omoshiroikamo.module.ids.common.item.logic.node.operator;

import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.LogicValues;
import ruiseki.omoshiroikamo.module.ids.common.util.LogicOperationUtils;

public class OpGreaterOrEqual implements ILogicOperator {

    @Override
    public String getId() {
        return "GEQ";
    }

    @Override
    public ILogicValue apply(List<ILogicValue> in) {
        Integer cmp = LogicOperationUtils.compare(in.get(0), in.get(1));
        return cmp == null ? LogicValues.NULL : LogicValues.of(cmp >= 0);
    }
}
