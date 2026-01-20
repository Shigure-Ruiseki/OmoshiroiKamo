package ruiseki.omoshiroikamo.module.cable.common.network.logic.redstone;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicPart;

public interface IRedstonePart extends ILogicPart {

    int getRedstoneOutput();

    int getRedstoneInput();

    int getRedstone();

    boolean isLowRedstone();

    boolean isHighRedstone();
}
