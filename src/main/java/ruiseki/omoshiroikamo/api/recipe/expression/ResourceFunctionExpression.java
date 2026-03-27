package ruiseki.omoshiroikamo.api.recipe.expression;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;

/**
 * Expression that evaluates to a resource value by name (e.g.,
 * essentia("ignis"), gas("hydrogen")).
 */
public class ResourceFunctionExpression implements IExpression {

    public enum Type {
        ESSENTIA,
        VIS,
        GAS,
        FLUID
    }

    private final Type type;
    private final IExpression argument;

    public ResourceFunctionExpression(Type type, IExpression argument) {
        this.type = type;
        this.argument = argument;
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null) return 0;
        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null) return 0;

        IMachineState state = recipeContext.getMachineState();
        if (state == null) return 0;

        String key = "";
        if (argument instanceof StringLiteralExpression) {
            key = ((StringLiteralExpression) argument).getStringValue();
        } else {
            key = String.valueOf(argument.evaluate(context));
        }

        switch (type) {
            case ESSENTIA:
                return state.getStoredEssentia(key);
            case VIS:
                return state.getStoredVis(key);
            case GAS:
                return state.getStoredGas(key);
            case FLUID:
                return state.getStoredFluid(key);
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return type.name()
            .toLowerCase() + "("
            + argument
            + ")";
    }
}
