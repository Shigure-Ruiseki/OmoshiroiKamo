package ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemEnergyInterface extends AbstractPartItem {

    public ItemEnergyInterface() {
        super(ModObject.itemEnergyInterface.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyInterface();
    }
}
