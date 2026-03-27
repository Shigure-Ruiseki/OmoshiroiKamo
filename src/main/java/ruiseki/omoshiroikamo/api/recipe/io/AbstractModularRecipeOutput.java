package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;

/**
 * Abstract base class for recipe outputs that work with the Modular Port system.
 * Extends AbstractRecipeOutput with Modular Port-specific capacity checking logic.
 */
public abstract class AbstractModularRecipeOutput extends AbstractRecipeOutput implements IModularRecipeOutput {

    protected int index = -1;
    protected int amount;
    protected IExpression amountExpr;

    @Override
    public boolean checkCapacity(List<IModularPort> ports, int multiplier, ConditionContext context) {
        long totalCapacity = 0;

        for (IModularPort port : ports) {
            // Common check for all outputs
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (isCorrectPort(port)) {
                totalCapacity += getPortCapacity(port);
            }
        }

        return totalCapacity >= getRequiredAmount(context) * multiplier;
    }

    /**
     * Check if the port is of the correct type and instance for this output.
     * Also checks IPortType.Type.
     */
    protected abstract boolean isCorrectPort(IModularPort port);

    /**
     * Calculate the capacity of a single valid port for this output type.
     */
    protected abstract long getPortCapacity(IModularPort port);

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
