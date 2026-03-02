package ruiseki.omoshiroikamo.api.modular.recipe.io;

import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.FluidJson;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

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
        return port instanceof AbstractFluidPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractFluidPortTE fluidPort = (AbstractFluidPortTE) port;
        FluidStack stored = fluidPort.getStoredFluid();
        if (stored != null && stored.isFluidEqual(required)) {
            int drain = (int) Math.min(stored.amount, remaining);
            if (!simulate) {
                fluidPort.internalDrain(drain, true);
            }
            return drain;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
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
