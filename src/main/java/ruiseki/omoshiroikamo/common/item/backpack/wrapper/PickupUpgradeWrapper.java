package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;

public class PickupUpgradeWrapper extends BasicUpgradeWrapper implements IPickupUpgrade {

    public PickupUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    @Override
    public boolean canPickup(ItemStack stack) {
        return checkFilter(stack);
    }
}
