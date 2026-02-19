package ruiseki.omoshiroikamo.api.ids.network.event;

import ruiseki.omoshiroikamo.api.ids.network.INetwork;

/**
 * An event posted in the {@link ruiseki.omoshiroikamo.api.ids.network.IPartNetwork} event bus.
 * 
 * @author rubensworks
 */
public interface ICancelableNetworkEvent<N extends INetwork<N>> extends INetworkEvent<N> {

    /**
     * Cancel this event from further processing.
     */
    public void cancel();

    /**
     * @return If this event was canceled.
     */
    public boolean isCanceled();

}
