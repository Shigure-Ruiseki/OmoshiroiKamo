package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.json.FluidJson;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

public class FluidOutput extends AbstractRecipeOutput {

    private final String fluidName;
    private final int amount;

    public FluidOutput(String fluidName, int amount) {
        this.fluidName = fluidName;
        this.amount = amount;
    }

    public String getFluidName() {
        return fluidName;
    }

    public int getAmount() {
        return amount;
    }

    public FluidStack getOutput() {
        return FluidRegistry.getFluidStack(fluidName, amount);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {

        // If not simulating, first check if we CAN output everything by simulating
        if (!simulate) {
            if (!process(ports, true)) return false;
        }

        FluidStack output = FluidRegistry.getFluidStack(fluidName, amount);
        if (output == null) return false;

        int remaining = output.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;
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
            int currentAmount = stored != null ? stored.amount : 0;
            int space = tankCapacity - currentAmount;

            // Only output if compatible fluid or empty
            if (stored == null || stored.isFluidEqual(output)) {
                int fill = Math.min(remaining, space);
                if (fill > 0) {
                    if (!simulate) {
                        FluidStack toFill = output.copy();
                        toFill.amount = fill;
                        fluidPort.internalFill(toFill, true);
                    }
                    remaining -= fill;
                }
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.FLUID && port instanceof AbstractFluidPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractFluidPortTE fluidPort = (AbstractFluidPortTE) port;
        FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
        if (tankInfo != null && tankInfo.length > 0) {
            long total = 0;
            for (FluidTankInfo info : tankInfo) {
                total += info.capacity;
            }
            return total;
        }
        return 0;
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
    }

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
        return new FluidOutput(
            stack.getFluid()
                .getName(),
            stack.amount);
    }
}
