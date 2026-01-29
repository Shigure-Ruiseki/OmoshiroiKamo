package ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.redstone;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.ILogicReaderPart;

public interface IRedstonePart extends ILogicReaderPart {

    int getRedstoneOutput();

    int getRedstoneInput();
}
