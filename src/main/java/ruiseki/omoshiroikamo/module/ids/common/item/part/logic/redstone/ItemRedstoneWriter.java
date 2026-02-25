package ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemRedstoneWriter extends AbstractPartItem {

    public ItemRedstoneWriter() {
        super(ModObject.itemRedstoneWriter.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new RedstoneWriter();
    }
}
