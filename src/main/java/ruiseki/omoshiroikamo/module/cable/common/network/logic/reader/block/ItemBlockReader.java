package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.block;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemBlockReader extends AbstractPartItem {

    public ItemBlockReader() {
        super(ModObject.itemBlockReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new BlockReader();
    }
}
