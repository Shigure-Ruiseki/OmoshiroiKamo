package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.output;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemEnergyExporter extends AbstractPartItem {

    public ItemEnergyExporter() {
        super(ModObject.ENERGY_EXPORTER.name);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyExporter();
    }
}
