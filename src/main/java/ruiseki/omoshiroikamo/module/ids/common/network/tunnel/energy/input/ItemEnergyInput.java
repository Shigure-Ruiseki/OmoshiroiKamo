package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.input;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemEnergyInput extends AbstractPartItem {

    public ItemEnergyInput() {
        super(ModObject.itemEnergyInput.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyInput();
    }
}
