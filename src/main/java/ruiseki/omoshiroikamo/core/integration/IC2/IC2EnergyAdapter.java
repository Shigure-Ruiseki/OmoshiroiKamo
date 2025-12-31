package ruiseki.omoshiroikamo.core.integration.IC2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

/**
 * Adapter class for IC2 energy integration.
 * This class is only loaded when IC2 is present.
 * It implements IC2's IEnergySink and IEnergySource interfaces
 * and delegates to our IEnergyTile implementation.
 */
public class IC2EnergyAdapter implements IEnergySink, IEnergySource {

    private final ruiseki.omoshiroikamo.api.energy.IEnergyTile energyTile;
    private final TileEntity tileEntity;
    private boolean registered = false;

    public IC2EnergyAdapter(ruiseki.omoshiroikamo.api.energy.IEnergyTile energyTile, TileEntity tileEntity) {
        this.energyTile = energyTile;
        this.tileEntity = tileEntity;
    }

    // ==================== IC2 Registration ====================

    public void register() {
        if (tileEntity.getWorldObj().isRemote || registered) return;

        TileEntity existing = EnergyNet.instance
            .getTileEntity(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

        if (existing != this) {
            if (existing instanceof IEnergyTile) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) existing));
            }
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            registered = true;
        }
    }

    public void deregister() {
        if (tileEntity.getWorldObj().isRemote || !registered) return;

        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        registered = false;
    }

    public boolean isRegistered() {
        return registered;
    }

    // ==================== IC2 IEnergySink ====================

    @Override
    public double getDemandedEnergy() {
        int missing = energyTile.getMaxEnergyStored() - energyTile.getEnergyStored();
        return Math.max(0, missing * EnergyConfig.rftToEU);
    }

    @Override
    public int getSinkTier() {
        return EnergyConfig.ic2SinkTier;
    }

    @Override
    public double injectEnergy(ForgeDirection direction, double amount, double voltage) {
        if (!(energyTile instanceof ruiseki.omoshiroikamo.api.energy.IEnergySink)) {
            return amount;
        }
        ruiseki.omoshiroikamo.api.energy.IEnergySink sink = (ruiseki.omoshiroikamo.api.energy.IEnergySink) energyTile;

        int rf = (int) (amount / EnergyConfig.rftToEU);
        int accepted = sink.receiveEnergy(direction, rf, false);
        return amount - (accepted * EnergyConfig.rftToEU);
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return energyTile.canConnectEnergy(direction);
    }

    // ==================== IC2 IEnergySource ====================

    @Override
    public double getOfferedEnergy() {
        return energyTile.getEnergyStored() * EnergyConfig.rftToEU;
    }

    @Override
    public void drawEnergy(double amount) {
        if (!(energyTile instanceof ruiseki.omoshiroikamo.api.energy.IEnergySource)) {
            return;
        }
        ruiseki.omoshiroikamo.api.energy.IEnergySource source = (ruiseki.omoshiroikamo.api.energy.IEnergySource) energyTile;

        int rf = (int) (amount / EnergyConfig.rftToEU);
        source.extractEnergy(ForgeDirection.UNKNOWN, rf, false);
    }

    @Override
    public int getSourceTier() {
        return EnergyConfig.ic2SourceTier;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return energyTile.canConnectEnergy(direction);
    }
}
