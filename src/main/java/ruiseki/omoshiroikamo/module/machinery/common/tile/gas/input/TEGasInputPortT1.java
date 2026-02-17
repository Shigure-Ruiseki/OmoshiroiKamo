package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT1 extends TEGasInputPort {

    public TEGasInputPortT1() {
        super(MachineryConfig.getGasPortCapacity(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
