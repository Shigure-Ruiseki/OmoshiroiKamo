package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT2 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT2() {
        super(MachineryConfig.getEnergyPortCapacity(2), MachineryConfig.getEnergyPortTransfer(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
