package ruiseki.omoshiroikamo.api.fluid.capability;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public interface FluidSource {

    FluidStack extract(FluidStack stack, boolean doDrain);

    boolean canExtract(Fluid fluid);

    FluidTankInfo[] getTankInfo();
}
