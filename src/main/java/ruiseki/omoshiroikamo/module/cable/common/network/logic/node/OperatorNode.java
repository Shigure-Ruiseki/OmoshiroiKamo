package ruiseki.omoshiroikamo.module.cable.common.network.logic.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator.LogicOperator;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public class OperatorNode implements ILogicNode {

    private final LogicOperator operator;
    private final List<ILogicNode> children;

    public OperatorNode(LogicOperator operator, ILogicNode... children) {
        this.operator = operator;
        this.children = Arrays.asList(children);
    }

    @Override
    public ILogicValue evaluate(EvalContext ctx) {
        List<ILogicValue> values = new ArrayList<>();

        for (ILogicNode node : children) {
            values.add(node.evaluate(ctx));
        }

        return operator.apply(values);
    }
}
