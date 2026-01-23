package ruiseki.omoshiroikamo.module.cable.common.network.logic.type;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public abstract class LogicType<T> {

    private final String id;

    protected LogicType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isComparable() {
        return false;
    }

    public boolean isEquatable() {
        return true;
    }

    public abstract T cast(ILogicValue value);

    @Override
    public String toString() {
        return id;
    }
}
