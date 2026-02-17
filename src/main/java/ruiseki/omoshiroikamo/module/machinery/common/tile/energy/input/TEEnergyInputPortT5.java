package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT5 extends TEEnergyInputPort {

    public TEEnergyInputPortT5() {
        super(MachineryConfig.getEnergyPortCapacity(5), MachineryConfig.getEnergyPortTransfer(5));
    }

    @Override
    public int getTier() {
        return 5;
    }
}
