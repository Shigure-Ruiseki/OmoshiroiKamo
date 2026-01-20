package ruiseki.omoshiroikamo.module.cable.common.network.energy.input;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

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
