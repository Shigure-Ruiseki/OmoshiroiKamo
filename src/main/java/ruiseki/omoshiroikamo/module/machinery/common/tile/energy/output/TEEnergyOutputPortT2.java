package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT2 extends TEEnergyInputPort {

    public TEEnergyOutputPortT2() {
        super(4096, 512);
    }

    @Override
    public int getTier() {
        return 2;
    }
}
