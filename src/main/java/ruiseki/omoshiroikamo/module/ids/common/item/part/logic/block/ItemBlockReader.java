package ruiseki.omoshiroikamo.module.ids.common.item.part.logic.block;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemBlockReader extends AbstractPartItem {

    public ItemBlockReader() {
        super(ModObject.BLOCK_READER.name);
    }

    @Override
    public ICablePart createPart() {
        return new BlockReader();
    }
}
