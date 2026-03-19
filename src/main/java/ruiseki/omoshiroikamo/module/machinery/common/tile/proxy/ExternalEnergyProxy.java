package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.energy.IOKEnergyIO;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;
import ruiseki.omoshiroikamo.core.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIntegrationRegistry;
import ruiseki.omoshiroikamo.core.energy.capability.IEnergyIntegrationDelegate;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * External Energy Port Proxy.
 * Adapts an external energy tile to be used as a modular port.
 * Supports IOKEnergy, CoFH RF API, and EnderIO (lazy-loaded).
 *
 * Design Pattern: Adapter Pattern
 * - Implements IOKEnergyIO (which combines IOKEnergySink and IOKEnergySource)
 * - Uses AbstractExternalProxy for common proxy functionality
 * - Lazy-loads EnderIO integration only when the mod is present
 */
public class ExternalEnergyProxy extends AbstractExternalProxy implements IOKEnergyIO {

    public ExternalEnergyProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ENERGY;
    }

    // ========== IOKEnergyTile Implementation (Base) ==========
    @Override
    public int getEnergyStored() {
        // Try IOKEnergy first
        Integer okStored = delegate(IOKEnergyTile.class, IOKEnergyTile::getEnergyStored, null, true);
        if (okStored != null) return okStored;

        // Try registered energy integration delegates (EnderIO, etc.)
        Object te = getTargetTileEntity();
        for (IEnergyIntegrationDelegate integration : EnergyIntegrationRegistry.getDelegates()) {
            Integer result = integration.getEnergyStored(te);
            if (result != null) return result;
        }

        // Try CoFH RF API
        IEnergyConnection connection = getTargetAs(IEnergyConnection.class, true);
        if (connection != null) {
            ForgeDirection side = findConnectableSide(connection);
            if (connection instanceof IEnergyHandler IEH) return IEH.getEnergyStored(side);
            if (connection instanceof IEnergyReceiver IER) return IER.getEnergyStored(side);
            if (connection instanceof IEnergyProvider IEP) return IEP.getEnergyStored(side);
        }

        notifyError();
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        // Try IOKEnergy first
        Integer okMax = delegate(IOKEnergyTile.class, IOKEnergyTile::getMaxEnergyStored, null, true);
        if (okMax != null) return okMax;

        // Try registered energy integration delegates (EnderIO, etc.)
        Object te = getTargetTileEntity();
        for (IEnergyIntegrationDelegate integration : EnergyIntegrationRegistry.getDelegates()) {
            Integer result = integration.getMaxEnergyStored(te);
            if (result != null) return result;
        }

        // Try CoFH RF API
        IEnergyConnection connection = getTargetAs(IEnergyConnection.class, true);
        if (connection != null) {
            ForgeDirection side = findConnectableSide(connection);
            if (connection instanceof IEnergyHandler IEH) return IEH.getMaxEnergyStored(side);
            if (connection instanceof IEnergyReceiver IER) return IER.getMaxEnergyStored(side);
            if (connection instanceof IEnergyProvider IEP) return IEP.getMaxEnergyStored(side);
        }

        notifyError();
        return 0;
    }

    @Override
    public void setEnergyStored(int stored) {
        delegateVoid(IOKEnergyTile.class, tile -> tile.setEnergyStored(stored), true);
    }

    @Override
    public int getEnergyTransfer() {
        return delegate(IOKEnergyTile.class, IOKEnergyTile::getEnergyTransfer, 0, true);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection side) {
        Boolean okConnect = delegate(IOKEnergyTile.class, tile -> tile.canConnectEnergy(side), null, true);
        if (okConnect != null) return okConnect;

        return delegate(IEnergyConnection.class, h -> h.canConnectEnergy(side), false, true);
    }

    // ========== IOKEnergySink Implementation (Receive) ==========

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        // Try IOKEnergySink first
        Integer okReceived = delegate(
            IOKEnergySink.class,
            sink -> sink.receiveEnergy(side, amount, simulate),
            null,
            true);
        if (okReceived != null) return okReceived;

        // Try registered energy integration delegates (EnderIO, etc.)
        Object te = getTargetTileEntity();
        for (IEnergyIntegrationDelegate integration : EnergyIntegrationRegistry.getDelegates()) {
            Integer result = integration.tryReceive(te, side, amount, simulate);
            if (result != null) return result;
        }

        // Try CoFH RF API
        IEnergyReceiver receiver = getTargetAs(IEnergyReceiver.class, true);
        if (receiver != null) {
            ForgeDirection targetSide = (side == ForgeDirection.UNKNOWN) ? findReceiveSide(receiver) : side;
            return receiver.receiveEnergy(targetSide, amount, simulate);
        }

        notifyError();
        return 0;
    }

    // ========== IOKEnergySource Implementation (Extract) ==========

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        // Try IOKEnergySource first
        Integer okExtracted = delegate(
            IOKEnergySource.class,
            source -> source.extractEnergy(side, amount, simulate),
            null,
            true);
        if (okExtracted != null) return okExtracted;

        // Try registered energy integration delegates (EnderIO, etc.)
        Object te = getTargetTileEntity();
        for (IEnergyIntegrationDelegate integration : EnergyIntegrationRegistry.getDelegates()) {
            Integer result = integration.tryExtract(te, side, amount, simulate);
            if (result != null) return result;
        }

        // Try CoFH RF API
        IEnergyProvider provider = getTargetAs(IEnergyProvider.class, true);
        if (provider != null) {
            ForgeDirection targetSide = (side == ForgeDirection.UNKNOWN) ? findExtractSide(provider) : side;
            return provider.extractEnergy(targetSide, amount, simulate);
        }

        notifyError();
        return 0;
    }

    /**
     * Finds a side that can connect for status queries.
     * Uses cached side if available, otherwise probes all 6 sides.
     */
    private ForgeDirection findConnectableSide(IEnergyConnection handler) {
        if (cachedSide != ForgeDirection.UNKNOWN && handler.canConnectEnergy(cachedSide)) {
            return cachedSide;
        }

        if (handler.canConnectEnergy(ForgeDirection.UNKNOWN)) {
            cachedSide = ForgeDirection.UNKNOWN;
            return cachedSide;
        }

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (handler.canConnectEnergy(dir)) {
                cachedSide = dir;
                return cachedSide;
            }
        }

        return ForgeDirection.UNKNOWN;
    }

    /**
     * Find a side that can receive energy.
     * Tests actual receiveEnergy capability with simulation.
     * This is critical for EnderIO CapBank which has directional input/output faces.
     */
    private ForgeDirection findReceiveSide(IEnergyReceiver receiver) {
        // Check cached side first (with actual transfer test)
        if (cachedSide != ForgeDirection.UNKNOWN) {
            if (receiver.canConnectEnergy(cachedSide) && receiver.receiveEnergy(cachedSide, 1, true) > 0) {
                return cachedSide;
            }
        }

        // Try UNKNOWN (omni-directional)
        if (receiver.canConnectEnergy(ForgeDirection.UNKNOWN)
            && receiver.receiveEnergy(ForgeDirection.UNKNOWN, 1, true) > 0) {
            cachedSide = ForgeDirection.UNKNOWN;
            return cachedSide;
        }

        // Probe all 6 sides
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (receiver.canConnectEnergy(dir) && receiver.receiveEnergy(dir, 1, true) > 0) {
                cachedSide = dir;
                return cachedSide;
            }
        }

        return ForgeDirection.UNKNOWN;
    }

    /**
     * Find a side that can extract energy.
     * Tests actual extractEnergy capability with simulation.
     * This is critical for EnderIO CapBank which has directional input/output faces.
     */
    private ForgeDirection findExtractSide(IEnergyProvider provider) {
        // Check cached side first (with actual transfer test)
        if (cachedSide != ForgeDirection.UNKNOWN) {
            if (provider.canConnectEnergy(cachedSide) && provider.extractEnergy(cachedSide, 1, true) > 0) {
                return cachedSide;
            }
        }

        // Try UNKNOWN (omni-directional)
        if (provider.canConnectEnergy(ForgeDirection.UNKNOWN)
            && provider.extractEnergy(ForgeDirection.UNKNOWN, 1, true) > 0) {
            cachedSide = ForgeDirection.UNKNOWN;
            return cachedSide;
        }

        // Probe all 6 sides
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (provider.canConnectEnergy(dir) && provider.extractEnergy(dir, 1, true) > 0) {
                cachedSide = dir;
                return cachedSide;
            }
        }

        return ForgeDirection.UNKNOWN;
    }
}
