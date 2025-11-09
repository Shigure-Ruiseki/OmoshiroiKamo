package ruiseki.omoshiroikamo.api.entity.chicken;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ruiseki.omoshiroikamo.api.entity.BaseRegistry;

/**
 * Global registry for storing and retrieving {@link LiquidEggRegistryItem} entries.
 *
 * <p>
 * This registry provides:
 * <ul>
 * <li>Static registration of liquid egg types</li>
 * <li>Lookup by ID</li>
 * <li>Iteration over all registered liquid eggs</li>
 * </ul>
 *
 * <p>
 * Unlike other registries (e.g., {@link BaseRegistry}), this class
 * is intentionally lightweight and does not perform validation or inheritance checks.
 */
public class LiquidEggRegistry {

    /**
     * Internal storage mapping ID → LiquidEggRegistryItem.
     */
    private static final Map<Integer, LiquidEggRegistryItem> items = new HashMap<>();

    /**
     * Registers a new liquid egg type.
     *
     * <p>
     * Note: No duplicate ID validation is performed.
     *
     * @param liquidEgg the entry to register
     */
    public static void register(LiquidEggRegistryItem liquidEgg) {
        items.put(liquidEgg.getId(), liquidEgg);
    }

    /**
     * @return an unmodifiable view of all registered liquid egg entries
     */
    public static Collection<LiquidEggRegistryItem> getAll() {
        return items.values();
    }

    /**
     * Retrieves a registered liquid egg by its ID.
     *
     * @param id the unique ID of the egg
     * @return matching {@link LiquidEggRegistryItem}, or null if not found
     */
    public static LiquidEggRegistryItem findById(int id) {
        return items.get(id);
    }
}
