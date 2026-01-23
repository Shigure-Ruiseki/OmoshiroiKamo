package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class BoolValue implements ILogicValue {

    private final boolean value;

    BoolValue(boolean value) {
        this.value = value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.BOOLEAN;
    }

    @Override
    public Object raw() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public int asInt() {
        return value ? 1 : 0;
    }

    @Override
    public String asString() {
        return Boolean.toString(value);
    }

    @Override
    public String toString() {
        return "Bool(" + value + ")";
    }
}
