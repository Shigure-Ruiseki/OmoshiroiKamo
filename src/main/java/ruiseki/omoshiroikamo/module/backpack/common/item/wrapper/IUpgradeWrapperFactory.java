package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;

public interface IUpgradeWrapperFactory<W extends UpgradeWrapper> {

    W createWrapper(ItemStack stack);
}
