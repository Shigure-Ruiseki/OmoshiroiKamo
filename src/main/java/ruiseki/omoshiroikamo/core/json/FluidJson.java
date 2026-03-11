package ruiseki.omoshiroikamo.core.json;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidJson {

    public String name;
    public int amount;

    public static FluidStack resolveFluidStack(FluidJson data) {
        if (data == null || data.name == null) return null;
        try {
            if (FluidRegistry.isFluidRegistered(data.name)) {
                return new FluidStack(FluidRegistry.getFluid(data.name), data.amount > 0 ? data.amount : 1000);
            }
        } catch (Throwable t) {
            // fallback for test environment
            return null;
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

    /**
     * Parse from string like "name,amount"
     */
    public static FluidJson parseFluidString(String string) {
        if (string == null || string.trim()
            .isEmpty()) return null;

        String[] parts = string.split(",");
        if (parts.length == 0) return null;

        FluidJson json = new FluidJson();
        json.name = parts[0].trim();

        if (parts.length > 1) {
            try {
                json.amount = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                json.amount = 1000;
            }
        } else {
            json.amount = 1000;
        }

        return json;
    }
}
