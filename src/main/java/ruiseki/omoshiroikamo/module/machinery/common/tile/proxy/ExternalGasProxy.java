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
        return delegate(IGasHandler.class, h -> h.receiveGas(from, stack, doTransfer), 0);
    }

    @Override
    public GasStack drawGas(ForgeDirection from, int amount, boolean doDraw) {
        return delegate(IGasHandler.class, h -> h.drawGas(from, amount, doDraw), null);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection from, Gas gas) {
        return delegate(IGasHandler.class, h -> h.canReceiveGas(from, gas), false);
    }

    @Override
    public boolean canDrawGas(ForgeDirection from, Gas gas) {
        return delegate(IGasHandler.class, h -> h.canDrawGas(from, gas), false);
    }

    @Override
    public GasTankInfo[] getTankInfo(ForgeDirection from) {
        return delegate(IGasHandler.class, h -> h.getTankInfo(from), new GasTankInfo[0]);
    }

    // ========== ITubeConnection Implementation (Delegated) ==========

    @Override
    public boolean canTubeConnect(ForgeDirection side) {
        return delegate(ITubeConnection.class, tube -> tube.canTubeConnect(side), false);
    }
}
