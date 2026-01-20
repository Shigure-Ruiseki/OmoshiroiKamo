package ruiseki.omoshiroikamo.module.cable.common.network.item.output;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemItemOutput extends AbstractPartItem {

    public ItemItemOutput() {
        super(ModObject.itemItemOutput.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemOutput();
    }
}
