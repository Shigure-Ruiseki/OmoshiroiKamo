package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;

public class ItemDataModelBlank extends ItemOK {

    public ItemDataModelBlank() {
        super(ModObject.itemDataModelBlank.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("dml/data_model_blank");
    }

}
