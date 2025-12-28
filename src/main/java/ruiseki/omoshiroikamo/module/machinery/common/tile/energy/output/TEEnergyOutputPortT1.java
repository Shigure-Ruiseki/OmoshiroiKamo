package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyOutputPortT1 extends TEEnergyInputPort {

    public TEEnergyOutputPortT1() {
        super(2048, 128);
    }

    @Override
    public int getTier() {
        return 1;
    }
}
