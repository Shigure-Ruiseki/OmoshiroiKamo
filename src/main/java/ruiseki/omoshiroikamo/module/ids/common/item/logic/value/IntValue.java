package ruiseki.omoshiroikamo.module.ids.common.item.logic.value;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicTypes;

public class IntValue extends NumberValue {

    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.INT;
    }

    @Override
    public Object raw() {
        return value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public String toString() {
        return "Int(" + value + ")";
    }
}
