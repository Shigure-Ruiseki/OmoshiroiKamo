package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

public class TEGasOutputPortT4 extends TEGasOutputPort {

    public TEGasOutputPortT4() {
        super(MachineryConfig.getGasPortCapacity(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
