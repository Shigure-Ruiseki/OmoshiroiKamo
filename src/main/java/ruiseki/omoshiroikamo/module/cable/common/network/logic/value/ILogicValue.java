package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;

public interface ILogicValue {

    LogicType<?> getType();

    Object raw();

    boolean asBoolean();

    int asInt();

    double asDouble();

    String asString();
}
