package ruiseki.omoshiroikamo.module.ids.common.network.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;

public class LiteralNode implements ILogicNode {

    private final ILogicValue value;

    public LiteralNode(ILogicValue value) {
        this.value = value;
    }

    public ILogicValue getValue() {
        return value;
    }

    @Override
    public ILogicValue evaluate(EvalContext ctx) {
        return value;
    }
}
