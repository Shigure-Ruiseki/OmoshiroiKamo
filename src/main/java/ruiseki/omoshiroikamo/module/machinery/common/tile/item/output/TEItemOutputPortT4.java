package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEItemOutputPortT4 extends TEItemOutputPort {

    public TEItemOutputPortT4() {
        super(MachineryConfig.getItemPortSlots(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
