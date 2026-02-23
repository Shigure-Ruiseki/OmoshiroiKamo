package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.output;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPartItem;

public class ItemItemExporter extends AbstractPartItem {

    public ItemItemExporter() {
        super(ModObject.itemItemExporter.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new ItemExporter();
    }
}
