package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT4 extends TEEnergyInputPort {

    public TEEnergyInputPortT4() {
        super(MachineryConfig.getEnergyPortCapacity(4), MachineryConfig.getEnergyPortTransfer(4));
    }

    @Override
    public int getTier() {
        return 4;
    }
}
