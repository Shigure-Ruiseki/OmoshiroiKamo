package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT5 extends TEGasOutputPort {

    public TEGasOutputPortT5() {
        super(MachineryConfig.getGasPortCapacity(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
