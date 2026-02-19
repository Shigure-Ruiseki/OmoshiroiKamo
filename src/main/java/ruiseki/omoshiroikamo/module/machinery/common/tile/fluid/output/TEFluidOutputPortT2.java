package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT2 extends TEFluidOutputPort {

    public TEFluidOutputPortT2() {
        super(MachineryConfig.getFluidPortCapacity(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
