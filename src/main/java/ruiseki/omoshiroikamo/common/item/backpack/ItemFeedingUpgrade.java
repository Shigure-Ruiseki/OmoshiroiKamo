package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IFeedingUpgrade;
import ruiseki.omoshiroikamo.common.block.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemFeedingUpgrade extends ItemUpgrade implements IFeedingUpgrade {

    public ItemFeedingUpgrade() {
        super(ModObject.itemFeedingUpgrade);
        setMaxStackSize(1);
        setTextureName("feeding_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "feeding_upgrade"));
        list.add(
            ItemNBTUtils.getNBT(itemstack)
                .toString());
    }

    @Override
    public ItemStack getFeedingStack(IItemHandler handler, ItemStack upgrade, int foodLevel, float health,
        float maxHealth) {
        int size = handler.getSlots();
        for (int i = 0; i < size; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (!(stack.getItem() instanceof ItemFood food)) {
                continue;
            }
            int healingAmount = food.func_150905_g(stack);

            if (healingAmount <= 20 - foodLevel && checkFilter(upgrade, stack)) {
                return handler.extractItem(i, 1, false);
            }
        }
        return null;
    }

    @Override
    public ExposedItemStackHandler getFilterItems(ItemStack upgrade) {

        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        NBTTagCompound handlerTag = tag.getCompoundTag(FILTER_ITEMS_TAG);
        ExposedItemStackHandler copy = new ExposedItemStackHandler(9) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return getStackInSlot(slot).getItem() instanceof ItemFood;
            }
        };
        copy.deserializeNBT(handlerTag);
        return copy;
    }

    @Override
    public void setFilterItems(ItemStack upgrade, ExposedItemStackHandler handler) {
        if (handler == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setTag(FILTER_ITEMS_TAG, handler.serializeNBT());
    }

    @Override
    public boolean checkFilter(ItemStack upgrade, ItemStack check) {
        return check.getItem() instanceof ItemFood && IFeedingUpgrade.super.checkFilter(upgrade, check);
    }

    @Override
    public FilterType getFilterType(ItemStack upgrade) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        int ordinal = tag.getInteger(FILTER_TYPE_TAG);
        FilterType[] types = FilterType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return FilterType.WHITELIST;
        }
        return types[ordinal];
    }

    @Override
    public void setFilterType(ItemStack stack, FilterType type) {
        if (type == null) {
            type = FilterType.WHITELIST;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(stack);
        tag.setInteger(FILTER_TYPE_TAG, type.ordinal());
    }
}
