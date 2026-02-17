package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT4 extends TEFluidOutputPort {

    public TEFluidOutputPortT4() {
        super(MachineryConfig.getFluidPortCapacity(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
