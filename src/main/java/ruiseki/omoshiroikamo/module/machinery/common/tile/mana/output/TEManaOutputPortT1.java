package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEManaOutputPortT1 extends TEManaOutputPort {

    public TEManaOutputPortT1() {
        super(MachineryConfig.manaPortCapacity, MachineryConfig.manaPortTransfer);
    }

    @Override
    public int getTier() {
        return 1;
    }
}
