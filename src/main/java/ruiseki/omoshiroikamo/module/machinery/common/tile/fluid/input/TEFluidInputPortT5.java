package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT5 extends TEFluidInputPort {

    public TEFluidInputPortT5() {
        super(MachineryConfig.getFluidPortCapacity(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
