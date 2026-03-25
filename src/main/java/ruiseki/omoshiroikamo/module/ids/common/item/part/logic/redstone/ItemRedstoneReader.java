package ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemRedstoneReader extends AbstractPartItem {

    public ItemRedstoneReader() {
        super(ModObject.REDSTONE_READER.name);
    }

    @Override
    public ICablePart createPart() {
        return new RedstoneReader();
    }
}
