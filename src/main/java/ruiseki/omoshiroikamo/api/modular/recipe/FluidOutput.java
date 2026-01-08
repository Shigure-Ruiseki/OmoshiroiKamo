package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.json.FluidJson;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
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
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractFluidPortTE)) {
                throw new IllegalStateException(
                    "FLUID OUTPUT port must be AbstractFluidPortTE, got: " + port.getClass()
                        .getName());
            }
            AbstractFluidPortTE fluidPort = (AbstractFluidPortTE) port;

            FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo == null || tankInfo.length == 0) continue;

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
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Create FluidOutput from JSON.
     */
    public static FluidOutput fromJson(JsonObject json) {
        FluidJson fluidJson = new FluidJson();
        fluidJson.name = json.get("fluid")
            .getAsString();
        fluidJson.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;

        FluidStack stack = FluidJson.resolveFluidStack(fluidJson);
        if (stack == null) {
            Logger.warn("Unknown fluid in recipe: {}", fluidJson.name);
            return null;
        }
        return new FluidOutput(stack);
    }
}
