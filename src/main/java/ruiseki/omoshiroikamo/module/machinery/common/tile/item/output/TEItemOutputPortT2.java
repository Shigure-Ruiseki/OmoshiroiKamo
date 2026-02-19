package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT2 extends TEItemOutputPort {

    public TEItemOutputPortT2() {
        super(MachineryConfig.getItemPortSlots(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
