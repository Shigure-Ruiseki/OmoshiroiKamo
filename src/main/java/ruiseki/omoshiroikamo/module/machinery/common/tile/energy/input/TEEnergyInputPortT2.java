package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT2 extends TEEnergyInputPort {

    public TEEnergyInputPortT2() {
        super(4096, 512);
    }

    @Override
    public int getTier() {
        return 2;
    }
}
