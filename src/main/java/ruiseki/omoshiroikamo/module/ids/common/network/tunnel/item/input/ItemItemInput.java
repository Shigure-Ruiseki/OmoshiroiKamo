package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.input;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemItemInput extends AbstractPartItem {

    public ItemItemInput() {
        super(ModObject.itemItemInput.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new ItemInput();
    }
}
