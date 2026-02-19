package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT5 extends TEGasInputPort {

    public TEGasInputPortT5() {
        super(MachineryConfig.getGasPortCapacity(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
