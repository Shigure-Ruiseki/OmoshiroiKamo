package ruiseki.omoshiroikamo.common.recipe.chance;

import net.minecraftforge.fluids.FluidStack;

public class ChanceFluidStack {

    public final FluidStack stack;
    public final float chance; // 0.0f to 1.0f

    public ChanceFluidStack(FluidStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }
}
