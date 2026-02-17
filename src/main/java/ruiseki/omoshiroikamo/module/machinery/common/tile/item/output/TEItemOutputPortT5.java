package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT5 extends TEItemOutputPort {

    public TEItemOutputPortT5() {
        super(MachineryConfig.getItemPortSlots(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
