package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.redstone;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicPart;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.ILogicReader;

public interface IRedstonePart extends ILogicPart, ILogicReader {

    int getRedstoneOutput();

    int getRedstoneInput();
}
