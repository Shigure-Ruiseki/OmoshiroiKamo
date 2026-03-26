package ruiseki.omoshiroikamo.core.energy.capability;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Central registry for energy integration delegates.
 * Maintains a priority-sorted list of delegates that can handle
 * non-standard energy systems (beyond basic CoFH RF).
 *
 * <p>
 * This registry enables the ExternalEnergyProxy to support multiple
 * mod-specific energy APIs without coupling to specific mods. Each mod
 * integration registers its delegate during initialization, and the proxy
 * iterates through them at runtime.
 *
 * <p>
 * <b>Lifecycle:</b>
 * <ol>
 * <li>During mod initialization (preInit), integrations register delegates</li>
 * <li>First call to {@link #getDelegates()} sorts the list by priority</li>
 * <li>ExternalEnergyProxy uses the sorted list for energy operations</li>
 * </ol>
 *
 * <p>
 * <b>Thread Safety:</b> This registry is not thread-safe. All registration
 * must occur during mod initialization before concurrent access begins.
 *
 * <p>
 * <b>Example Usage:</b>
 * 
 * <pre>
 * {@code
 * // During mod integration initialization
 * EnergyIntegrationRegistry.registerDelegate(new EnderIOEnergyDelegate());
 *
 * // During energy transfer in ExternalEnergyProxy
 * for (IEnergyIntegrationDelegate delegate : EnergyIntegrationRegistry.getDelegates()) {
 *     Integer result = delegate.tryReceive(te, side, amount, simulate);
 *     if (result != null) return result;
 * }
 * }
 * </pre>
 */
public class EnergyIntegrationRegistry {

    private static final List<IEnergyIntegrationDelegate> delegates = new ArrayList<>();
    private static boolean sorted = false;

    /**
     * Register an energy integration delegate.
     * Should be called during mod integration initialization.
     *
     * <p>
     * <b>IMPORTANT:</b> Registration must occur before any energy operations
     * begin (typically during preInit phase). Delegates are sorted by priority
     * on first access to {@link #getDelegates()}.
     *
     * @param delegate The delegate to register
     * @throws IllegalArgumentException if delegate is null
     */
    public static void registerDelegate(IEnergyIntegrationDelegate delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Cannot register null delegate");
        }
        delegates.add(delegate);
        sorted = false;
    }

    /**
     * Get all registered delegates in priority order.
     * Delegates are sorted by priority (highest first) on first access.
     *
     * <p>
     * The returned list should not be modified. It is used directly
     * by ExternalEnergyProxy for energy operations.
     *
     * <p>
     * <b>Priority Order:</b>
     * <ul>
     * <li>Higher priority delegates are checked first</li>
     * <li>First delegate that returns non-null handles the operation</li>
     * <li>If all delegates return null, falls back to CoFH RF</li>
     * </ul>
     *
     * @return List of delegates sorted by priority (highest first)
     */
    public static List<IEnergyIntegrationDelegate> getDelegates() {
        if (!sorted && !delegates.isEmpty()) {
            delegates.sort(
                Comparator.comparingInt(IEnergyIntegrationDelegate::getPriority)
                    .reversed());
            sorted = true;
        }
        return delegates;
    }

    /**
     * Get the number of registered delegates.
     * Primarily used for debugging and testing.
     *
     * @return Number of registered delegates
     */
    public static int getDelegateCount() {
        return delegates.size();
    }

    /**
     * Clear all registered delegates.
     * <b>WARNING:</b> This method is intended for testing only.
     * Calling this during normal operation will break energy integration.
     */
    public static void clearForTesting() {
        delegates.clear();
        sorted = false;
    }
}
