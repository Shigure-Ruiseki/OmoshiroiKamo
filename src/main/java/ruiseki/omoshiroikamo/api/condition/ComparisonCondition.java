package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.expression.DotNotationNBTExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.StringLiteralExpression;

/**
 * Condition that compares two expressions.
 */
public class ComparisonCondition implements ICondition {

    private final IExpression left;
    private final IExpression right;
    private final String operator;

    public ComparisonCondition(IExpression left, IExpression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        // Check for string comparison first
        String lStr = getStringValue(left, context);
        String rStr = getStringValue(right, context);

        if (lStr != null && rStr != null) {
            // Both are strings - perform string comparison
            return compareStrings(lStr, rStr);
        }

        // Fall back to numeric comparison
        double lVal = left.evaluate(context);
        double rVal = right.evaluate(context);

        switch (operator) {
            case ">":
                return lVal > rVal;
            case ">=":
                return lVal >= rVal;
            case "<":
                return lVal < rVal;
            case "<=":
                return lVal <= rVal;
            case "==":
                return Math.abs(lVal - rVal) < 0.0001;
            case "!=":
                return Math.abs(lVal - rVal) >= 0.0001;
            default:
                return false;
        }
    }

    /**
     * Get string value from an expression if it represents a string.
     * Returns null if the expression is not a string.
     */
    private String getStringValue(IExpression expr, ConditionContext context) {
        if (expr instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) expr).getStringValue();
        } else if (expr instanceof DotNotationNBTExpression) {
            NBTBase nbt = ((DotNotationNBTExpression) expr).getNestedNBT(context);
            if (nbt instanceof NBTTagString) {
                return ((NBTTagString) nbt).func_150285_a_(); // getString()
            }
        }
        return null;
    }

    /**
     * Compare two strings based on the operator.
     * Only == and != are supported for strings.
     */
    private boolean compareStrings(String left, String right) {
        switch (operator) {
            case "==":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            default:
                // Other operators (<, >, <=, >=) not supported for strings
                return false;
        }
    }

    @Override
    public String getDescription() {
        return left.toString() + " " + operator + " " + right.toString();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "comparison");
        // Serialization not implemented yet
    }

    public static ICondition fromJson(JsonObject json) {
        IExpression left = ExpressionsParser.parse(json.get("left"));
        IExpression right = ExpressionsParser.parse(json.get("right"));
        String op = json.get("operator")
            .getAsString();
        return new ComparisonCondition(left, right, op);
    }
}
