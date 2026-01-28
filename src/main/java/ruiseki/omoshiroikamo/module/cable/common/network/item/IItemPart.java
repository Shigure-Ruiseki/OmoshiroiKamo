package ruiseki.omoshiroikamo.module.cable.common.network.item;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.writer.ILogicWriterPart;

public interface IItemPart extends ILogicWriterPart, IItemNet {

    /**
     * Maximum amount of item transferable per tick.
     */
    int getTransferLimit();
}
