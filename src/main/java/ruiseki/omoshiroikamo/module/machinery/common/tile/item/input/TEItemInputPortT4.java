package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemInputPortT4 extends TEItemInputPort {

    public TEItemInputPortT4() {
        super(MachineryConfig.getItemPortSlots(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
