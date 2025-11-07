package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyConnectionPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyHandlerPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyProviderPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyReceiverPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.IPowerInterface;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractPoweredTE;

public class PowerHandlerUtils {

    public static final String STORED_ENERGY_NBT_KEY = "storedEnergyRF";

    public static IPowerInterface create(Object o) {
        if (o instanceof IEnergyHandler) {
            return new EnergyHandlerPI((IEnergyHandler) o);
        } else if (o instanceof IEnergyProvider) {
            return new EnergyProviderPI((IEnergyProvider) o);
        } else if (o instanceof IEnergyReceiver) {
            return new EnergyReceiverPI((IEnergyReceiver) o);
        } else if (o instanceof IEnergyConnection) {
            return new EnergyConnectionPI((IEnergyConnection) o);
        }
        return null;
    }

    public static int getStoredEnergyForItem(ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();
        if (tag == null) {
            return 0;
        }

        if (tag.hasKey("storedEnergy")) {
            double storedMj = tag.getDouble("storedEnergy");
            return (int) (storedMj * 10);
        }

        return tag.getInteger(STORED_ENERGY_NBT_KEY);
    }

    public static void setStoredEnergyForItem(ItemStack item, int storedEnergy) {
        NBTTagCompound tag = item.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setInteger(STORED_ENERGY_NBT_KEY, storedEnergy);
        item.setTagCompound(tag);
    }

    public static int receiveInternal(AbstractPoweredTE target, int maxReceive, ForgeDirection from, boolean simulate) {
        int result = Math.min(target.getMaxEnergyReceived(), maxReceive);
        result = Math.min(target.getMaxEnergyStored() - target.getEnergyStored(), result);
        result = Math.max(0, result);
        if (result > 0 && !simulate) {
            target.setEnergyStored(target.getEnergyStored() + result);
        }
        return result;
    }

}
