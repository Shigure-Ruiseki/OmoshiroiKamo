package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input;

import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public abstract class TEManaInputPort extends AbstractManaPortTE {

    protected TEManaInputPort(int manaCapacity, int manaMaxReceive) {
        super(manaCapacity, manaMaxReceive);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
