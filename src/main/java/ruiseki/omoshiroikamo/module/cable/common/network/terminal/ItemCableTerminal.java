package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemCableTerminal extends AbstractPartItem {

    public ItemCableTerminal() {
        super(ModObject.itemCableTerminal.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new CableTerminal();
    }
}
