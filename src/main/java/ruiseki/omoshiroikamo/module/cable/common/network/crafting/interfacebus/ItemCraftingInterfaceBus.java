package ruiseki.omoshiroikamo.module.cable.common.network.crafting.interfacebus;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemCraftingInterfaceBus extends AbstractPartItem {

    public ItemCraftingInterfaceBus() {
        super(ModObject.itemCraftingInterfaceBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new CraftingInterfaceBus();
    }
}
