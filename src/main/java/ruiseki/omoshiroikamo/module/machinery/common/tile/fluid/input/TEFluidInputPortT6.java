package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT6 extends TEFluidInputPort {

    public TEFluidInputPortT6() {
        super(MachineryConfig.getFluidPortCapacity(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
