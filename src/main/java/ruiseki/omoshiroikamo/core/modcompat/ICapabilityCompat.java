package ruiseki.omoshiroikamo.core.modcompat;

import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.core.capabilities.ICapabilityProvider;

/**
 * Compatibility for external mod capabilities.
 * It is safe to do anything with the target capability, since this will
 * only be loaded if the target capability is available.
 *
 * @param <P> The type of capability provider.
 * @author rubensworks
 */
public interface ICapabilityCompat<P extends ICapabilityProvider> {

    public void attach(P provider);

    /**
     * Reference to a capability instance.
     *
     * @param <C> Tne type of capability.
     */
    public static interface ICapabilityReference<C> {

        /**
         * @return A capability, this will probably refer to a field annotated with
         *         {@link CapabilityInject}.
         */
        public Capability<C> getCapability();
    }

}
