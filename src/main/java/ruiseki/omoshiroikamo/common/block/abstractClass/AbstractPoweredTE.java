package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;
import ruiseki.omoshiroikamo.api.energy.IPowerContainer;
import ruiseki.omoshiroikamo.api.energy.PowerHandlerUtils;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketPowerStorage;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.plugin.compat.IC2Compat;

@Optional.InterfaceList({ @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
    @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2") })
public abstract class AbstractPoweredTE extends AbstractIOTE implements IEnergyHandler, IEnergySink, IPowerContainer {

    private int storedEnergyRF = 0;
    protected float lastSyncPowerStored = -1;
    boolean inICNet = false;
    protected boolean canReceivePower = true;

    protected EnergyStorage energyStorage;

    public AbstractPoweredTE(SlotDefinition slotDefinition) {
        super(slotDefinition);
        energyStorage = new EnergyStorage(100000);
    }

    public AbstractPoweredTE(SlotDefinition slotDefinition, EnergyStorage energyStorage) {
        super(slotDefinition);
        this.energyStorage = energyStorage;
    }

    @Override
    public void doUpdate() {
        super.doUpdate();

        if (!isServerSide()) {
            return;
        }

        if (LibMods.IC2.isLoaded() && !this.inICNet) {
            IC2Compat.loadIC2Tile(this);
            this.inICNet = true;
        }

        boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (LibMods.IC2.isLoaded()) {
            unload();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        unload();
    }

    @Optional.Method(modid = "IC2")
    void unload() {
        if (LibMods.IC2.isLoaded() && this.inICNet) {
            IC2Compat.unloadIC2Tile(this);
            this.inICNet = false;
        }
    }

    public void setCanReceivePower(boolean value) {
        this.canReceivePower = value;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY, storedEnergyRF);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        int energy;
        if (root.hasKey("storedEnergy")) {
            float storedEnergyMJ = root.getFloat("storedEnergy");
            energy = (int) (storedEnergyMJ * 10);
        } else {
            energy = root.getInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY);
        }
        setEnergyStored(energy);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (!canReceivePower) {
            return 0;
        }
        return PowerHandlerUtils.receiveInternal(this, maxReceive, from, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getEnergyStored();
    }

    public int getEnergyStored() {
        return storedEnergyRF;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public void setEnergyStored(int energy) {
        storedEnergyRF = Math.min(energy, getMaxEnergyStored());
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    public int getMaxEnergyReceived() {
        return energyStorage.getMaxReceive();
    }

    public int getMaxEnergyExtract() {
        return energyStorage.getMaxExtract();
    }

    public int getPowerUsePerTick() {
        return energyStorage.getMaxExtract();
    }

    @Optional.Method(modid = "IC2")
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return canConnectEnergy(direction);
    }

    @Optional.Method(modid = "IC2")
    public double getDemandedEnergy() {
        if (!canReceivePower) {
            return 0;
        }
        return convertRFtoEU(getMaxEnergyReceived() - storedEnergyRF, getIC2Tier());
    }

    @Optional.Method(modid = "IC2")
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (!canReceivePower) {
            return amount;
        }
        int rf = convertEUtoRF(amount);
        int r = Math.max(0, Math.min(getMaxEnergyReceived() - storedEnergyRF, rf));
        storedEnergyRF += r;
        double eu = convertRFtoEU(r, getIC2Tier());
        return amount - eu;
    }

    @Optional.Method(modid = "IC2")
    public int getSinkTier() {
        return getIC2Tier();
    }

    int getIC2Tier() {
        return 2;
    }

    public static double convertRFtoEU(int rf, int maxTier) {
        return (double) rf / 4;
    }

    public static int convertEUtoRF(double eu) {
        return (int) eu * 4;
    }
}
