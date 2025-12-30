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
        readFromNBT(nbt, "energy");
    }

    public void writeToNBT(NBTTagCompound nbt) {
        writeToNBT(nbt, "energy");
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
        setEnergyInternal(this.energy);
    }

    public void setEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        setEnergyInternal(this.energy);
    }

    public void setEnergyStorage(int capacity, int maxTransfer) {
        setEnergyStorage(capacity, maxTransfer, maxTransfer);
    }

    public void setEnergyStorage(int capacity) {
        setEnergyStorage(capacity, capacity, capacity);
    }

    public void setMaxTransfer(int maxTransfer) {
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    public void setEnergyStored(int energy) {
        setEnergyInternal(energy);
    }

    public void modifyEnergyStored(int delta) {
        if (delta != 0) {
            setEnergyInternal(this.energy + delta);
        }
    }

    public void voidEnergy(int amount) {
        if (amount > 0 && energy > 0) {
            setEnergyInternal(this.energy - amount);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int receivable = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (receivable <= 0) return 0;

        if (!simulate) {
            setEnergyInternal(energy + receivable);
        }
        return receivable;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extractable = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (extractable <= 0) return 0;

        if (!simulate) {
            setEnergyInternal(energy - extractable);
        }
        return extractable;
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public void onEnergyChanged() {}

    protected int clampEnergy(int value) {
        if (value < 0) return 0;
        return Math.min(value, capacity);
    }

    protected void setEnergyInternal(int newEnergy) {
        newEnergy = clampEnergy(newEnergy);
        if (newEnergy != this.energy) {
            this.energy = newEnergy;
            onEnergyChanged();
        }
    }

}
