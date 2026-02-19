package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT6 extends TEFluidOutputPort {

    public TEFluidOutputPortT6() {
        super(MachineryConfig.getFluidPortCapacity(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
