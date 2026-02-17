package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT5 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT5() {
        super(MachineryConfig.getEnergyPortCapacity(5), MachineryConfig.getEnergyPortTransfer(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
