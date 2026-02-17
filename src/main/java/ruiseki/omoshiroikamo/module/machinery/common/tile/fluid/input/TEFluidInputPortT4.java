package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidInputPortT4 extends TEFluidInputPort {

    public TEFluidInputPortT4() {
        super(MachineryConfig.getFluidPortCapacity(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
