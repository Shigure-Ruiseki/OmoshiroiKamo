package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT2 extends TEFluidInputPort {

    public TEFluidInputPortT2() {
        super(MachineryConfig.getFluidPortCapacity(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
