package ruiseki.omoshiroikamo.module.ids.common.network.logic.part.redstone;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemRedstoneWriter extends AbstractPartItem {

    public ItemRedstoneWriter() {
        super(ModObject.itemRedstoneWriter.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new RedstoneWriter();
    }
}
