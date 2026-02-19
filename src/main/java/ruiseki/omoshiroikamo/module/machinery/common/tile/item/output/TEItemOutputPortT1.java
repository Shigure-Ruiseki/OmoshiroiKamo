package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT1 extends TEItemOutputPort {

    public TEItemOutputPortT1() {
        super(MachineryConfig.getItemPortSlots(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
