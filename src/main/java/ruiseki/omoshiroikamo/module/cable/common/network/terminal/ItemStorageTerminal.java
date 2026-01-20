package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemStorageTerminal extends AbstractPartItem {

    public ItemStorageTerminal() {
        super(ModObject.itemStorgeTerminal.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new StorageTerminal();
    }
}
