package ruiseki.omoshiroikamo.module.cable.common.network.item.input;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemItemInput extends AbstractPartItem {

    public ItemItemInput() {
        super(ModObject.itemItemInput.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new ItemInput();
    }
}
