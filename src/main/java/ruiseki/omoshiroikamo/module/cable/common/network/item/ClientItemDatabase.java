package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.HashMap;
import java.util.Map;

public class ClientItemDatabase {

    public static final ClientItemDatabase INSTANCE = new ClientItemDatabase();
    public Map<ItemKey, Long> db = new HashMap<>();

    public void update(Map<ItemKey, Long> newDB) {
        db.clear();
        db.putAll(newDB);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClientItemDatabase{\n");

        for (Map.Entry<ItemKey, Long> e : db.entrySet()) {
            sb.append("  ")
                .append(
                    e.getKey()
                        .toString())
                .append(" x")
                .append(e.getValue())
                .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }

}
