package ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemEnergyInterfaceBus extends AbstractPartItem {

    public ItemEnergyInterfaceBus() {
        super(ModObject.itemEnergyInterfaceBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyInterfaceBus();
    }
}
