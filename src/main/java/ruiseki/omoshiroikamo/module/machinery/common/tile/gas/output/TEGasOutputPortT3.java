package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT3 extends TEGasOutputPort {

    public TEGasOutputPortT3() {
        super(MachineryConfig.getGasPortCapacity(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
