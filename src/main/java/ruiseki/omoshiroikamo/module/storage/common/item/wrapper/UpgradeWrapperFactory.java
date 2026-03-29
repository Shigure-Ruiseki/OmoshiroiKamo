package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;

public class UpgradeWrapperFactory {

    @SuppressWarnings("unchecked")
    public static <W extends UpgradeWrapperBase> W createWrapper(ItemStack stack, IStorageWrapper wrapper) {
        if (stack == null) {
            return null;
        }
        Item item = stack.getItem();
        if (!(item instanceof IUpgradeWrapperFactory<?>factory)) {
            return null;
        }
        return (W) factory.createWrapper(stack, wrapper);
    }
}
