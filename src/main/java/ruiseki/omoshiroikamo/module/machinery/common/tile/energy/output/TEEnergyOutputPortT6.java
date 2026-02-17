package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT6 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT6() {
        super(MachineryConfig.getEnergyPortCapacity(6), MachineryConfig.getEnergyPortTransfer(6));
    }

    @Override
    public int getTier() {
        return 6;
    }
}
