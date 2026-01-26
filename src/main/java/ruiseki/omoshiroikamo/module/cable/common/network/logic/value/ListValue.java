package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class ListValue implements ILogicValue {

    private final List<ILogicValue> values;

    public ListValue(List<ILogicValue> values) {
        this.values = values == null ? Collections.emptyList() : values;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.LIST;
    }

    @Override
    public Object raw() {
        return values;
    }

    @Override
    public boolean asBoolean() {
        return !values.isEmpty();
    }

    @Override
    public int asInt() {
        return values.size();
    }

    @Override
    public long asLong() {
        return values.size();
    }

    @Override
    public float asFloat() {
        return values.size();
    }

    @Override
    public double asDouble() {
        return values.size();
    }

    @Override
    public String asString() {
        if (values.isEmpty()) return "";
        StringJoiner joiner = new StringJoiner(", ");
        for (ILogicValue v : values) {
            joiner.add(v.asString());
        }
        return joiner.toString();
    }

    @Override
    public List<ILogicValue> asList() {
        return values;
    }
}
