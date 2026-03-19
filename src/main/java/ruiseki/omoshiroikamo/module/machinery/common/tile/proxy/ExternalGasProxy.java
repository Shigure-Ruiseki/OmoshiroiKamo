package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.ITubeConnection;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.gas.GasTankInfo;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * External Gas Port Proxy.
 * Adapts an external gas handler (Mekanism) to be used as a modular port.
 *
 * Design Pattern: Adapter Pattern
 * - Implements IGasHandler and ITubeConnection by delegating to the target TileEntity
 * - Uses AbstractExternalProxy for common proxy functionality
 */
public class ExternalGasProxy extends AbstractExternalProxy implements IGasHandler, ITubeConnection {

    public ExternalGasProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.GAS;
    }

    // ========== IGasHandler Implementation (Delegated) ==========

    @Override
    public int receiveGas(ForgeDirection from, GasStack stack, boolean doTransfer) {
        IGasHandler handler = getTargetAs(IGasHandler.class);
        if (handler == null) return 0;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findReceiveSide(handler) : from;
        return handler.receiveGas(targetSide, stack, doTransfer);
    }

    @Override
    public GasStack drawGas(ForgeDirection from, int amount, boolean doDraw) {
        IGasHandler handler = getTargetAs(IGasHandler.class);
        if (handler == null) return null;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findDrawSide(handler) : from;
        return handler.drawGas(targetSide, amount, doDraw);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection from, Gas gas) {
        IGasHandler handler = getTargetAs(IGasHandler.class, true);
        if (handler == null) return false;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findReceiveSide(handler) : from;
        return handler.canReceiveGas(targetSide, gas);
    }

    @Override
    public boolean canDrawGas(ForgeDirection from, Gas gas) {
        IGasHandler handler = getTargetAs(IGasHandler.class, true);
        if (handler == null) return false;

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findDrawSide(handler) : from;
        return handler.canDrawGas(targetSide, gas);
    }

    @Override
    public GasTankInfo[] getTankInfo(ForgeDirection from) {
        IGasHandler handler = getTargetAs(IGasHandler.class, true);
        if (handler == null) return new GasTankInfo[0];

        ForgeDirection targetSide = (from == ForgeDirection.UNKNOWN) ? findFunctionalSide(handler) : from;
        return handler.getTankInfo(targetSide);
    }

    /**
     * Finds a side that can receive gas.
     */
    private ForgeDirection findReceiveSide(IGasHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN && handler.canReceiveGas(cachedSide, null)) return cachedSide;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (handler.canReceiveGas(dir, null)) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    /**
     * Finds a side that can draw gas.
     */
    private ForgeDirection findDrawSide(IGasHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN && handler.canDrawGas(cachedSide, null)) return cachedSide;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (handler.canDrawGas(dir, null)) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    /**
     * Finds any functional side for status queries.
     */
    private ForgeDirection findFunctionalSide(IGasHandler handler) {
        if (cachedSide != ForgeDirection.UNKNOWN) return cachedSide;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            GasTankInfo[] info = handler.getTankInfo(dir);
            if (info != null && info.length > 0) {
                cachedSide = dir;
                return cachedSide;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    // ========== ITubeConnection Implementation (Delegated) ==========

    @Override
    public boolean canTubeConnect(ForgeDirection side) {
        return delegate(ITubeConnection.class, tube -> tube.canTubeConnect(side), false);
    }
}
