package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemPolymerClay extends ItemOK {

    public ItemPolymerClay() {
        super(ModObject.itemPolymerClay.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("polymer_clay");
    }
}
