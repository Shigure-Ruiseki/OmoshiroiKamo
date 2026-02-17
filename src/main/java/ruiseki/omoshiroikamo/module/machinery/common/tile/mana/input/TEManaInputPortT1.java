package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEManaInputPortT1 extends TEManaInputPort {

    public TEManaInputPortT1() {
        super(MachineryConfig.manaPortCapacity, MachineryConfig.manaPortTransfer);
    }

    @Override
    public int getTier() {
        return 1;
    }
}
