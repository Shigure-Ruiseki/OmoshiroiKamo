package ruiseki.omoshiroikamo.api.storage;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Simple mana storage implementation (similar to CoFH EnergyStorage)
 */
public class ManaStorage {

    protected int mana;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ManaStorage(int capacity) {
        this(capacity, capacity, capacity);
    }

    public ManaStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public ManaStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public int getManaStored() {
        return mana;
    }

    public int getMaxManaStored() {
        return capacity;
    }

    public boolean isFull() {
        return mana >= capacity;
    }

    public boolean isEmpty() {
        return mana <= 0;
    }

    public int receiveMana(int amount, boolean simulate) {
        int receivable = Math.min(capacity - mana, Math.min(maxReceive, amount));
        if (receivable <= 0) return 0;

        if (!simulate) {
            setManaInternal(mana + receivable);
        }
        return receivable;
    }

    public int extractMana(int amount, boolean simulate) {
        int extractable = Math.min(mana, Math.min(maxExtract, amount));
        if (extractable <= 0) return 0;

        if (!simulate) {
            setManaInternal(mana - extractable);
        }
        return extractable;
    }

    public void setMana(int amount) {
        setManaInternal(amount);
    }

    public void voidMana(int amount) {
        if (amount > 0 && mana > 0) {
            setManaInternal(this.mana - amount);
        }
    }

    protected void setManaInternal(int value) {
        int clamped = clamp(value);
        if (clamped != mana) {
            mana = clamped;
            onManaChanged();
        }
    }

    protected int clamp(int value) {
        return Math.max(0, Math.min(capacity, value));
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (mana > capacity) mana = capacity;
    }

    public void setMaxTransfer(int transfer) {
        this.maxReceive = transfer;
        this.maxExtract = transfer;
    }

    public void setTransferRates(int receive, int extract) {
        this.maxReceive = receive;
        this.maxExtract = extract;
    }

    protected void onManaChanged() {
        // override in TE if needed
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Mana", mana);
        tag.setInteger("Capacity", capacity);
    }

    public void readFromNBT(NBTTagCompound tag) {
        mana = tag.getInteger("Mana");
        capacity = tag.getInteger("Capacity");
        if (mana > capacity) mana = capacity;
    }
}
