package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT1 extends TEFluidOutputPort {

    public TEFluidOutputPortT1() {
        super(MachineryConfig.getFluidPortCapacity(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
