package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemEnergyFilterInterface extends AbstractPartItem {

    public ItemEnergyFilterInterface() {
        super(ModObject.itemEnergyFilterInterface.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyFilterInterface();
    }
}
