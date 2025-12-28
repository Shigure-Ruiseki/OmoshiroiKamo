package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT3 extends TEEnergyInputPort {

    public TEEnergyOutputPortT3() {
        super(8192, 512);
    }

    @Override
    public int getTier() {
        return 3;
    }
}
