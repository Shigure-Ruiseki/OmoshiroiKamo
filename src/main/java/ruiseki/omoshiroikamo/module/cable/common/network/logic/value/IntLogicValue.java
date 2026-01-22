package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.LogicValue;

public record IntLogicValue(int value) implements LogicValue {

    public boolean asBoolean() {
        return value > 0;
    }

    public int asInt() {
        return value;
    }

    public String asString() {
        return String.valueOf(value);
    }
}
