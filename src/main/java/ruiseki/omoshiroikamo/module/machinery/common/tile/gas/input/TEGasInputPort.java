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
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
