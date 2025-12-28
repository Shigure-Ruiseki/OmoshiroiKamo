package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT4 extends TEEnergyInputPort {

    public TEEnergyInputPortT4() {
        super(16384, 2048);
    }

    @Override
    public int getTier() {
        return 4;
    }
}
