package ruiseki.omoshiroikamo.module.cable.common.network.item;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface IItemPart extends ICablePart, IItemNet {

    /**
     * Maximum amount of item transferable per tick.
     */
    int getTransferLimit();
}
