package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ruiseki.omoshiroikamo.api.ids.ICableNode;

public class CableGrid {

    private final Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> networks = new HashMap<>();

    public void addNetwork(Class<? extends ICableNode> type, AbstractCableNetwork<?> network) {

        networks.put(type, network);
        network.setGrid(this);
        network.onConstruct();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractCableNetwork<?>> T getNetwork(Class<T> clazz) {
        for (AbstractCableNetwork<?> net : networks.values()) {
            if (clazz.isInstance(net)) {
                return (T) net;
            }
        }
        return null;
    }

    public Collection<AbstractCableNetwork<?>> getAllNetworks() {
        return networks.values();
    }
}
