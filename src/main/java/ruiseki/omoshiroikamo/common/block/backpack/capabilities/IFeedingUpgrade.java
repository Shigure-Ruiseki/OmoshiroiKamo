package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;

public interface IFeedingUpgrade extends IBasicFilterable, IToggleable, IUpgrade {

    ItemStack getFeedingStack(IItemHandler handler, ItemStack stack, int foodLevel, float health, float maxHealth);
}
