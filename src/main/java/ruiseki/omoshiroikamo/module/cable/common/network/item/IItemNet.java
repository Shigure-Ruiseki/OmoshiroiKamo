package ruiseki.omoshiroikamo.module.cable.common.network.item;

import ruiseki.omoshiroikamo.api.cable.ICableNode;

public interface IItemNet extends ICableNode {

    default ItemNetwork getItemNetwork() {
        return getCable() != null ? (ItemNetwork) getCable().getNetwork(IItemNet.class) : null;
    }
}
