package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.FluidJson;

public class FluidInput extends AbstractRecipeInput {

    private FluidStack required;
    private int count = 0;

    public FluidInput(FluidStack required) {
        this.required = required != null ? required.copy() : null;
        if (this.required != null) this.count = this.required.amount;
    }

    public FluidStack getRequired() {
        return required != null ? required.copy() : null;
    }

    public FluidStack getFluid() {
        return getRequired();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public long getRequiredAmount() {
        return required != null ? required.amount : count;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.FLUID && port instanceof IFluidHandler;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        IFluidHandler fluidPort = (IFluidHandler) port;
        FluidStack stored = null;

        FluidTankInfo[] infos = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
        if (infos != null && infos.length > 0) {
            stored = infos[0].fluid;
        }

        if (stored != null && stored.isFluidEqual(required)) {
            int drainAmount = (int) Math.min(stored.amount, remaining);
            FluidStack drained = fluidPort
                .drain(ForgeDirection.UNKNOWN, new FluidStack(required, drainAmount), !simulate);
            return drained != null ? drained.amount : 0;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        FluidJson fluidJson = new FluidJson();
        fluidJson.name = json.get("fluid")
            .getAsString();
        fluidJson.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
        this.count = fluidJson.amount;
        this.required = FluidJson.resolveFluidStack(fluidJson);
    }

    @Override
    public void write(JsonObject json) {
        if (!consume) json.addProperty("consume", false);

        if (required != null && required.getFluid() != null) {
            json.addProperty(
                "fluid",
                required.getFluid()
                    .getName());
            json.addProperty("amount", required.amount);
        }
    }

    @Override
    public boolean validate() {
        return required != null;
    }

    public static FluidInput fromJson(JsonObject json) {
        FluidInput input = new FluidInput(null);
        input.read(json);
        return input;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
