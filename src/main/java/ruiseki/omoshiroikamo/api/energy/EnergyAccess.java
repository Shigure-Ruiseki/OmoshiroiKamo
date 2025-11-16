package ruiseki.omoshiroikamo.api.energy;

import javax.annotation.Nonnegative;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Provides access to energy sources/sinks.
 * Should not be stored; intended for single discrete operations.
 */
public interface EnergyAccess {

    EnergyAccess EMPTY = new EmptyEnergyAccess();

    /**
     * Extracts energy from the current source.
     *
     * @param side     The side of the block
     * @param amount   The amount to extract
     * @param simulate If true, only simulate the extraction
     * @return The amount actually extracted
     */
    @Nonnegative
    int extract(ForgeDirection side, int amount, boolean simulate);

    /**
     * Inserts energy into the current sink.
     *
     * @param side     The side of the block
     * @param amount   The amount to insert
     * @param simulate If true, only simulate the insertion
     * @return The amount of energy that could not be inserted
     */
    @Nonnegative
    int insert(ForgeDirection side, int amount, boolean simulate);

    @Nonnegative
    boolean canConnectEnergy(ForgeDirection side);

    /**
     * Empty implementation for convenience.
     */
    class EmptyEnergyAccess implements EnergyAccess {

        @Override
        public int extract(ForgeDirection side, int amount, boolean simulate) {
            return 0;
        }

        @Override
        public int insert(ForgeDirection side, int amount, boolean simulate) {
            return amount;
        }

        @Override
        public boolean canConnectEnergy(ForgeDirection var1) {
            return true;
        }
    }
}
