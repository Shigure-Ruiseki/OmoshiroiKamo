package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting.interfacebus;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPartItem;

public class ItemCraftingInterface extends AbstractPartItem {

    public ItemCraftingInterface() {
        super(ModObject.itemCraftingInterface.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new CraftingInterface();
    }
}
