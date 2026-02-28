package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

public class FluidOutput extends AbstractRecipeOutput {

    private String fluidName;
    private int amount;

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

    public FluidStack getFluid() {
        return getOutput();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public void apply(List<IModularPort> ports) {
        FluidStack output = FluidRegistry.getFluidStack(fluidName, amount);
        if (output == null) return;

        int remaining = output.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof AbstractFluidPortTE)) continue;

            AbstractFluidPortTE fluidPort = (AbstractFluidPortTE) port;
            FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo == null || tankInfo.length == 0) continue;

            int tankCapacity = tankInfo[0].capacity;
            FluidStack stored = fluidPort.getStoredFluid();
            int currentAmount = stored != null ? stored.amount : 0;
            int space = tankCapacity - currentAmount;

            if (stored == null || stored.isFluidEqual(output)) {
                int fill = Math.min(remaining, space);
                if (fill > 0) {
                    FluidStack toFill = output.copy();
                    toFill.amount = fill;
                    fluidPort.internalFill(toFill, true);
                    remaining -= fill;
                }
            }
            if (remaining <= 0) break;
        }
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

    @Override
    public void read(JsonObject json) {
        this.fluidName = json.get("fluid")
            .getAsString();
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("fluid", fluidName);
        json.addProperty("amount", amount);
    }

    @Override
    public boolean validate() {
        return fluidName != null && !fluidName.isEmpty() && amount > 0;
    }

    public static FluidOutput fromJson(JsonObject json) {
        FluidOutput output = new FluidOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
