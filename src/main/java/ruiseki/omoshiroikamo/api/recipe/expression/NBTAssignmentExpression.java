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

        // Check if this is a nested path
        if (pathSegments != null && pathSegments.size() > 1) {
            // Nested path: navigate and create hierarchy
            applyNestedNBT(nbt, pathSegments, value, context);
        } else {
            // Simple key: direct assignment
            applySimpleNBT(nbt, nbtKey, value, context);
        }
    }

    /**
     * Apply value to nested NBT path (e.g., display.Name).
     * Creates intermediate NBTTagCompound nodes as needed.
     */
    private void applyNestedNBT(NBTTagCompound root, List<String> segments, Object value, ConditionContext context) {
        NBTTagCompound current = root;

        // Navigate to parent compound, creating nodes as needed
        for (int i = 0; i < segments.size() - 1; i++) {
            String segment = segments.get(i);
            if (!current.hasKey(segment)) {
                // Create new compound
                current.setTag(segment, new NBTTagCompound());
            }
            NBTBase next = current.getTag(segment);
            if (!(next instanceof NBTTagCompound)) {
                // Override with compound if type mismatch
                next = new NBTTagCompound();
                current.setTag(segment, next);
            }
            current = (NBTTagCompound) next;
        }

        // Set final value
        String finalKey = segments.get(segments.size() - 1);
        applySimpleNBT(current, finalKey, value, context);
    }

    /**
     * Apply value to simple NBT key (existing logic).
     */
    private void applySimpleNBT(NBTTagCompound nbt, String key, Object value, ConditionContext context) {
        // Determine NBT type and write
        if (value instanceof String) {
            String stringValue = (String) value;
            NBTBase nbtValue = NBTTypeInference.parseValue(stringValue);
            nbt.setTag(key, nbtValue);
        } else if (value instanceof NBTTagList) {
            // Array literal
            nbt.setTag(key, (NBTTagList) value);
        } else if (value instanceof Double || value instanceof Float) {
            double numValue = ((Number) value).doubleValue();

            // For compound operations (+=, -=, *=, /=), get current value
            if (!operation.equals("=")) {
                double current = nbt.hasKey(key) ? nbt.getDouble(key) : 0.0;
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
            nbt.setTag(key, nbtValue);
        }
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
        return "nbt('" + nbtKey + "') " + operation + " " + valueExpression;
    }

    public static NBTAssignmentExpression fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        String op = json.get("operation")
            .getAsString();
        IExpression value = ExpressionsParser.parse(json.get("value"));
        return new NBTAssignmentExpression(key, value, op);
    }
}
