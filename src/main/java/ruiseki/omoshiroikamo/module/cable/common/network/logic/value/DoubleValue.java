package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class DoubleValue extends NumberValue {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.DOUBLE;
    }

    @Override
    public Object raw() {
        return value;
    }

    @Override
    public int asInt() {
        return (int) value;
    }

    @Override
    public long asLong() {
        return (long) value;
    }

    @Override
    public float asFloat() {
        return (float) value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String toString() {
        return "Double(" + value + ")";
    }
}
