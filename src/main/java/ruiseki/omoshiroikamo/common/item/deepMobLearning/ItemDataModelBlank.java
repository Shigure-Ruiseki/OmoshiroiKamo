package ruiseki.omoshiroikamo.common.item.deepMobLearning;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;

public class ItemDataModelBlank extends ItemOK {

    public ItemDataModelBlank() {
        super(ModObject.itemDataModelBlank.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("data_model_blank");
    }

}
