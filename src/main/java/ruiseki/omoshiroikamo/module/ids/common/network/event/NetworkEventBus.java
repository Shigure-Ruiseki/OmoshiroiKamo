package ruiseki.omoshiroikamo.module.ids.common.network.event;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.EventBus;
import ruiseki.omoshiroikamo.api.ids.network.IEventListenableNetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.event.ICancelableNetworkEvent;
import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEvent;
import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEventBus;
import ruiseki.omoshiroikamo.api.util.CollectionHelpers;

/**
 * An event bus for {@link ruiseki.omoshiroikamo.api.ids.network.IPartNetwork} events where
 * {@link INetworkElement} instances can listen to.
 *
 * Partially based on Minecraft Forge's {@link EventBus} implementation.
 *
 * @author rubensworks
 */
public class NetworkEventBus<N extends INetwork<N>> implements INetworkEventBus<N> {

    private final Map<Class<? extends INetworkEvent<N>>, Set<IEventListenableNetworkElement<N, ?>>> listeners = Collections
        .synchronizedMap(
            Maps.<Class<? extends INetworkEvent<N>>, Set<IEventListenableNetworkElement<N, ?>>>newHashMap());

    @Override
    public void register(IEventListenableNetworkElement<N, ?> target, Class<? extends INetworkEvent<N>> eventType) {
        CollectionHelpers.addToMapSet(this.listeners, eventType, target);
    }

    @Override
    public void unregister(IEventListenableNetworkElement<N, ?> target, Class<? extends INetworkEvent<N>> eventType) {
        Set<IEventListenableNetworkElement<N, ?>> listeners = this.listeners.get(eventType);
        if (listeners != null) {
            listeners.remove(target);
        }
    }

    @Override
    public void unregister(IEventListenableNetworkElement<N, ?> target) {
        for (Class<? extends INetworkEvent<N>> eventType : target.getNetworkEventListener()
            .getSubscribedEvents()) {
            unregister(target, eventType);
        }
    }

    @Override
    public void post(INetworkEvent<N> event) {
        Set<IEventListenableNetworkElement<N, ?>> listeners = this.listeners.get(event.getClass());
        if (listeners != null) {
            for (IEventListenableNetworkElement listener : listeners) {
                listener.getNetworkEventListener()
                    .onEvent(event, listener);
            }
        }
    }

    @Override
    public boolean postCancelable(ICancelableNetworkEvent<N> event) {
        post(event);
        return !event.isCanceled();
    }

}
