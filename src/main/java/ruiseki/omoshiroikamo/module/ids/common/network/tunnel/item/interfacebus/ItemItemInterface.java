package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemItemInterface extends AbstractPartItem {

    public ItemItemInterface() {
        super(ModObject.itemItemInterface.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemInterface();
    }
}
