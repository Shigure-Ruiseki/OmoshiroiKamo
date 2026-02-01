package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.output;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

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
