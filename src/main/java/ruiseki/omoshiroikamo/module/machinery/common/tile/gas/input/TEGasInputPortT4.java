package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasInputPortT4 extends TEGasInputPort {

    public TEGasInputPortT4() {
        super(MachineryConfig.getGasPortCapacity(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
