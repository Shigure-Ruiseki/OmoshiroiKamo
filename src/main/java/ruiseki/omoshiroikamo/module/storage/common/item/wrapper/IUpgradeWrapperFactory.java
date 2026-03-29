package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;

public interface IUpgradeWrapperFactory<W extends UpgradeWrapperBase> {

    W createWrapper(ItemStack stack, IStorageWrapper wrapper);
}
