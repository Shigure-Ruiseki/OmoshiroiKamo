package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.energy.IOKEnergyIO;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;
import ruiseki.omoshiroikamo.core.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * External Energy Port Proxy.
 * Adapts an external energy tile to be used as a modular port.
 * Supports unified energy system (RF/EU via IOKEnergy interfaces).
 *
 * Design Pattern: Adapter Pattern
 * - Implements IOKEnergyIO (which combines IOKEnergySink and IOKEnergySource)
 * - Uses AbstractExternalProxy for common proxy functionality
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
        return delegate(IOKEnergyTile.class, IOKEnergyTile::getEnergyStored, 0);
    }

    @Override
    public int getMaxEnergyStored() {
        return delegate(IOKEnergyTile.class, IOKEnergyTile::getMaxEnergyStored, 0);
    }

    @Override
    public void setEnergyStored(int stored) {
        delegateVoid(IOKEnergyTile.class, tile -> tile.setEnergyStored(stored));
    }

    @Override
    public int getEnergyTransfer() {
        return delegate(IOKEnergyTile.class, IOKEnergyTile::getEnergyTransfer, 0);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection side) {
        return delegate(IOKEnergyTile.class, tile -> tile.canConnectEnergy(side), false);
    }

    // ========== IOKEnergySink Implementation (Receive) ==========

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return delegate(IOKEnergySink.class, sink -> sink.receiveEnergy(side, amount, simulate), 0);
    }

    // ========== IOKEnergySource Implementation (Extract) ==========

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        return delegate(IOKEnergySource.class, source -> source.extractEnergy(side, amount, simulate), 0);
    }
}
