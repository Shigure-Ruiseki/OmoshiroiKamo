package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT6 extends TEItemOutputPort {

    public TEItemOutputPortT6() {
        super(MachineryConfig.getItemPortSlots(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
