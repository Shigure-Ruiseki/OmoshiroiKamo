package ruiseki.omoshiroikamo.module.ids.common.network.logic.type;

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

    public boolean isNumeric() {
        return false;
    }

    @Override
    public String toString() {
        return id;
    }
}
