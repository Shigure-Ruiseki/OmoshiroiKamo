package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT4 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT4() {
        super(MachineryConfig.getEnergyPortCapacity(4), MachineryConfig.getEnergyPortTransfer(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
