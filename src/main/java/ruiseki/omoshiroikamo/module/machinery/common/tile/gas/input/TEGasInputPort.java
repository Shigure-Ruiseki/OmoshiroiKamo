package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public abstract class TEGasInputPort extends AbstractGasPortTE {

    public TEGasInputPort(int gasCapacity) {
        super(gasCapacity);
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive() && !tank.isFull()) {
            // FluidTransfer transfer = new FluidTransfer();
            // for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            // if (!getSideIO(direction).canInput()) {
            // continue;
            // }
            // TileEntity source = getPos().offset(direction)
            // .getTileEntity(worldObj);
            // transfer.pull(this, direction, source);
            // transfer.transfer();
            // }
        }
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
