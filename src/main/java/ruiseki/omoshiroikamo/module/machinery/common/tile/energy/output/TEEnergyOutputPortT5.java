package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT5 extends TEEnergyInputPort {

    public TEEnergyOutputPortT5() {
        super(32768, 8192);
    }

    @Override
    public int getTier() {
        return 5;
    }
}
