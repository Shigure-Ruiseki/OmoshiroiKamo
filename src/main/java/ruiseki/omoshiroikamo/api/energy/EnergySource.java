package ruiseki.omoshiroikamo.api.energy;

import javax.annotation.Nonnegative;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

/**
 * Represents a source of energy. Should only be retrieved via
 * {@link CapabilityProvider#getCapability(Class, ForgeDirection)}.
 *
 * <p>
 * A source must be effectively stateless. Its methods should always reflect the
 * current state of the world. Caches may be used for performance, but modifying
 * the backing storage while using this interface is undefined behavior.
 * </p>
 */
public interface EnergySource {

    /**
     * Called once per transfer, before any configuration takes place. If this source
     * is persistent, its state should be reset to match the world.
     */
    default void resetSource() {}

    /**
     * Pulls energy from this source. No guarantees are made that the full requested
     * amount can be extracted.
     *
     * @param maxAmount The maximum amount of energy to pull
     * @param simulate  If true, the operation should only simulate the extraction
     * @return The amount of energy actually extracted
     */
    @Nonnegative
    int pull(int maxAmount, boolean simulate);

    /**
     * Returns an {@link EnergyAccess} over internal sub-sources or energy slots, if supported.
     * Modifying the underlying storage while iterating is undefined behavior.
     *
     * @return an {@link EnergyAccess}, or {@code null} if iteration is not supported
     */
    @Nullable
    default EnergyAccess sourceAccess() {
        return null;
    }
}
