package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEFluidOutputPortT3 extends TEFluidOutputPort {

    public TEFluidOutputPortT3() {
        super(MachineryConfig.getFluidPortCapacity(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
