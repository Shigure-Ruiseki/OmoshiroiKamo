package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.input;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemEnergyImporter extends AbstractPartItem {

    public ItemEnergyImporter() {
        super(ModObject.ENERGY_IMPORTER.name);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyImporter();
    }
}
