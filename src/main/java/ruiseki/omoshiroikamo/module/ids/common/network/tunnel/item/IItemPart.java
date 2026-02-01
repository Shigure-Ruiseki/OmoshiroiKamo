package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.writer.ILogicWriterPart;

public interface IItemPart extends ILogicWriterPart, IItemNet {

    /**
     * Maximum amount of item transferable per tick.
     */
    int getTransferLimit();
}
