package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT3 extends TEEnergyOutputPort {

    public TEEnergyOutputPortT3() {
        super(MachineryConfig.getEnergyPortCapacity(3), MachineryConfig.getEnergyPortTransfer(3));
    }

    @Override
    public int getTier() {
        return 3;
    }
}
