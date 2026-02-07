package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemSootCoveredPlate extends ItemOK {

    public ItemSootCoveredPlate() {
        super(ModObject.itemSootCoveredPlate.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("dml/soot_covered_plate");
    }
}
