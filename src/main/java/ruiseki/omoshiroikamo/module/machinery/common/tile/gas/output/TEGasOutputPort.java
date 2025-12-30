package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public abstract class TEGasOutputPort extends AbstractGasPortTE {

    public TEGasOutputPort(int gasCapacity) {
        super(gasCapacity);
    }

    @Override
    public IO getIOLimit() {
        return IO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive()) {
            // FluidTransfer transfer = new FluidTransfer();
            // for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            // if (!getSideIO(direction).canOutput()) {
            // continue;
            // }
            // TileEntity sink = getPos().offset(direction)
            // .getTileEntity(worldObj);
            // transfer.push(this, direction, sink);
            // transfer.transfer();
            // }
        }
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
