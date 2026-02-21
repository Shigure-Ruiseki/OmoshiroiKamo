package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.output;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPartItem;

public class ItemEnergyExporter extends AbstractPartItem {

    public ItemEnergyExporter() {
        super(ModObject.itemEnergyExporter.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyExporter();
    }
}
