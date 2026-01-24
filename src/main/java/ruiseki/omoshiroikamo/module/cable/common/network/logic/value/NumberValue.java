package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

public abstract class NumberValue implements ILogicValue {

    @Override
    public boolean asBoolean() {
        return asDouble() != 0;
    }

    @Override
    public String asString() {
        return raw().toString();
    }
}
