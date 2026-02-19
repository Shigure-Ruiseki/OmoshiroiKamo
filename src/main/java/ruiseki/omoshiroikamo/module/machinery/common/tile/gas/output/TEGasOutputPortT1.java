package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT1 extends TEGasOutputPort {

    public TEGasOutputPortT1() {
        super(MachineryConfig.getGasPortCapacity(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
