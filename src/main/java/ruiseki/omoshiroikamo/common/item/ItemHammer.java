package ruiseki.omoshiroikamo.common.item;

import ruiseki.omoshiroikamo.api.enums.ModObject;

public class ItemHammer extends ItemOK {

    public ItemHammer() {
        super(ModObject.itemHammer);
        setMaxDamage(131);
        setTextureName("hammer");
    }
}
