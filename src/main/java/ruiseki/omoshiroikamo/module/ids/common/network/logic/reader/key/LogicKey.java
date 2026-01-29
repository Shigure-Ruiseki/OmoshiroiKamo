package ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.key;

import java.util.Objects;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicType;

public class LogicKey {

    private final String id;
    private final LogicType<?> defaultType;

    public LogicKey(String id, LogicType<?> type) {
        this.id = id;
        this.defaultType = type;
    }

    public String getId() {
        return id;
    }

    public LogicType<?> getDefaultType() {
        return defaultType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicKey)) return false;
        return id.equals(((LogicKey) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LogicKey[" + id + "]";
    }
}
