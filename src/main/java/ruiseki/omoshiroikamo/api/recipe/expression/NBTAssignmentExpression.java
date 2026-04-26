package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Action that assigns a value to an NBT key in the current context's working NBT.
 */
public class NBTAssignmentExpression implements IAction, IExpression {

    private final String nbtKey;
    private final List<String> pathSegments;
    private final IExpression valueExpression;
    private final String operation;

    public NBTAssignmentExpression(String nbtKey, List<String> pathSegments, IExpression valueExpression,
        String operation) {
        this.nbtKey = nbtKey;
        this.pathSegments = pathSegments;
        this.valueExpression = valueExpression;
        this.operation = operation;
    }

    @Override
    public void execute(ConditionContext context) {
        NBTTagCompound nbt = context.getWorkingNBT();
        if (nbt == null) return;

        EvaluationValue evalVal = valueExpression.evaluate(context);

        // Find the target compound and key
        NBTTagCompound targetNbt = nbt;
        String targetKey = nbtKey;

        if (pathSegments != null && pathSegments.size() > 1) {
            for (int i = 0; i < pathSegments.size() - 1; i++) {
                String segment = pathSegments.get(i);
                if (!targetNbt.hasKey(segment)) {
                    targetNbt.setTag(segment, new NBTTagCompound());
                }
                NBTBase next = targetNbt.getTag(segment);
                if (!(next instanceof NBTTagCompound)) {
                    next = new NBTTagCompound();
                    targetNbt.setTag(segment, next);
                }
                targetNbt = (NBTTagCompound) next;
            }
            targetKey = pathSegments.get(pathSegments.size() - 1);
        }

        // Apply operation
        if (evalVal.isString()) {
            targetNbt.setString(targetKey, evalVal.asString());
        } else if (evalVal.isBoolean()) {
            targetNbt.setBoolean(targetKey, evalVal.asBoolean());
        } else if (evalVal.isNumeric()) {
            double numValue = evalVal.asDouble();
            if (!operation.equals("=")) {
                double current = targetNbt.hasKey(targetKey) ? targetNbt.getDouble(targetKey) : 0.0;
                switch (operation) {
                    case "+=":
                        numValue = current + numValue;
                        break;
                    case "-=":
                        numValue = current - numValue;
                        break;
                    case "*=":
                        numValue = current * numValue;
                        break;
                    case "/=":
                        numValue = numValue != 0 ? current / numValue : current;
                        break;
                }
            }
            targetNbt.setDouble(targetKey, numValue);
        }
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        // As an expression, it returns the value to be assigned
        return valueExpression.evaluate(context);
    }

    @Override
    public String toString() {
        return nbtKey + " " + operation + " " + valueExpression;
    }

    public static NBTAssignmentExpression fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        IExpression value = ExpressionsParser.parse(json.get("value"));
        String op = json.has("op") ? json.get("op")
            .getAsString() : "=";
        java.util.List<String> path = java.util.Arrays.asList(key.split("\\."));
        return new NBTAssignmentExpression(key, path, value, op);
    }

    public String getNBTKey() {
        return nbtKey;
    }

    public String getOperation() {
        return operation;
    }
}
