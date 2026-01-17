package ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;

public class ItemItemInterfaceBus extends AbstractPartItem {

    public ItemItemInterfaceBus() {
        super(ModObject.itemItemInterfaceBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemInterfaceBus();
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IItemPart.class;
    }
}
