package ruiseki.omoshiroikamo.module.machinery.common.tile;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractEnergyTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 * Extends AbstractEnergyTE to leverage existing energy management system.
 */
public class TEEnergyInputPort extends AbstractEnergyTE {

    private static final int DEFAULT_CAPACITY = 100000;
    private static final int DEFAULT_MAX_RECEIVE = 10000;

    public TEEnergyInputPort() {
        super(DEFAULT_CAPACITY, DEFAULT_MAX_RECEIVE);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return super.processTasks(redstoneCheckPassed);
    }

    /**
     * Extract energy for machine processing.
     *
     * @param amount requested amount
     * @return amount actually extracted
     */
    public int extractEnergy(int amount) {
        int extracted = Math.min(energyStorage.getEnergyStored(), amount);
        energyStorage.voidEnergy(extracted);
        return extracted;
    }
}
