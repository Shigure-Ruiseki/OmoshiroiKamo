package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

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
