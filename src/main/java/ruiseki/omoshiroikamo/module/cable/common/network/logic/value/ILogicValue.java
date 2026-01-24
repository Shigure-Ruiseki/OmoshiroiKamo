package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;

public interface ILogicValue {

    LogicType<?> getType();

    Object raw();

    boolean asBoolean();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    String asString();
}
