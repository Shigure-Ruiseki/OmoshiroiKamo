package ruiseki.omoshiroikamo.common.item.deepMobLearning;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;

public class ItemSootCoveredPlate extends ItemOK {

    public ItemSootCoveredPlate() {
        super(ModObject.itemSootCoveredPlate.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("soot_covered_plate");
    }
}
