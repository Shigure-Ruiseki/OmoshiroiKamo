package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT6 extends TEEnergyInputPort {

    public TEEnergyInputPortT6() {
        super(131072, 32768);
    }

    @Override
    public int getTier() {
        return 6;
    }
}
