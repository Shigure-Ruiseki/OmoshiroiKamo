package ruiseki.omoshiroikamo.module.ids.common.network.logic.part.block;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemBlockReader extends AbstractPartItem {

    public ItemBlockReader() {
        super(ModObject.itemBlockReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new BlockReader();
    }
}
