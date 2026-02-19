package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT3 extends TEGasInputPort {

    public TEGasInputPortT3() {
        super(MachineryConfig.getGasPortCapacity(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
