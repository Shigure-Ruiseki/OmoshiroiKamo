package ruiseki.omoshiroikamo.api.fluid.capability;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public interface FluidSink {

    int insert(FluidStack stack, boolean doFill);

    boolean canInsert(Fluid fluid);

    FluidTankInfo[] getTankInfo();
}
