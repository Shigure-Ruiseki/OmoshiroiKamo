package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT1 extends TEItemInputPort {

    public TEItemInputPortT1() {
        super(MachineryConfig.getItemPortSlots(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
