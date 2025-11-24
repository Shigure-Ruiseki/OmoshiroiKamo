package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;

public class AdvancedPickupUpgradeWrapper extends AdvancedUpgradeWrapper implements IPickupUpgrade {

    public AdvancedPickupUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    @Override
    public boolean canPickup(ItemStack stack) {
        return checkFilter(stack);
    }
}
