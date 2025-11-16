package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

/**
 * Represents a sink that can accept energy. Should only be retrieved via
 * {@link CapabilityProvider#getCapability(Class, ForgeDirection)}.
 *
 * <p>
 * A sink must be effectively stateless. Its methods should always reflect the
 * current state of the world. Caches may be used for performance, but modifying
 * the backing storage while using this interface is undefined behavior.
 * </p>
 */
public interface EnergySink {

    /**
     * Called once per transfer, before any configuration takes place. If this sink
     * is persistent, its state should be reset to match the world.
     */
    default void resetSink() {}

    /**
     * Inserts energy into this sink. This operation is <em>not atomic</em>. There is
     * no guarantee that the sink will accept all the energy; this is a best-effort operation.
     *
     * @param amount Amount of energy to insert
     * @return The amount of energy that could not be inserted (rejected)
     */
    int store(int amount);

    /**
     * Returns an {@link EnergyAccess} over internal sub-sinks or energy slots, if supported.
     * Modifying the underlying storage while iterating is undefined behavior.
     *
     * @return an {@link EnergyAccess}, or {@code null} if iteration is not supported
     */
    @Nullable
    default EnergyAccess sinkAccess() {
        return null;
    }

    /**
     * Chains this sink with another. Energy is first fed into this sink, then any
     * rejected energy is fed into the next sink.
     *
     * @param next The next sink to feed rejected energy into
     * @return A new {@link EnergySink} representing the chained sinks
     */
    default EnergySink then(EnergySink next) {
        return (amount) -> {
            int rejected = this.store(amount);
            return rejected == 0 ? 0 : next.store(rejected);
        };
    }

    /**
     * Static helper for chaining two sinks, handling {@code null} values gracefully.
     *
     * @param first  The first sink
     * @param second The second sink
     * @return A chained {@link EnergySink}, or the non-null sink if one is null
     */
    static EnergySink chain(EnergySink first, EnergySink second) {
        if (first == null || second == null) {
            return first != null ? first : second;
        }
        return first.then(second);
    }
}
