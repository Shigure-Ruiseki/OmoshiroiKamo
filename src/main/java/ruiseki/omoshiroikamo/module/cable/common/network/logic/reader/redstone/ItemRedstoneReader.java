package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.redstone;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemRedstoneReader extends AbstractPartItem {

    public ItemRedstoneReader() {
        super(ModObject.itemRedstoneReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new RedstoneReader();
    }
}
