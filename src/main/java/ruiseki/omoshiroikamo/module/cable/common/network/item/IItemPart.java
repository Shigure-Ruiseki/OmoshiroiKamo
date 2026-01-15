package ruiseki.omoshiroikamo.module.cable.common.network.item;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface IItemPart extends ICablePart {

    /**
     * Maximum amount of item transferable per tick.
     */
    int getTransferLimit();

    default ItemNetwork getItemNetwork() {
        return getCable() != null ? (ItemNetwork) getCable().getNetwork(IItemPart.class) : null;
    }
}
