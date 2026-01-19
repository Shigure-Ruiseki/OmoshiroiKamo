package ruiseki.omoshiroikamo.module.cable.common.network.item.input;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemItemInputBus extends AbstractPartItem {

    public ItemItemInputBus() {
        super(ModObject.itemItemInputBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemInputBus();
    }
}
