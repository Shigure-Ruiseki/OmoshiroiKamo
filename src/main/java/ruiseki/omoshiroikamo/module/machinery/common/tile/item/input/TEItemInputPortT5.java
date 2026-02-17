package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT5 extends TEItemInputPort {

    public TEItemInputPortT5() {
        super(MachineryConfig.getItemPortSlots(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
