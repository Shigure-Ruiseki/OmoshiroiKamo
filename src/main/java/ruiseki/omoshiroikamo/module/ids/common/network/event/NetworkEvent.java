package ruiseki.omoshiroikamo.module.ids.common.network.event;

import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEvent;

/**
 * An event posted in the {@link ruiseki.omoshiroikamo.api.ids.network.IPartNetwork} event bus.
 * 
 * @author rubensworks
 */
public class NetworkEvent<N extends INetwork<N>> implements INetworkEvent<N> {

    private final N network;

    public NetworkEvent(N network) {
        this.network = network;
    }

    @Override
    public N getNetwork() {
        return this.network;
    }

}
