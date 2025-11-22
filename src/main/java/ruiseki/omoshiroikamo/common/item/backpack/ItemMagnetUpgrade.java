package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IMagnetUpgrade;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IToggleable;
import ruiseki.omoshiroikamo.common.block.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemMagnetUpgrade extends ItemUpgrade implements IMagnetUpgrade {

    public ItemMagnetUpgrade() {
        super(ModObject.itemMagnetUpgrade);
        setMaxStackSize(1);
        setTextureName("magnet_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "magnet_upgrade"));
        list.add(
            ItemNBTUtils.getNBT(itemstack)
                .toString());
    }

    @Override
    public boolean isEnabled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean(ENABLED_TAG);
    }

    @Override
    public void setEnabled(ItemStack stack, boolean enabled) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(stack);
        tag.setBoolean(IToggleable.ENABLED_TAG, enabled);
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

    @Override
    public ExposedItemStackHandler getFilterItems(ItemStack upgrade) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        NBTTagCompound handlerTag = tag.getCompoundTag(FILTER_ITEMS_TAG);
        ExposedItemStackHandler copy = new ExposedItemStackHandler(9);
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
}
