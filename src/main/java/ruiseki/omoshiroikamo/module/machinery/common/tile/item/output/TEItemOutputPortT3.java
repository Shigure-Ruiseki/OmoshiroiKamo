package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT3 extends TEItemOutputPort {

    public TEItemOutputPortT3() {
        super(MachineryConfig.getItemPortSlots(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
