package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT6 extends TEItemInputPort {

    public TEItemInputPortT6() {
        super(MachineryConfig.getItemPortSlots(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
