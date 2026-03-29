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
    public double evaluate(ConditionContext context) {
        if (context == null || context.getRecipeContext() == null
            || context.getRecipeContext()
                .getMachineState() == null) {
            return 0;
        }

        IPortType.Direction direction = IPortType.Direction.BOTH;
        String filter = null;

        // Parse arguments: ( [direction], [filter] )
        // Direction is optional string: "input", "output", "both"
        // Filter is optional string: "minecraft:iron_ingot" or "ore:ingotIron"

        for (IExpression arg : arguments) {
            String val = arg.evaluateString(context);
            if (val == null) continue;

            if (val.equalsIgnoreCase("input")) {
                direction = IPortType.Direction.INPUT;
            } else if (val.equalsIgnoreCase("output")) {
                direction = IPortType.Direction.OUTPUT;
            } else if (val.equalsIgnoreCase("both")) {
                direction = IPortType.Direction.BOTH;
            } else {
                // Must be a filter
                filter = val;
            }
        }

        var state = context.getRecipeContext()
            .getMachineState();
        switch (type) {
            case COUNT:
                return state.getItemCount(direction, filter);
            case SPACE:
                return state.getItemSpace(direction, filter);
            case SLOT_COUNT:
                return state.getItemSlotCount(direction, false);
            case SLOT_EMPTY:
                return state.getItemSlotCount(direction, true);
            default:
                return 0;
        }
    }
}
