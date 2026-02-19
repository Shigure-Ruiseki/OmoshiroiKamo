package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT2 extends TEEnergyInputPort {

    public TEEnergyInputPortT2() {
        super(MachineryConfig.getEnergyPortCapacity(2), MachineryConfig.getEnergyPortTransfer(2));
    }

    @Override
    public int getTier() {
        return 2;
    }
}
