package ruiseki.omoshiroikamo.module.ids.common.item.logic.value;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicTypes;

public class NullValue implements ILogicValue {

    @Override
    public LogicType<?> getType() {
        return LogicTypes.NULL;
    }

    @Override
    public Object raw() {
        return null;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public String asString() {
        return "";
    }
}
