package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface IEnergyPart extends ICablePart, IEnergyNet {

    /**
     * Maximum amount of energy transferable per tick.
     */
    int getTransferLimit();

    /**
     * Tile → part → network (event-based)
     * Called when an external tile actively pushes energy into this part.
     */
    int receiveEnergy(int amount, boolean simulate);

    /**
     * Network → part → tile (event-based)
     * Called when an external tile actively requests energy from this part.
     */
    int extractEnergy(int amount, boolean simulate);
}
