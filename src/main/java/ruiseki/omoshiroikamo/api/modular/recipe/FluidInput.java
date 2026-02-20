package ruiseki.omoshiroikamo.api.modular.recipe;

import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.FluidJson;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

public class FluidInput extends AbstractRecipeInput {

    private final FluidStack required;

    public FluidInput(FluidStack required) {
        this.required = required.copy();
    }

    public FluidStack getRequired() {
        return required.copy();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    protected long getRequiredAmount() {
        return required.amount;
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

    public static FluidInput fromJson(JsonObject json) {
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
        return new FluidInput(stack);
    }
}
