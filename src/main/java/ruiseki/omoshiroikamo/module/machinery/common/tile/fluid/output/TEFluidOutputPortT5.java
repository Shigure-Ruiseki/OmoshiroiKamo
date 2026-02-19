package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT5 extends TEFluidOutputPort {

    public TEFluidOutputPortT5() {
        super(MachineryConfig.getFluidPortCapacity(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
