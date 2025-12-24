package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;

public class CraftingUpgradeWrapper extends UpgradeWrapper implements ICraftingUpgrade {

    protected UpgradeItemStackHandler handler;

    public CraftingUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new UpgradeItemStackHandler(10) {

            @Override
            protected void onContentsChanged(int slot) {
                NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
                tag.setTag(ICraftingUpgrade.STORAGE_TAG, this.serializeNBT());
            }
        };
    }

    @Override
    public UpgradeItemStackHandler getStorage() {
        NBTTagCompound handlerTag = ItemNBTUtils.getCompound(upgrade, STORAGE_TAG, false);
        if (handlerTag != null) {
            handler.deserializeNBT(handlerTag);
        }
        return handler;
    }

    @Override
    public void setStorage(UpgradeItemStackHandler handler) {
        if (handler != null) {
            ItemNBTUtils.setCompound(upgrade, STORAGE_TAG, handler.serializeNBT());
        }
    }

    @Override
    public CraftingDestination getCraftingDes() {
        int ordinal = ItemNBTUtils.getInt(upgrade, CRAFTING_DEST_TAG, IBasicFilterable.FilterType.WHITELIST.ordinal());
        CraftingDestination[] types = CraftingDestination.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return CraftingDestination.BACKPACK;
        }
        return types[ordinal];
    }

    @Override
    public void setCraftingDes(CraftingDestination type) {
        if (type == null) {
            type = CraftingDestination.BACKPACK;
        }
        ItemNBTUtils.setInt(upgrade, CRAFTING_DEST_TAG, type.ordinal());

    }

    @Override
    public boolean isUseBackpack() {
        return ItemNBTUtils.getBoolean(upgrade, USE_BACKPACK_TAG, false);
    }

    @Override
    public void setUseBackpack(boolean used) {
        ItemNBTUtils.setBoolean(upgrade, USE_BACKPACK_TAG, used);
    }

}
