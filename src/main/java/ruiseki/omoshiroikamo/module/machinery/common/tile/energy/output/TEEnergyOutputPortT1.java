package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT1 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT1() {
        super(MachineryConfig.getEnergyPortCapacity(1), MachineryConfig.getEnergyPortTransfer(1));
    }

    @Override
    public int getTier() {
        return 1;
    }
}
