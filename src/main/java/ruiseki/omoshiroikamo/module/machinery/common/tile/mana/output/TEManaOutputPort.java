package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public abstract class TEManaOutputPort extends AbstractManaPortTE {

    protected TEManaOutputPort(int manaCapacity, int manaMaxReceive) {
        super(manaCapacity, manaMaxReceive);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return false;
    }
}
