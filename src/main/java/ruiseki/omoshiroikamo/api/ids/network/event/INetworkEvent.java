package ruiseki.omoshiroikamo.api.ids.network.event;

import ruiseki.omoshiroikamo.api.ids.network.INetwork;

/**
 * An event posted in the {@link ruiseki.omoshiroikamo.api.ids.network.IPartNetwork} event bus.
 * 
 * @author rubensworks
 */
public interface INetworkEvent<N extends INetwork<N>> {

    /**
     * @return The network this event is thrown in.
     */
    public N getNetwork();

}
