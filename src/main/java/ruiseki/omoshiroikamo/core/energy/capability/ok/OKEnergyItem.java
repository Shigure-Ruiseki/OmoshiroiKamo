package ruiseki.omoshiroikamo.core.energy.capability.ok;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.energy.IEnergyItem;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class OKEnergyItem implements EnergyIO {

    private final IEnergyItem handler;
    private final ItemStack itemStack;

    public OKEnergyItem(IEnergyItem handler, ItemStack itemStack) {
        this.handler = handler;
        this.itemStack = itemStack;

    }

    @Override
    public int extract(int amount, boolean simulate) {
        return handler.extractEnergy(itemStack, amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return handler.receiveEnergy(itemStack, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return true;
    }

}
