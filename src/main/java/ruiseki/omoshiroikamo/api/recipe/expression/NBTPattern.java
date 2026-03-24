package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a pattern for matching and modifying NBT compound tags in a list.
 * Supports operation auto-inference based on value format:
 * - Comparison operators (>=, <=, >, <, ==, !=): REQUIRE
 * - Zero value (0): REMOVE
 * - Relative operators (+, -): MODIFY
 * - Absolute value: SET
 */
public class NBTPattern {

    private final Map<String, ValuePattern> patterns;
    private final OperationType primaryOperation;

    public enum OperationType {
        REQUIRE, // Condition check - item must match
        REMOVE, // Delete matching items
        MODIFY, // Relative modification
        SET // Absolute value setting
    }

    public NBTPattern(Map<String, ValuePattern> patterns, OperationType primaryOperation) {
        this.patterns = patterns;
        this.primaryOperation = primaryOperation;
    }

    /**
     * Check if this pattern matches the given NBT compound exactly.
     * All patterns (REQUIRE, SET, MODIFY, REMOVE) must match.
     *
     * @param item The NBT compound to check
     * @return True if all pattern conditions are met
     */
    public boolean matches(NBTTagCompound item) {
        if (item == null) return false;

        for (Map.Entry<String, ValuePattern> entry : patterns.entrySet()) {
            if (!entry.getValue()
                .matches(item, entry.getKey(), true)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if this item is a valid target for application/update.
     * If REQUIRE patterns exist, they take precedence.
     * If not, other patterns (SET, etc.) act as basic filters.
     *
     * @param item The NBT compound to check
     * @return True if the item is a valid target
     */
    public boolean matchesForUpdate(NBTTagCompound item) {
        if (item == null) return false;

        boolean hasRequire = patterns.values()
            .stream()
            .anyMatch(p -> p.operation == OperationType.REQUIRE);

        for (Map.Entry<String, ValuePattern> entry : patterns.entrySet()) {
            ValuePattern pattern = entry.getValue();
            String key = entry.getKey();

            if (hasRequire) {
                // If REQUIRE exists, ONLY REQUIRE patterns filter targets (strictly).
                if (pattern.operation == OperationType.REQUIRE && !pattern.matches(item, key, true)) {
                    return false;
                }
            } else {
                // If NO REQUIRE, ALL patterns filter targets but in non-strict mode.
                // This allows updating existing items (like incrementing Count in Slot 0).
                if (!pattern.matches(item, key, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Apply this pattern's modifications to the given NBT compound.
     *
     * @param item The NBT compound to modify
     * @return True if the pattern was applied
     */
    public boolean apply(NBTTagCompound item) {
        if (item == null) return false;

        for (Map.Entry<String, ValuePattern> entry : patterns.entrySet()) {
            String key = entry.getKey();
            ValuePattern pattern = entry.getValue();
            pattern.apply(item, key);
        }

        return true;
    }

    /**
     * Get the primary operation type for this pattern.
     */
    public OperationType getOperationType() {
        return primaryOperation;
    }

    /**
     * Check if this is a removal pattern.
     */
    public boolean isRemove() {
        return primaryOperation == OperationType.REMOVE;
    }

    /**
     * Parse a pattern from JSON.
     *
     * @param json The JSON object defining the pattern
     * @return A new NBTPattern
     */
    public static NBTPattern fromJson(JsonObject json) {
        Map<String, ValuePattern> patterns = new HashMap<>();
        boolean hasRequire = false;
        boolean hasModify = false;
        boolean hasRemove = false;

        // First pass: parse all and detect operation types
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            ValuePattern pattern = ValuePattern.parse(entry.getValue());
            patterns.put(entry.getKey(), pattern);

            if (pattern.operation == OperationType.REQUIRE) hasRequire = true;
            else if (pattern.operation == OperationType.MODIFY) hasModify = true;
            else if (pattern.operation == OperationType.REMOVE) hasRemove = true;
        }

        // Heuristic adjustment: If we have MODIFY, then any REMOVE (val 0) should be
        // treated as SET (val 0)
        // for filtering purposes. Example: {"Slot": 0, "Count": "+1"}
        if (hasModify) {
            for (Map.Entry<String, ValuePattern> entry : patterns.entrySet()) {
                ValuePattern p = entry.getValue();
                if (p.operation == OperationType.REMOVE) {
                    patterns.put(entry.getKey(), new ValuePattern(OperationType.SET, null, p.value));
                    hasRemove = false; // May no longer have remove fields
                }
            }
        }

        // Determine primary operation
        OperationType primaryOp;
        if (hasRequire) primaryOp = OperationType.REQUIRE;
        else if (hasModify) primaryOp = OperationType.MODIFY;
        else if (hasRemove) primaryOp = OperationType.REMOVE;
        else primaryOp = OperationType.SET;

        return new NBTPattern(patterns, primaryOp);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("primaryOp", primaryOperation.ordinal());
        NBTTagCompound patternsTag = new NBTTagCompound();
        for (Map.Entry<String, ValuePattern> entry : patterns.entrySet()) {
            NBTTagCompound pTag = new NBTTagCompound();
            entry.getValue()
                .writeToNBT(pTag);
            patternsTag.setTag(entry.getKey(), pTag);
        }
        nbt.setTag("patterns", patternsTag);
    }

    public static NBTPattern readFromNBT(NBTTagCompound nbt) {
        OperationType primaryOp = OperationType.values()[nbt.getInteger("primaryOp")];
        NBTTagCompound patternsTag = nbt.getCompoundTag("patterns");
        Map<String, ValuePattern> patterns = new HashMap<>();
        for (Object keyObj : patternsTag.func_150296_c()) {
            String key = (String) keyObj;
            patterns.put(key, ValuePattern.readFromNBT(patternsTag.getCompoundTag(key)));
        }
        return new NBTPattern(patterns, primaryOp);
    }

    public Map<String, ValuePattern> getPatterns() {
        return patterns;
    }

    /**
     * Represents a value pattern for a single NBT key.
     */
    public static class ValuePattern {

        final OperationType operation;
        final String operator; // For REQUIRE: >=, <=, >, <, ==, !=
        final Object value;

        public ValuePattern(OperationType operation, String operator, Object value) {
            this.operation = operation;
            this.operator = operator;
            this.value = value;
        }

        public OperationType getOperationType() {
            return operation;
        }

        public Object getValue() {
            return value;
        }

        /**
         * Check if the value in the NBT matches this pattern for filtering purposes.
         * 
         * @param nbt    The NBT compound
         * @param key    The key to check
         * @param strict If true, performing strict equality check even for SET
         *               operations.
         * @return True if matches
         */
        public boolean matches(NBTTagCompound nbt, String key, boolean strict) {
            if (!nbt.hasKey(key)) return false;

            switch (operation) {
                case REQUIRE:
                    return checkCondition(nbt, key);
                case SET:
                    // Minecraft specific heuristics
                    // These keys identify unique items/slots/enchants.
                    // They must match strictly even during target identification (non-strict mode).
                    if (key.equals("id") || key.equals("Slot") || key.equals("Name")) {
                        return checkEquality(nbt, key);
                    }
                    // For other keys, allow non-strict match to permit updates.
                    return !strict || checkEquality(nbt, key);
                default:
                    // For MODIFY/REMOVE, existence is already checked above.
                    return true;
            }
        }

        private boolean checkEquality(NBTTagCompound nbt, String key) {
            double current;
            NBTBase tag = nbt.getTag(key);
            if (tag instanceof NBTTagInt) current = ((NBTTagInt) tag).func_150287_d();
            else if (tag instanceof NBTTagShort) current = ((NBTTagShort) tag).func_150289_e();
            else if (tag instanceof NBTTagByte) current = ((NBTTagByte) tag).func_150290_f();
            else current = nbt.getDouble(key);

            if (value instanceof Number) {
                double target = ((Number) value).doubleValue();
                return Math.abs(current - target) < 0.01;
            } else if (value instanceof String) {
                String str = (String) value;
                try {
                    double target = Double.parseDouble(str);
                    return Math.abs(current - target) < 0.01;
                } catch (NumberFormatException e) {
                    return nbt.getString(key)
                        .equals(str);
                }
            }
            return false;
        }

        /**
         * Apply this pattern to the NBT.
         */
        public void apply(NBTTagCompound nbt, String key) {
            switch (operation) {
                case REMOVE:
                    nbt.removeTag(key);
                    break;
                case MODIFY:
                    modifyValue(nbt, key);
                    break;
                case SET:
                    setValue(nbt, key);
                    break;
                case REQUIRE:
                    // No modification for require
                    break;
            }
        }

        private boolean checkCondition(NBTTagCompound nbt, String key) {
            if (!nbt.hasKey(key)) return false;

            double current = nbt.getDouble(key);
            double target = toDouble(value);

            switch (operator) {
                case ">=":
                    return current >= target;
                case "<=":
                    return current <= target;
                case ">":
                    return current > target;
                case "<":
                    return current < target;
                case "==":
                    return Math.abs(current - target) < 0.0001;
                case "!=":
                    return Math.abs(current - target) >= 0.0001;
                default:
                    return false;
            }
        }

        private void modifyValue(NBTTagCompound nbt, String key) {
            double current = nbt.hasKey(key) ? nbt.getDouble(key) : 0.0;
            double modifier = toDouble(value);

            String op = operator != null ? operator : "+";
            double result;
            if (op.equals("-")) {
                result = current - modifier;
            } else {
                result = current + modifier;
            }

            NBTBase nbtValue = NBTTypeInference.parseNumeric(result);
            nbt.setTag(key, nbtValue);
        }

        private void setValue(NBTTagCompound nbt, String key) {
            NBTBase nbtValue;
            if (value instanceof String) {
                nbtValue = NBTTypeInference.parseValue((String) value);
            } else {
                nbtValue = NBTTypeInference.parseNumeric(toDouble(value));
            }
            nbt.setTag(key, nbtValue);
        }

        private double toDouble(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            } else if (obj instanceof String) {
                try {
                    String str = (String) obj;
                    // Remove operator prefix if present
                    if (str.startsWith("+") || str.startsWith("-")) {
                        return Double.parseDouble(str.substring(1));
                    }
                    return Double.parseDouble(str);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
            return 0.0;
        }

        /**
         * Parse a value pattern from JSON element.
         */
        public static ValuePattern parse(JsonElement element) {
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive()
                    .isNumber()) {
                    double num = element.getAsDouble();
                    if (num == 0) {
                        return new ValuePattern(OperationType.REMOVE, null, num);
                    } else {
                        return new ValuePattern(OperationType.SET, null, num);
                    }
                } else if (element.getAsJsonPrimitive()
                    .isString()) {
                        String str = element.getAsString();
                        return parseString(str);
                    }
            }

            // Default to SET with the value
            return new ValuePattern(OperationType.SET, null, element.toString());
        }

        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setInteger("op", operation.ordinal());
            if (operator != null) nbt.setString("operator", operator);
            if (value instanceof Number) {
                nbt.setDouble("val", ((Number) value).doubleValue());
                nbt.setBoolean("isNum", true);
            } else if (value instanceof String) {
                nbt.setString("val", (String) value);
                nbt.setBoolean("isNum", false);
            }
        }

        public static ValuePattern readFromNBT(NBTTagCompound nbt) {
            OperationType op = OperationType.values()[nbt.getInteger("op")];
            String operator = nbt.hasKey("operator") ? nbt.getString("operator") : null;
            Object value;
            if (nbt.getBoolean("isNum")) {
                value = nbt.getDouble("val");
            } else {
                value = nbt.getString("val");
            }
            return new ValuePattern(op, operator, value);
        }

        private static ValuePattern parseString(String str) {
            // Check for comparison operators
            if (str.startsWith(">=")) {
                return new ValuePattern(OperationType.REQUIRE, ">=", str.substring(2));
            } else if (str.startsWith("<=")) {
                return new ValuePattern(OperationType.REQUIRE, "<=", str.substring(2));
            } else if (str.startsWith("==")) {
                return new ValuePattern(OperationType.REQUIRE, "==", str.substring(2));
            } else if (str.startsWith("!=")) {
                return new ValuePattern(OperationType.REQUIRE, "!=", str.substring(2));
            } else if (str.startsWith(">")) {
                return new ValuePattern(OperationType.REQUIRE, ">", str.substring(1));
            } else if (str.startsWith("<")) {
                return new ValuePattern(OperationType.REQUIRE, "<", str.substring(1));
            }

            // Check for relative operators
            if (str.startsWith("+")) {
                return new ValuePattern(OperationType.MODIFY, "+", str.substring(1));
            } else if (str.startsWith("-")) {
                return new ValuePattern(OperationType.MODIFY, "-", str.substring(1));
            }

            // Absolute value
            return new ValuePattern(OperationType.SET, null, str);
        }
    }
}
