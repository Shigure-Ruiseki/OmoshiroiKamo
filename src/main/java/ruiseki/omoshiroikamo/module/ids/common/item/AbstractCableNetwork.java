package ruiseki.omoshiroikamo.module.ids.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.module.ids.IDsModule;

public abstract class AbstractCableNetwork<T extends ICableNode> {

    private CableGrid grid;
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
            IDsModule.IDsNetworkTickHandler.registerNetwork(this);
        }
        nodes.add((T) part);
    }

    public Collection<T> getNodes() {
        return nodes;
    }

    public void destroyNetwork() {

        onDestroy();

        for (T part : nodes) {
            ICable cable = part.getCable();
            if (cable != null) {
                cable.getNetworks()
                    .remove(baseNodeClass);
            }
        }

        nodes.clear();
        IDsModule.IDsNetworkTickHandler.unregisterNetwork(this);
    }

    public Class<T> getBaseNodeClass() {
        return baseNodeClass;
    }

    public void setGrid(CableGrid grid) {
        this.grid = grid;
    }

    public CableGrid getGrid() {
        return grid;
    }

    public abstract void doNetworkTick();

    public void onConstruct() {}

    public void onDestroy() {}

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [nodes=" + nodes.size() + "]";
    }
}
