package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT6 extends TEGasInputPort {

    public TEGasInputPortT6() {
        super(MachineryConfig.getGasPortCapacity(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
