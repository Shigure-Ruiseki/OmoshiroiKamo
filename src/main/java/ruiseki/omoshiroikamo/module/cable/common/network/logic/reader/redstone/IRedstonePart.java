package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.redstone;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.ILogicReaderPart;

public interface IRedstonePart extends ILogicReaderPart {

    int getRedstoneOutput();

    int getRedstoneInput();
}
