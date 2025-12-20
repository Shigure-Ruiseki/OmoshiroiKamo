package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Reference implementation of {@link cofh.api.energy.IEnergyStorage}. Use/extend this or implement your own.
 *
 * @author King Lemming
 */

@Optional.Interface(modid = "CoFHLib", iface = "cofh.api.energy.IEnergyStorage", striprefs = true)
public class EnergyStorage implements IEnergyStorage {

    protected int energy;
    protected int capacity;
    @Setter
    @Getter
    protected int maxReceive;
    @Setter
    @Getter
    protected int maxExtract;

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        readFromNBT(nbt, "Energy");
    }

    public void writeToNBT(NBTTagCompound nbt) {
        writeToNBT(nbt, "Energy");
    }

    public void readFromNBT(NBTTagCompound nbt, String tag) {
        this.energy = nbt.getInteger(tag);
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
    }

    public void writeToNBT(NBTTagCompound nbt, String tag) {
        nbt.setInteger(tag, getEnergyStored());
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.energy > capacity) {
            this.energy = capacity;
        }
        onEnergyChanged();
    }

    public void setMaxTransfer(int maxTransfer) {
        this.setMaxReceive(maxTransfer);
        this.setMaxExtract(maxTransfer);
        onEnergyChanged();
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }
        onEnergyChanged();
    }

    public void modifyEnergyStored(int energy) {
        this.energy += energy;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        } else if (this.energy < 0) {
            this.energy = 0;
        }
        onEnergyChanged();
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.energy += energyReceived;
            onEnergyChanged();
        }

        return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            this.energy -= energyExtracted;
            onEnergyChanged();
        }

        return energyExtracted;
    }

    public void voidEnergy(int energy) {
        this.energy -= Math.min(this.energy, energy);
        onEnergyChanged();
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public void onEnergyChanged() {}
}
