package ruiseki.omoshiroikamo.module.machinery.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public class TEEnergyInputPort extends TileEntity implements IEnergyReceiver {

    private int energyStored = 0;
    private int maxEnergy = 100000;

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxEnergy - energyStored, maxReceive);
        if (!simulate) {
            energyStored += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energyStored;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return maxEnergy;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    /**
     * Extract energy for machine processing.
     * 
     * @return amount of energy actually extracted
     */
    public int extractEnergy(int amount) {
        int extracted = Math.min(energyStored, amount);
        energyStored -= extracted;
        return extracted;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("energyStored", energyStored);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energyStored = nbt.getInteger("energyStored");
    }
}
