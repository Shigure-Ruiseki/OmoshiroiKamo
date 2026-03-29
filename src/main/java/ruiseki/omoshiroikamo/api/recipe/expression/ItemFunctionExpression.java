package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Expression for querying item port states with filters and directions.
 */
public class ItemFunctionExpression implements IExpression {

    public enum FunctionType {
        COUNT,
        SPACE,
        SLOT_COUNT,
        SLOT_EMPTY
    }

    private final FunctionType type;
    private final List<IExpression> arguments;

    public ItemFunctionExpression(FunctionType type, List<IExpression> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null || context.getRecipeContext() == null
            || context.getRecipeContext()
                .getMachineState() == null) {
            return EvaluationValue.ZERO;
        }

        IPortType.Direction direction = IPortType.Direction.BOTH;
        String filter = null;

        for (IExpression arg : arguments) {
            String val = arg.evaluate(context)
                .asString();
            if (val == null || val.isEmpty()) continue;

            if (val.equalsIgnoreCase("input")) {
                direction = IPortType.Direction.INPUT;
            } else if (val.equalsIgnoreCase("output")) {
                direction = IPortType.Direction.OUTPUT;
            } else if (val.equalsIgnoreCase("both")) {
                direction = IPortType.Direction.BOTH;
            } else {
                filter = val;
            }
        }

        var state = context.getRecipeContext()
            .getMachineState();
        switch (type) {
            case COUNT:
                return new EvaluationValue(state.getItemCount(direction, filter));
            case SPACE:
                return new EvaluationValue(state.getItemSpace(direction, filter));
            case SLOT_COUNT:
                return new EvaluationValue((double) state.getItemSlotCount(direction, false));
            case SLOT_EMPTY:
                return new EvaluationValue((double) state.getItemSlotCount(direction, true));
            default:
                return EvaluationValue.ZERO;
        }
    }

    @Override
    public String toString() {
        return "item_" + type.name()
            .toLowerCase() + "()";
    }
}
