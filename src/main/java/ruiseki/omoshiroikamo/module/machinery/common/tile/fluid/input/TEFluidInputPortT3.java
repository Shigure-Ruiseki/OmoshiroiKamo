package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT3 extends TEFluidInputPort {

    public TEFluidInputPortT3() {
        super(MachineryConfig.getFluidPortCapacity(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
