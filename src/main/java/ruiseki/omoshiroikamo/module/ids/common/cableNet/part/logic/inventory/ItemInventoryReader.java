package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.logic.inventory;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPartItem;

public class ItemInventoryReader extends AbstractPartItem {

    public ItemInventoryReader() {
        super(ModObject.itemInventoryReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new InventoryReader();
    }
}
