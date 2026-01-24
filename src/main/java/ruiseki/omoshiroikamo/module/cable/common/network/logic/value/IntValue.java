package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class IntValue implements ILogicValue {

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
    public boolean asBoolean() {
        return false;
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
    public String asString() {
        return Integer.toString(value);
    }

    @Override
    public String toString() {
        return "Int(" + value + ")";
    }
}
