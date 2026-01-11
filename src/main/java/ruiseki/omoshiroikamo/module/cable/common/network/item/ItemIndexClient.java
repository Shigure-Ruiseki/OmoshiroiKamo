package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.HashMap;
import java.util.Map;

public class ItemIndexClient {

    public static final ItemIndexClient INSTANCE = new ItemIndexClient();

    public Map<ItemStackKey, Integer> db = new HashMap<>();
    private int version = 0;

    public void update(Map<ItemStackKey, Integer> newDB) {
        db.clear();
        db.putAll(newDB);
        version++;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClientItemDatabase{\n");

        for (var e : db.entrySet()) {
            sb.append("  ")
                .append(e.getKey())
                .append(" x")
                .append(e.getValue())
                .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
