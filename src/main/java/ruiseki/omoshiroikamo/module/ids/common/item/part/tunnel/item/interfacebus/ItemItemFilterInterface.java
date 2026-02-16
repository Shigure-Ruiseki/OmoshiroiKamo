package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemItemFilterInterface extends AbstractPartItem {

    public ItemItemFilterInterface() {
        super(ModObject.itemItemFilterInterface.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemFilterInterface();
    }
}
