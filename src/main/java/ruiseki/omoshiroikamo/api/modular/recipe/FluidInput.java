package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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
        int found = 0;
        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (!(port instanceof AbstractFluidPortTE fluidPort)) continue;

            FluidStack stored = fluidPort.getStoredFluid();
            if (stored != null && stored.isFluidEqual(required)) {
                found += stored.amount;
            }
        }

        if (found < required.amount) {
            return false;
        }

        if (!simulate) {
            consumeFromPorts(ports);
        }
        return true;
    }

    private void consumeFromPorts(List<IModularPort> ports) {
        int remaining = required.amount;
        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (!(port instanceof AbstractFluidPortTE fluidPort)) continue;

            FluidStack stored = fluidPort.getStoredFluid();
            if (stored != null && stored.isFluidEqual(required)) {
                int drain = Math.min(stored.amount, remaining);
                fluidPort.internalDrain(drain, true);
                remaining -= drain;
            }
            if (remaining <= 0) break;
        }
    }
}
