package ruiseki.omoshiroikamo.api.recipe.expression;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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
        FLUID,
        FLUID_IN,
        FLUID_OUT_S,
        MANA,
        ITEM,
        ITEM_IN,
        ITEM_OUT,
        ITEM_S,
        ITEM_IN_S,
        ITEM_OUT_S,
        ITEM_F,
        ITEM_F_IN,
        ITEM_F_OUT,
        ENERGY
    }

    private final Type type;
    private final IExpression argument;

    public ResourceFunctionExpression(Type type, IExpression argument) {
        this.type = type;
        this.argument = argument;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null) return EvaluationValue.ZERO;
        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null) return EvaluationValue.ZERO;

        IMachineState state = recipeContext.getMachineState();
        if (state == null) return EvaluationValue.ZERO;

        String key = argument != null ? argument.evaluate(context)
            .asString() : "";

        switch (type) {
            case ESSENTIA:
                return new EvaluationValue(state.getStoredEssentia(key));
            case VIS:
                return new EvaluationValue(state.getStoredVis(key));
            case GAS:
                return new EvaluationValue(state.getStoredGas(key));
            case ENERGY:
                return new EvaluationValue(state.getStoredEnergy());
            case MANA:
                return new EvaluationValue(state.getStoredMana());
            case FLUID:
                return new EvaluationValue(state.getStoredFluid(key));
            case FLUID_IN:
                return new EvaluationValue(state.getFluidInput(key));
            case FLUID_OUT_S:
                return new EvaluationValue(state.getFluidOutputSpace(key));
            case ITEM:
                return new EvaluationValue(state.getItemCount(IPortType.Direction.BOTH, key));
            case ITEM_IN:
                return new EvaluationValue(state.getItemCount(IPortType.Direction.INPUT, key));
            case ITEM_OUT:
                return new EvaluationValue(state.getItemCount(IPortType.Direction.OUTPUT, key));
            case ITEM_S:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.BOTH, key));
            case ITEM_IN_S:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.INPUT, key));
            case ITEM_OUT_S:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.OUTPUT, key));
            case ITEM_F:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.BOTH, key));
            case ITEM_F_IN:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.INPUT, key));
            case ITEM_F_OUT:
                return new EvaluationValue(state.getItemSpace(IPortType.Direction.OUTPUT, key));
        }

        return EvaluationValue.ZERO;
    }

    @Override
    public String toString() {
        return type.name()
            .toLowerCase() + "("
            + argument
            + ")";
    }
}
