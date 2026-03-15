package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * External Fluid Port Proxy.
 * Adapts an external IFluidHandler (like tanks) to be used as a modular port.
 *
 * Design Pattern: Adapter Pattern
 * - Implements IFluidHandler by delegating all calls to the target TileEntity
 * - Uses AbstractExternalProxy for common proxy functionality
 */
public class ExternalFluidProxy extends AbstractExternalProxy implements IFluidHandler {

    public ExternalFluidProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    // ========== IFluidHandler Implementation (Delegated) ==========

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return delegate(IFluidHandler.class, h -> h.fill(from, resource, doFill), 0);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return delegate(IFluidHandler.class, h -> h.drain(from, resource, doDrain), null);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return delegate(IFluidHandler.class, h -> h.drain(from, maxDrain, doDrain), null);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return delegate(IFluidHandler.class, h -> h.canFill(from, fluid), false);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return delegate(IFluidHandler.class, h -> h.canDrain(from, fluid), false);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return delegate(IFluidHandler.class, h -> h.getTankInfo(from), new FluidTankInfo[0]);
    }
}
