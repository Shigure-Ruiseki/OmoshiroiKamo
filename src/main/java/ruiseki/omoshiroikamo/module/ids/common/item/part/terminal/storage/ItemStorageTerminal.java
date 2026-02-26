package ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractPartItem;

public class ItemStorageTerminal extends AbstractPartItem {

    public ItemStorageTerminal() {
        super(ModObject.itemStorageTerminal.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new StorageTerminal();
    }
}
