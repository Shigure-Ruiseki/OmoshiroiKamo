package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;

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
