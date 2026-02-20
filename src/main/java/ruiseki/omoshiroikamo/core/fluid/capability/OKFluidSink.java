package ruiseki.omoshiroikamo.core.fluid.capability;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class OKFluidSink implements FluidSink {

    private final IFluidHandler sink;
    private final ForgeDirection side;

    public OKFluidSink(IFluidHandler sink, ForgeDirection side) {
        this.sink = sink;
        this.side = side;
    }

    @Override
    public int insert(FluidStack stack, boolean doFill) {
        if (!canInsert(stack.getFluid())) {
            return 0;
        }
        return sink.fill(side, stack, doFill);
    }

    @Override
    public boolean canInsert(Fluid fluid) {
        return sink.canFill(side, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo() {
        return sink.getTankInfo(side);
    }
}
