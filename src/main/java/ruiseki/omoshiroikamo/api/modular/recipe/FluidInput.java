package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

/**
 * Recipe input requirement for fluids.
 */
public class FluidInput implements IRecipeInput {

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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = required.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractFluidPortTE)) {
                throw new IllegalStateException(
                    "FLUID INPUT port must be AbstractFluidPortTE, got: " + port.getClass()
                        .getName());
            }
            AbstractFluidPortTE fluidPort = (AbstractFluidPortTE) port;

            FluidStack stored = fluidPort.getStoredFluid();
            if (stored != null && stored.isFluidEqual(required)) {
                int drain = Math.min(stored.amount, remaining);
                if (!simulate) {
                    fluidPort.internalDrain(drain, true);
                }
                remaining -= drain;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Create FluidInput from JSON.
     * Format: { "fluid": "water", "amount": 10000 }
     */
    public static FluidInput fromJson(JsonObject json) {
        String fluidName = json.get("fluid")
            .getAsString();
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 0;

        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            Logger.warn("Unknown fluid in recipe: {}", fluidName);
            return null;
        }
        return new FluidInput(new FluidStack(fluid, amount));
    }
}
