package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item;

import ruiseki.omoshiroikamo.api.ids.ICableNode;

public interface IItemNet extends ICableNode {

    default ItemNetwork getItemNetwork() {
        return getCable() != null ? (ItemNetwork) getCable().getNetwork(IItemNet.class) : null;
    }
}
