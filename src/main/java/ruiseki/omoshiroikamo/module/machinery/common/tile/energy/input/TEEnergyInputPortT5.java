package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPortT5 extends TEEnergyInputPort {

    public TEEnergyInputPortT5() {
        super(32768, 8192);
    }

    @Override
    public int getTier() {
        return 5;
    }
}
