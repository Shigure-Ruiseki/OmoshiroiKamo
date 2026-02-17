package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicTypes;

public class LongValue extends NumberValue {

    private final long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.LONG;
    }

    @Override
    public Object raw() {
        return value;
    }

    @Override
    public int asInt() {
        return Math.toIntExact(value);
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
    public long asLong() {
        return value;
    }

    @Override
    public String toString() {
        return "Long(" + value + ")";
    }
}
