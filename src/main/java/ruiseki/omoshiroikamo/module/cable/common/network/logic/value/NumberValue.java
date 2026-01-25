package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

public abstract class NumberValue implements ILogicValue {

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return raw().toString();
    }
}
