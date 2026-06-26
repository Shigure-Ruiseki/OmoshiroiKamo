package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class ItemPolymerClay extends ItemOK {

    public ItemPolymerClay() {
        super(ModObject.POLYMER_CLAY.name);
        setMaxStackSize(64);
        setTextureName(Reference.PREFIX_MOD + "dml/polymer_clay");
    }
}
