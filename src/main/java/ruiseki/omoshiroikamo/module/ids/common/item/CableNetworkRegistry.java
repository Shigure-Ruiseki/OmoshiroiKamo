package ruiseki.omoshiroikamo.module.ids.common.item;

import java.util.HashMap;
import java.util.Map;

import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.LogicNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.IItemNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.ItemNetwork;

public final class CableNetworkRegistry {

    private static final Map<Class<? extends ICableNode>, NetworkFactory<?>> REGISTRY = new HashMap<>();

    public static <T extends ICableNode> void register(Class<T> partType, NetworkFactory<T> factory) {
        REGISTRY.put(partType, factory);
    }

    static {
        CableNetworkRegistry.register(IEnergyNet.class, EnergyNetwork::new);
        CableNetworkRegistry.register(IItemNet.class, ItemNetwork::new);
        CableNetworkRegistry.register(ILogicNet.class, LogicNetwork::new);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ICableNode> AbstractCableNetwork<T> create(Class<T> type) {
        NetworkFactory<T> factory = (NetworkFactory<T>) REGISTRY.get(type);
        if (factory == null) {
            throw new IllegalStateException("No network registered for " + type);
        }
        return factory.create();
    }

    public interface NetworkFactory<T extends ICableNode> {

        AbstractCableNetwork<T> create();
    }
}
