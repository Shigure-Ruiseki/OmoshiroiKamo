package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class FloatValue extends NumberValue {

    private final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.FLOAT;
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
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String toString() {
        return "Float(" + value + ")";
    }
}
