package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemEnergyOutputBus extends AbstractPartItem {

    public ItemEnergyOutputBus() {
        super(ModObject.itemEnergyOutputBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyOutputBus();
    }
}
