package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT6 extends TEGasOutputPort {

    public TEGasOutputPortT6() {
        super(MachineryConfig.getGasPortCapacity(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
