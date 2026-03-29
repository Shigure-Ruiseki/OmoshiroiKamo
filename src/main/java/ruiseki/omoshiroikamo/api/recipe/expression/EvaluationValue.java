package ruiseki.omoshiroikamo.api.recipe.expression;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

/**
 * A wrapper class that holds a value of different types.
 * This class is used to unify the return types of expressions and avoid
 * frequent boxing/parsing.
 */
public final class EvaluationValue {

    public enum Type {
        NUMERIC,
        STRING,
        BOOLEAN,
        NBT,
        VOID
    }

    public static final EvaluationValue ZERO = new EvaluationValue(0.0);
    public static final EvaluationValue ONE = new EvaluationValue(1.0);
    public static final EvaluationValue TRUE = new EvaluationValue(true);
    public static final EvaluationValue FALSE = new EvaluationValue(false);
    public static final EvaluationValue VOID = new EvaluationValue(Type.VOID, 0.0, null, false, null);

    private final Type type;
    private final double numericValue;
    private final String stringValue;
    private final boolean booleanValue;
    private final NBTBase nbtValue;

    private EvaluationValue(Type type, double num, String str, boolean bool, NBTBase nbt) {
        this.type = type;
        this.numericValue = num;
        this.stringValue = str;
        this.booleanValue = bool;
        this.nbtValue = nbt;
    }

    public EvaluationValue(double value) {
        this(Type.NUMERIC, value, null, value != 0, null);
    }

    public EvaluationValue(String value) {
        this(Type.STRING, 0.0, value, value != null && !value.isEmpty(), null);
    }

    public EvaluationValue(boolean value) {
        this(Type.BOOLEAN, value ? 1.0 : 0.0, null, value, null);
    }

    public EvaluationValue(NBTBase value) {
        this(Type.NBT, 0.0, null, value != null, value);
    }

    public Type getType() {
        return type;
    }

    public double asDouble() {
        if (type == Type.NUMERIC) return numericValue;
        if (type == Type.BOOLEAN) return booleanValue ? 1.0 : 0.0;
        if (type == Type.STRING && stringValue != null) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        if (type == Type.NBT && nbtValue != null) {
            if (nbtValue instanceof NBTTagByte tag) return tag.func_150290_f();
            if (nbtValue instanceof NBTTagShort tag) return tag.func_150289_e();
            if (nbtValue instanceof NBTTagInt tag) return tag.func_150287_d();
            if (nbtValue instanceof NBTTagLong tag) return tag.func_150291_c();
            if (nbtValue instanceof NBTTagFloat tag) return tag.func_150288_h();
            if (nbtValue instanceof NBTTagDouble tag) return tag.func_150286_g();
            if (nbtValue instanceof NBTTagString str) {
                try {
                    return Double.parseDouble(str.func_150285_a_());
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0.0;
    }

    public String asString() {
        if (type == Type.STRING) return stringValue != null ? stringValue : "";
        if (type == Type.NUMERIC) {
            if (numericValue == (long) numericValue) return String.valueOf((long) numericValue);
            return String.valueOf(numericValue);
        }
        if (type == Type.BOOLEAN) return String.valueOf(booleanValue);
        if (type == Type.NBT && nbtValue != null) return nbtValue.toString();
        return "";
    }

    public boolean asBoolean() {
        if (type == Type.BOOLEAN) return booleanValue;
        if (type == Type.NUMERIC) return numericValue != 0;
        if (type == Type.STRING) return stringValue != null && !stringValue.isEmpty();
        if (type == Type.NBT) return nbtValue != null;
        return false;
    }

    public NBTBase asNbt() {
        if (type == Type.NBT) return nbtValue;
        if (type == Type.NUMERIC) return new NBTTagDouble(numericValue);
        if (type == Type.STRING) return new NBTTagString(stringValue);
        return null;
    }

    public boolean isNumeric() {
        return type == Type.NUMERIC;
    }

    public boolean isString() {
        return type == Type.STRING;
    }

    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    public boolean isNbt() {
        return type == Type.NBT;
    }

    public boolean isVoid() {
        return type == Type.VOID;
    }

    public boolean isZero() {
        return type == Type.NUMERIC && numericValue == 0;
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Strict equality: same type and same value. Satisfies the equals/hashCode
     * contract.
     * For cross-type comparison (e.g. string "1.0" vs numeric 1.0), use
     * {@link #looseEquals}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationValue)) return false;
        EvaluationValue that = (EvaluationValue) o;
        if (this.type != that.type) return false;
        switch (type) {
            case NUMERIC:
                return Double.compare(that.numericValue, numericValue) == 0;
            case STRING:
                return stringValue != null && stringValue.equals(that.stringValue);
            case BOOLEAN:
                return booleanValue == that.booleanValue;
            case NBT:
                return nbtValue != null && nbtValue.equals(that.nbtValue);
            default:
                return true;
        }
    }

    /**
     * Loose (duck-typed) equality for use in recipe expression comparisons (==,
     * !=).
     * If either side is numeric, both are compared as doubles with a small
     * tolerance (1e-4).
     * Otherwise compared as strings.
     */
    public boolean looseEquals(EvaluationValue that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.isNumeric() || that.isNumeric()) return Math.abs(this.asDouble() - that.asDouble()) < 1e-4;
        if (this.type == that.type) return this.equals(that);
        return this.asString()
            .equals(that.asString());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = type.hashCode();
        temp = Double.doubleToLongBits(numericValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        result = 31 * result + (booleanValue ? 1 : 0);
        result = 31 * result + (nbtValue != null ? nbtValue.hashCode() : 0);
        return result;
    }
}
