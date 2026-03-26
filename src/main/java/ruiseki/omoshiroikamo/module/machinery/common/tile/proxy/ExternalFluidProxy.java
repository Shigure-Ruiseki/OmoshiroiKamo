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
        IFluidHandler handler = getTargetAs(IFluidHandler.class);
        if (handler == null) return 0;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findFillSide(handler) : from;
        return handler.fill(targetSide, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        IFluidHandler handler = getTargetAs(IFluidHandler.class);
        if (handler == null) return null;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findDrainSide(handler) : from;
        return handler.drain(targetSide, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        IFluidHandler handler = getTargetAs(IFluidHandler.class);
        if (handler == null) return null;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findDrainSide(handler) : from;
        return handler.drain(targetSide, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        IFluidHandler handler = getTargetAs(IFluidHandler.class, true);
        if (handler == null) return false;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findFillSide(handler) : from;
        return handler.canFill(targetSide, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        IFluidHandler handler = getTargetAs(IFluidHandler.class, true);
        if (handler == null) return false;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findDrainSide(handler) : from;
        return handler.canDrain(targetSide, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        IFluidHandler handler = getTargetAs(IFluidHandler.class, true);
        if (handler == null) return new FluidTankInfo[0];

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findFunctionalSide(handler) : from;
        return handler.getTankInfo(targetSide);
    }

    /**
     * Finds a side that can fill fluid.
     */
    private ForgeDirection findFillSide(IFluidHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN && handler.canFill(cachedSide, null)) return cachedSide;
        if (handler.canFill(ForgeDirection.UNKNOWN, null)) {
            cachedSide = ForgeDirection.UNKNOWN;
            return cachedSide;
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (handler.canFill(dir, null)) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    /**
     * Finds a side that can drain fluid.
     */
    private ForgeDirection findDrainSide(IFluidHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN && handler.canDrain(cachedSide, null)) return cachedSide;
        if (handler.canDrain(ForgeDirection.UNKNOWN, null)) {
            cachedSide = ForgeDirection.UNKNOWN;
            return cachedSide;
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (handler.canDrain(dir, null)) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    /**
     * Finds any functional side for status queries.
     */
    private ForgeDirection findFunctionalSide(IFluidHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN) return cachedSide;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            FluidTankInfo[] info = handler.getTankInfo(dir);
            if (info != null && info.length > 0) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }
}
