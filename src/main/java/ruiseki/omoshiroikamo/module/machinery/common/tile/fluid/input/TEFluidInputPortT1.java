package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT1 extends TEFluidInputPort {

    public TEFluidInputPortT1() {
        super(MachineryConfig.getFluidPortCapacity(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
