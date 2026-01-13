package ruiseki.omoshiroikamo.module.cable.common.network.item;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ItemIndexClient {

    private final Object2IntOpenHashMap<ItemStackKey> db = new Object2IntOpenHashMap<>();

    private int serverVersion = -1;

    public void update(Object2IntOpenHashMap<ItemStackKey> newDB, int version) {
        db.clear();
        db.putAll(newDB);
        this.serverVersion = version;
    }

    public void destroy() {
        db.clear();
        serverVersion = -1;
    }

    public int getServerVersion() {
        return serverVersion;
    }

    /** Read-only usage by GUI */
    public Object2IntOpenHashMap<ItemStackKey> view() {
        return db;
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
