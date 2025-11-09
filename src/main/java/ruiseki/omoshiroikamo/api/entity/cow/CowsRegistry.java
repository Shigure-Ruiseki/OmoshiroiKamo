package ruiseki.omoshiroikamo.api.entity.cow;

import ruiseki.omoshiroikamo.api.entity.BaseRegistry;

/**
 * Registry storing all {@link CowsRegistryItem} cow types.
 *
 * <p>
 * This registry provides:
 * <ul>
 * <li>Registration of custom cow definitions</li>
 * <li>Validation of duplicate IDs or names</li>
 * <li>Child breeding lookup via inherited BaseRegistry logic</li>
 * <li>Spawn filtering and chance calculations (inherited)</li>
 * </ul>
 *
 * <p>
 * This is a singleton registry: use {@link #INSTANCE} to access it.
 */
public class CowsRegistry extends BaseRegistry<CowsRegistryItem> {

    /**
     * Global singleton instance of the cow registry.
     */
    public static final CowsRegistry INSTANCE = new CowsRegistry();

    private CowsRegistry() {}
}
