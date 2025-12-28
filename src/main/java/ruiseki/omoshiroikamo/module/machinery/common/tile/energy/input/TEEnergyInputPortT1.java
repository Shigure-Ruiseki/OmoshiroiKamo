package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT1 extends TEEnergyInputPort {

    public TEEnergyInputPortT1() {
        super(2048, 128);
    }

    @Override
    public int getTier() {
        return 1;
    }
}
