package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that assigns a value to an NBT key.
 * Supports assignment operators: =, +=, -=, *=, /=
 * Supports nested NBT paths via dot notation (e.g., display.Name)
 */
public class NBTAssignmentExpression implements INBTWriteExpression {

    private final String nbtKey;
    private final List<String> pathSegments;
    private final IExpression valueExpression;
    private final String operation;

    // Legacy constructor for simple keys
    public NBTAssignmentExpression(String nbtKey, IExpression valueExpression, String operation) {
        this(nbtKey, null, valueExpression, operation);
    }

    // New constructor for nested paths
    public NBTAssignmentExpression(String nbtKey, List<String> pathSegments, IExpression valueExpression,
        String operation) {
        this.nbtKey = nbtKey;
        this.pathSegments = pathSegments;
        this.valueExpression = valueExpression;
        this.operation = operation;
    }

    @Override
    public double evaluate(ConditionContext context) {
        // For numeric evaluation, return the value that would be written
        return valueExpression.evaluate(context);
    }

    @Override
    public void applyToNBT(NBTTagCompound nbt, ConditionContext context) {
        if (nbt == null) return;

        Object value = getValueToWrite(context);

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

        // Determine NBT type and write
        if (value instanceof String) {
            String stringValue = (String) value;
            NBTBase nbtValue = NBTTypeInference.parseValue(stringValue);
            targetNbt.setTag(targetKey, nbtValue);
        } else if (value instanceof NBTTagList) {
            // Array literal
            targetNbt.setTag(targetKey, (NBTTagList) value);
        } else if (value instanceof Double || value instanceof Float) {
            double numValue = ((Number) value).doubleValue();

            // For compound operations (+=, -=, *=, /=), get current value
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

            // Write as appropriate type
            NBTBase nbtValue = NBTTypeInference.parseNumeric(numValue);
            targetNbt.setTag(targetKey, nbtValue);
        }
    }

    private void applySimpleNBT(NBTTagCompound nbt, String key, Object value, ConditionContext context) {
        // Obsolete but keeping temporarily if needed by other logic
        applyToNBT(nbt, context);
    }

    /**
     * Get the value to write, either as a number, string, or array.
     */
    private Object getValueToWrite(ConditionContext context) {
        if (valueExpression instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) valueExpression).getStringValue();
        } else if (valueExpression instanceof ArrayLiteralExpression) {
            return ((ArrayLiteralExpression) valueExpression).toNBTList(context);
        } else {
            return valueExpression.evaluate(context);
        }
    }

    @Override
    public String getNBTKey() {
        return nbtKey;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return nbtKey + " " + operation + " " + valueExpression;
    }

    public static NBTAssignmentExpression fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        String op = json.get("operation")
            .getAsString();
        IExpression value = ExpressionsParser.parse(json.get("value"));
        return new NBTAssignmentExpression(key, java.util.Arrays.asList(key.split("\\.")), value, op);
    }
}
