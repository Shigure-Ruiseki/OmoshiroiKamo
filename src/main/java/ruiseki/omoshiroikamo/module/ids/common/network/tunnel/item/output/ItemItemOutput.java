package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.output;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

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
