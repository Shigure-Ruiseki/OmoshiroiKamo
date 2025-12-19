package ruiseki.omoshiroikamo.api.json;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidJson {

    public String name;
    public int amount;

    public static FluidStack resolveFluidStack(FluidJson data) {
        if (data == null || data.name == null) return null;
        if (FluidRegistry.isFluidRegistered(data.name)) {
            return new FluidStack(FluidRegistry.getFluid(data.name), data.amount > 0 ? data.amount : 1000);
        }
        return null;
    }

    public static FluidJson parseFluidStack(FluidStack stack) {
        FluidJson json = new FluidJson();
        json.name = stack.getFluid()
            .getName();
        json.amount = stack.amount;
        return json;
    }
}
