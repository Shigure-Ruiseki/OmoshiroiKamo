package ruiseki.omoshiroikamo.module.dml.common.item;

import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class ItemDataModelBlank extends ItemOK {

    public ItemDataModelBlank() {
        super(ModObject.DATA_MODEL_BLANK.name);
        setMaxStackSize(64);
        setTextureName(Reference.PREFIX_MOD + "dml/data_model_blank");
    }

}
