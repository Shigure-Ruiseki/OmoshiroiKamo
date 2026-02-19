package ruiseki.omoshiroikamo.api.ids.network;

import java.util.Set;

import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEvent;

/**
 * Interface to indicate delegates of a network element instance.
 * 
 * @author rubensworks
 */
public interface INetworkEventListener<N extends INetwork<N>, E> {

    /**
     * @return If this should be registered to the network event bus for listening to network events.
     */
    public boolean hasEventSubscriptions();

    /**
     * @return The static set of events this listener should be subscribed to.
     */
    public Set<Class<? extends INetworkEvent<N>>> getSubscribedEvents();

    /**
     * Can be called at any time by the {@link ruiseki.omoshiroikamo.api.ids.network.event.INetworkEventBus}.
     * Only events in the set from {@link INetworkEventListener#getSubscribedEvents()} will be received.
     * 
     * @param event          The received event.
     * @param networkElement The network element.
     */
    public void onEvent(INetworkEvent<N> event, E networkElement);

}
