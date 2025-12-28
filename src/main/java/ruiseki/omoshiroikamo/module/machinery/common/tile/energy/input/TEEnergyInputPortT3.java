package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT3 extends TEEnergyInputPort {

    public TEEnergyInputPortT3() {
        super(8192, 512);
    }

    @Override
    public int getTier() {
        return 3;
    }
}
