package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

/**
 * Recipe output for fluids.
 */
public class FluidOutput implements IRecipeOutput {

    private final FluidStack output;

    public FluidOutput(FluidStack output) {
        this.output = output.copy();
    }

    public FluidStack getOutput() {
        return output.copy();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = output.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID)
                continue;
            if (!(port instanceof AbstractFluidPortTE fluidPort))
                continue;

            FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo == null || tankInfo.length == 0)
                continue;

            int tankCapacity = tankInfo[0].capacity;
            FluidStack stored = fluidPort.getStoredFluid();
            if (stored == null || stored.isFluidEqual(output)) {
                int capacity = tankCapacity - (stored != null ? stored.amount : 0);
                int fill = Math.min(remaining, capacity);
                if (!simulate) {
                    FluidStack toFill = output.copy();
                    toFill.amount = fill;
                    fluidPort.internalFill(toFill, true);
                }
                remaining -= fill;
            }
            if (remaining <= 0)
                break;
        }

        return remaining <= 0;
    }
}
