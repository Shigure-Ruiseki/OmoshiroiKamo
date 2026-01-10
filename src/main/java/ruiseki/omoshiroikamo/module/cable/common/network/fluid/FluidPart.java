package ruiseki.omoshiroikamo.module.cable.common.network.fluid;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public interface FluidPart extends ICablePart {

    /**
     * Network → part → tile
     * Called when the network wants to send energy to an external tile.
     */
    int pushFluid(FluidStack stack, boolean doFill);

    /**
     * Tile → part → network (tick-based)
     * Called each tick to pull fluid from an external tile into the network.
     */
    int pullFluid(FluidStack stack, boolean doFill);

    /**
     * Maximum amount of fluid transferable per tick.
     */
    int getTransferLimit();

    /**
     * Tile → part → network (event-based)
     * Called when an external tile actively pushes fluid into this part.
     */
    int receiveFluid(FluidStack stack, boolean doFill);

    /**
     * Network → part → tile (event-based)
     * Called when an external tile actively requests fluid from this part.
     */
    int extractFluid(FluidStack stack, boolean doFill);
}
