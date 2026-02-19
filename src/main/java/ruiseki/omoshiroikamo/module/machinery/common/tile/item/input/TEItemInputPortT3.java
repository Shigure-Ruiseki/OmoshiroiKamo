package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT3 extends TEItemInputPort {

    public TEItemInputPortT3() {
        super(MachineryConfig.getItemPortSlots(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
