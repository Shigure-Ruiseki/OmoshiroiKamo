package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;

public class ItemPolymerClay extends ItemOK {

    public ItemPolymerClay() {
        super(ModObject.POLYMER_CLAY.name);
        setMaxStackSize(64);
        setTextureName("dml/polymer_clay");
    }
}
