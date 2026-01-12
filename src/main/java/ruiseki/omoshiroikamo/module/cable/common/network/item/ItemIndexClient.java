package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.HashMap;
import java.util.Map;

public class ItemIndexClient {

    public static final ItemIndexClient INSTANCE = new ItemIndexClient();

    public Map<ItemStackKey, Integer> db = new HashMap<>();
    private int serverVersion = -1;

    public void update(Map<ItemStackKey, Integer> newDB, int serverVersion) {
        db.clear();
        db.putAll(newDB);
        this.serverVersion = serverVersion;
    }

    public void destroy() {
        db.clear();
        this.serverVersion = -1;
    }

    public int getServerVersion() {
        return serverVersion;
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
