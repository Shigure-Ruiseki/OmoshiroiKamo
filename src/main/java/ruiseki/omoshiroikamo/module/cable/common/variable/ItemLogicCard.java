package ruiseki.omoshiroikamo.module.cable.common.variable;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemLogicCard extends ItemOK {

    public ItemLogicCard() {
        super(ModObject.itemLogicCard.unlocalisedName);
        setMaxStackSize(1);
    }
}
