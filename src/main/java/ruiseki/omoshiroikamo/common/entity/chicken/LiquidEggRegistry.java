package ruiseki.omoshiroikamo.common.entity.chicken;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LiquidEggRegistry {

    private static final Map<Integer, LiquidEggRegistryItem> items = new HashMap<>();

    public static void register(LiquidEggRegistryItem liquidEgg) {
        items.put(liquidEgg.getId(), liquidEgg);
    }

    public static Collection<LiquidEggRegistryItem> getAll() {
        return items.values();
    }

    public static LiquidEggRegistryItem findById(int id) {
        return items.get(id);
    }
}
