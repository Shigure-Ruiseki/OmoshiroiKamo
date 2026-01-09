package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface IEnergyPart extends ICablePart {

    /**
     * Network → part → tile
     * Called when the network wants to send energy to an external tile.
     */
    int pushEnergy(int amount, boolean simulate);

    /**
     * Tile → part → network (tick-based)
     * Called each tick to pull energy from an external tile into the network.
     */
    int pullEnergy(int amount, boolean simulate);

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
