package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

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
    public void apply(List<IModularPort> ports, int multiplier) {
        FluidStack output = FluidRegistry.getFluidStack(fluidName, amount * multiplier);
        if (output == null) return;

        int remaining = output.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof IFluidHandler)) continue;

            IFluidHandler fluidPort = (IFluidHandler) port;
            FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo == null || tankInfo.length == 0) continue;

            int tankCapacity = tankInfo[0].capacity;
            FluidStack stored = tankInfo[0].fluid;
            int currentAmount = stored != null ? stored.amount : 0;
            int space = tankCapacity - currentAmount;

            if (stored == null || stored.isFluidEqual(output)) {
                int fillAmount = Math.min(remaining, space);
                if (fillAmount > 0) {
                    FluidStack toFill = output.copy();
                    toFill.amount = fillAmount;
                    fluidPort.fill(ForgeDirection.UNKNOWN, toFill, true);
                    remaining -= fillAmount;
                }
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.FLUID && port instanceof IFluidHandler;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (!(port instanceof IFluidHandler)) return 0;
        IFluidHandler fluidPort = (IFluidHandler) port;
        FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
        FluidStack output = getOutput();

        if (tankInfo != null && tankInfo.length > 0) {
            long totalAvailable = 0;
            for (FluidTankInfo info : tankInfo) {
                FluidStack stored = info.fluid;
                if (stored == null || output == null || stored.isFluidEqual(output)) {
                    int currentAmount = stored != null ? stored.amount : 0;
                    int space = info.capacity - currentAmount;
                    if (space > 0) totalAvailable += space;
                }
            }
            return totalAvailable;
        }
        return 0;
    }

    @Override
    public long getRequiredAmount() {
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
        return output;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        return new FluidOutput(fluidName, amount * multiplier);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "fluid");
        nbt.setString("fluid", fluidName);
        nbt.setInteger("amount", amount);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.fluidName = nbt.getString("fluid");
        this.amount = nbt.getInteger("amount");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
