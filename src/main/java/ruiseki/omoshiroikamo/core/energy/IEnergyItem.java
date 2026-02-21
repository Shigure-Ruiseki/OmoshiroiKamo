package ruiseki.omoshiroikamo.core.energy;

import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional;

@Optional.Interface(modid = "CoFHLib", iface = "cofh.api.energy.IEnergyContainerItem", striprefs = true)
public interface IEnergyItem extends IEnergyContainerItem {

    int receiveEnergy(ItemStack stack, int amount, boolean simulate);

    int extractEnergy(ItemStack stack, int amount, boolean simulate);

    int getEnergyStored(ItemStack stack);

    void setEnergyStored(ItemStack stack, int amount);

    int getMaxEnergyStored(ItemStack stack);
}
