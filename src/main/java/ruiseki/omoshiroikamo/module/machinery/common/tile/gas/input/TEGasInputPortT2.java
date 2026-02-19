package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT2 extends TEGasInputPort {

    public TEGasInputPortT2() {
        super(MachineryConfig.getGasPortCapacity(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
