package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.module.ids.IDsCommon;

public abstract class AbstractCableNetwork<T extends ICableNode> {

    protected final List<T> nodes = new ArrayList<>();
    protected final Class<T> baseNodeClass;

    public AbstractCableNetwork(Class<T> baseNodeClass) {
        this.baseNodeClass = baseNodeClass;
    }

    @SuppressWarnings("unchecked")
    public void addNode(ICableNode part) {
        if (part == null) return;
        if (!baseNodeClass.isInstance(part)) return;

        for (T p : nodes) {
            if (p == part || (p.getCable() == part.getCable() && p.getSide() == part.getSide())) {
                return;
            }
        }

        if (nodes.isEmpty()) {
            IDsCommon.IDsNetworkTickHandler.registerNetwork(this);
        }
        nodes.add((T) part);
    }

    public Collection<T> getNodes() {
        return nodes;
    }

    public void destroyNetwork() {
        for (T part : nodes) {
            ICable cable = part.getCable();
            if (cable != null) {
                cable.getNetworks()
                    .remove(baseNodeClass);
            }
        }

        nodes.clear();
        IDsCommon.IDsNetworkTickHandler.unregisterNetwork(this);
    }

    public abstract void doNetworkTick();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [nodes=" + nodes.size() + "]";
    }
}
