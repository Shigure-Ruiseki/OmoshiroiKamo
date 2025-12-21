package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;
import ruiseki.omoshiroikamo.common.network.PacketEnergy;
import ruiseki.omoshiroikamo.common.network.PacketHandler;

public abstract class AbstractEnergyTE extends AbstractTE implements IEnergyTile {

    private int lastSyncPowerStored;
    protected EnergyStorage energyStorage;

    public AbstractEnergyTE(int energyCapacity, int energyMaxReceive) {
        energyStorage = new EnergyStorage(energyCapacity, energyMaxReceive) {

            @Override
            public void onEnergyChanged() {
                super.onEnergyChanged();
                markDirty();
            }
        };
    }

    public AbstractEnergyTE(int energyCapacity) {
        this(energyCapacity, energyCapacity);
    }

    public AbstractEnergyTE() {
        this(100000, 100000);
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = getEnergyStored();
            PacketHandler.sendToAllAround(new PacketEnergy(this), this);
        }

        return false;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void setEnergyStored(int storedEnergy) {
        energyStorage.setEnergyStored(storedEnergy);
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        energyStorage.writeToNBT(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        energyStorage.readFromNBT(root);
    }
}
