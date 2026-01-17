package ruiseki.omoshiroikamo.module.cable.common.network.energy.input;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class ItemEnergyInputBus extends AbstractPartItem {

    public ItemEnergyInputBus() {
        super(ModObject.itemEnergyInputBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyInputBus();
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IEnergyPart.class;
    }
}
