package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemEnergyOutput extends AbstractPartItem {

    public ItemEnergyOutput() {
        super(ModObject.itemEnergyOutput.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyOutput();
    }
}
