package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyStorage;
import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.persist.nbt.INBTSerializable;

/**
 * Reference implementation of {@link cofh.api.energy.IEnergyStorage}. Use/extend this or implement your own.
 *
 * @author King Lemming
 */

public class EnergyStorage implements IEnergyStorage, INBTSerializable {

    protected int energy;
    protected int capacity;
    @Setter
    @Getter
    protected int maxReceive;
    @Setter
    @Getter
    protected int maxExtract;

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
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
        if (!canReceive()) return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) setEnergyInternal(energy + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) setEnergyInternal(energy - energyExtracted);
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
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

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        readFromNBT(tag);
    }

}
