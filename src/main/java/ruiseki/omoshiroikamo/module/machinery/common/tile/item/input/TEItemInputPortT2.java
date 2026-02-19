package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT2 extends TEItemInputPort {

    public TEItemInputPortT2() {
        super(MachineryConfig.getItemPortSlots(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
