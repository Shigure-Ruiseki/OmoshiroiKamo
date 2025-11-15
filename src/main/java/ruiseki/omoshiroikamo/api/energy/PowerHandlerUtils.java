package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyConnectionPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyHandlerPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyProviderPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.EnergyReceiverPI;
import ruiseki.omoshiroikamo.api.energy.powerInterface.IPowerInterface;

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

}
