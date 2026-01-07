package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface IEnergyPart extends ICablePart {

    /**
     * Network → part → tile
     */
    int pushEnergy(int amount, boolean simulate);

    /**
     * Tile → part → network
     */
    int pullEnergy(int amount, boolean simulate);

    /**
     * per tick
     */
    int getTransferLimit();
}
