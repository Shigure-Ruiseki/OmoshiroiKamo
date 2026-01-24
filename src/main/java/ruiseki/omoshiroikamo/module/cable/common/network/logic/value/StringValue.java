package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class StringValue implements ILogicValue {

    private final String value;

    StringValue(String value) {
        this.value = value == null ? "" : value;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.STRING;
    }

    @Override
    public Object raw() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return !value.isEmpty();
    }

    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String toString() {
        return "String(\"" + value + "\")";
    }
}
