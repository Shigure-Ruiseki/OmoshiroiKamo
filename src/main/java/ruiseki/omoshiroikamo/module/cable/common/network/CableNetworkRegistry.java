package ruiseki.omoshiroikamo.module.cable.common.network;

import java.util.HashMap;
import java.util.Map;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;

public final class CableNetworkRegistry {

    private static final Map<Class<? extends ICablePart>, NetworkFactory<?>> REGISTRY = new HashMap<>();

    public static <T extends ICablePart> void register(Class<T> partType, NetworkFactory<T> factory) {
        REGISTRY.put(partType, factory);
    }

    static {
        CableNetworkRegistry.register(IEnergyPart.class, EnergyNetwork::new);
        CableNetworkRegistry.register(IItemPart.class, ItemNetwork::new);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ICablePart> AbstractCableNetwork<T> create(Class<T> type) {
        NetworkFactory<T> factory = (NetworkFactory<T>) REGISTRY.get(type);
        if (factory == null) {
            throw new IllegalStateException("No network registered for " + type);
        }
        return factory.create();
    }

    public interface NetworkFactory<T extends ICablePart> {

        AbstractCableNetwork<T> create();
    }
}
