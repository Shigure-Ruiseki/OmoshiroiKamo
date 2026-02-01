package ruiseki.omoshiroikamo.module.ids.common.network.logic.value;

public abstract class NumberValue implements ILogicValue {

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return String.valueOf(raw());
    }
}
