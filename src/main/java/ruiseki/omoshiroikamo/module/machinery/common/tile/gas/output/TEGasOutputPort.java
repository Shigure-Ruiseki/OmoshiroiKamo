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
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
