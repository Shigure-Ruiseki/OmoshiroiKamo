package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.inventory;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemInventoryReader extends AbstractPartItem {

    public ItemInventoryReader() {
        super(ModObject.itemInventoryReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new InventoryReader();
    }
}
