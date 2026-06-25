package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class ItemSootCoveredPlate extends ItemOK {

    public ItemSootCoveredPlate() {
        super(ModObject.SOOT_COVERED_PLATE.name);
        setMaxStackSize(64);
        setTextureName(Reference.PREFIX_MOD + "dml/soot_covered_plate");
    }
}
