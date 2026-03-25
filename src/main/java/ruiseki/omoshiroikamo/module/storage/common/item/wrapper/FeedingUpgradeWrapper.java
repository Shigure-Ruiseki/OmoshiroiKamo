package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;

public class FeedingUpgradeWrapper extends BasicUpgradeWrapper implements IFeedingUpgrade {

    public FeedingUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new UpgradeItemStackHandler(9) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack != null && stack.getItem() instanceof ItemFood;
            }

            @Override
            protected void onContentsChanged(int slot) {
                NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
                tag.setTag(IBasicFilterable.FILTER_ITEMS_TAG, this.serializeNBT());
            }
        };
    }

    @Override
    public int getFoodSlot(IItemHandler handler, int foodLevel, float health, float maxHealth) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0) continue;
            if (!checkFilter(stack)) continue;

            ItemFood item = stack.getItem() instanceof ItemFood ? (ItemFood) stack.getItem() : null;
            if (item == null) continue;

            int healingAmount = item.func_150905_g(stack);

            if (healingAmount <= 20 - foodLevel) return slot;
        }
        return -1;
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return check.getItem() instanceof ItemFood && super.checkFilter(check);
    }

}
