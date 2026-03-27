package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;

/**
 * Abstract base class for recipe inputs that work with the Modular Port system.
 * Extends AbstractRecipeInput with Modular Port-specific processing logic.
 */
public abstract class AbstractModularRecipeInput extends AbstractRecipeInput implements IModularRecipeInput {

    protected int index = -1;
    protected int amount;
    protected IExpression amountExpr;

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate, ConditionContext context) {
        long remaining = getRequiredAmount(context) * multiplier;
        boolean actualSimulate = simulate || !consume;

        for (IModularPort port : ports) {
            if (port.getPortType() != getPortType()) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!isCorrectPort(port)) {
                throw new IllegalStateException(
                    getPortType() + " INPUT port must be compatible implementation, got: "
                        + port.getClass()
                            .getName());
            }

            remaining -= consume(port, remaining, actualSimulate, context);

            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Check if the port is of the correct type and instance for this input.
     */
    protected abstract boolean isCorrectPort(IModularPort port);

    /**
     * Consume from a single port and return the amount consumed.
     */
    protected abstract long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context);

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Evaluates the dynamic amount expression with the given context.
     * Falls back to the static amount if no expression is defined.
     *
     * @param context The condition context for evaluation
     * @return The evaluated amount (always >= 0)
     */
    protected long evaluateAmount(ConditionContext context) {
        if (amountExpr != null && context != null) {
            double evaluated = amountExpr.evaluate(context);
            return (long) Math.max(0, evaluated);
        }
        return amount;
    }

    /**
     * Reads common amount fields from JSON.
     * Subclasses should call this before reading their specific fields.
     *
     * @param json The JSON object to read from
     */
    protected void readCommonAmountFields(JsonObject json) {
        if (json.has("amount")) {
            this.amount = json.get("amount")
                .getAsInt();
        }
        if (json.has("amountExpr")) {
            String exprStr = json.get("amountExpr")
                .getAsString();
            this.amountExpr = ExpressionParser.parseExpression(exprStr);
        }
    }

    /**
     * Writes common amount fields to JSON.
     * Subclasses should call this after writing their specific fields.
     *
     * @param json The JSON object to write to
     */
    protected void writeCommonAmountFields(JsonObject json) {
        json.addProperty("amount", amount);
        if (amountExpr != null) {
            json.addProperty("amountExpr", amountExpr.toString());
        }
    }
}
