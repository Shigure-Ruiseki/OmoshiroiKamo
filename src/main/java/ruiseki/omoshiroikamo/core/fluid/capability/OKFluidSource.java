package ruiseki.omoshiroikamo.core.fluid.capability;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class OKFluidSource implements FluidSource {

    private final IFluidHandler source;
    private final ForgeDirection side;

    public OKFluidSource(IFluidHandler source, ForgeDirection side) {
        this.source = source;
        this.side = side;
    }

    @Override
    public FluidStack extract(FluidStack stack, boolean doDrain) {
        if (stack == null || !canExtract(stack.getFluid())) {
            return null;
        }

        return source.drain(side, stack, doDrain);
    }

    @Override
    public boolean canExtract(Fluid fluid) {
        return source.canDrain(side, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo() {
        return source.getTankInfo(side);
    }
}
