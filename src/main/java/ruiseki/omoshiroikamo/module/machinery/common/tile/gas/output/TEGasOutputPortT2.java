package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT2 extends TEGasOutputPort {

    public TEGasOutputPortT2() {
        super(MachineryConfig.getGasPortCapacity(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
