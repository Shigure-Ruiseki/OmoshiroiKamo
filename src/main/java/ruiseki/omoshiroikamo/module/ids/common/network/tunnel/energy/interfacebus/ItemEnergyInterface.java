package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.interfacebus;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

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
