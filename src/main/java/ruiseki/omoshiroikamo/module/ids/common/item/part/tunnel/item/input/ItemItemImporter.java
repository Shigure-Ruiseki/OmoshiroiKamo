package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.input;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemItemImporter extends AbstractPartItem {

    public ItemItemImporter() {
        super(ModObject.itemItemImporter.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new ItemImporter();
    }
}
