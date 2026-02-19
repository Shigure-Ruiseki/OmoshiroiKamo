package ruiseki.omoshiroikamo.api.gas;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.conduit.gas.GasUtil;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import ruiseki.omoshiroikamo.api.persist.nbt.INBTSerializable;

public class SmartGasTank extends GasTank implements IGasTank, INBTSerializable {

    private int capacity;

    public SmartGasTank(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    public float getFilledRatio() {
        if (getStored() <= 0) {
            return 0;
        }
        if (getMaxGas() <= 0) {
            return -1;
        }
        float res = (float) getStored() / getMaxGas();
        return res;
    }

    public boolean isFull() {
        return getStored() >= getMaxGas();
    }

    public void setAmount(int amount) {
        if (stored != null) {
            stored.amount = amount;
            onContentsChanged();
        }
    }

    public int getAvailableSpace() {
        return getMaxGas() - getStored();
    }

    public void addAmount(int amount) {
        setAmount(getStored() + amount);
    }

    @Override
    public int getMaxGas() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (getStored() > capacity) {
            setAmount(capacity);
        }
    }

    @Override
    public int receive(GasStack resource, boolean doReceive) {
        if (resource == null || resource.getGas()
            .getID() < 0) {
            return 0;
        }

        // If tank is empty or has zero amount, accept any gas type
        if (stored == null || stored.getGas()
            .getID() < 0 || stored.amount <= 0) {
            int fill = Math.min(resource.amount, capacity);
            if (doReceive) {
                setGas(resource.copy());
                stored.amount = fill;
                onContentsChanged();
            }
            return fill;
        }

        if (!stored.isGasEqual(resource)) return 0;

        int space = capacity - stored.amount;
        int filled = Math.min(space, resource.amount);

        if (doReceive && filled > 0) {
            stored.amount += filled;
            onContentsChanged();
        }

        return filled;
    }

    @Override
    public GasStack draw(int maxDrain, boolean doDraw) {
        if (stored == null || stored.amount <= 0) return null;

        int drained = Math.min(maxDrain, stored.amount);

        if (doDraw) {
            stored.amount -= drained;
            onContentsChanged();
        }

        return new GasStack(stored.getGas(), drained);
    }

    public String getGasName() {
        return stored != null ? stored.getGas()
            .getLocalizedName() : null;
    }

    public boolean containsValidGas() {
        return GasUtil.isGasValid(stored);
    }

    public void writeCommon(NBTTagCompound nbtRoot) {
        writeCommon("gasTank", nbtRoot);
    }

    public void writeCommon(String name, NBTTagCompound nbtRoot) {
        if (getStored() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            write(tankRoot);
            nbtRoot.setTag(name, tankRoot);
        } else {
            nbtRoot.removeTag(name);
        }
    }

    public void readCommon(NBTTagCompound nbtRoot) {
        readCommon("gasTank", nbtRoot);
    }

    public void readCommon(String name, NBTTagCompound nbtRoot) {
        NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag(name);
        if (tankRoot != null) {
            read(tankRoot);
            onContentsChanged();
        } else {
            setGas(null);
        }
    }

    public boolean isEmpty() {
        return stored == null || stored.amount == 0;
    }

    @Override
    public GasTankInfo getInfo() {
        return new GasTankInfo(this);
    }

    protected void onContentsChanged() {}

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommon(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        readCommon(tag);
    }
}
