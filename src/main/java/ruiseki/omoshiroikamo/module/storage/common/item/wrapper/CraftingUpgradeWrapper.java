package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.UpgradeItemStackHandler;

public class CraftingUpgradeWrapper extends UpgradeWrapper implements ICraftingUpgrade {

    protected UpgradeItemStackHandler handler;
    private boolean initialized = false;

    public CraftingUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new UpgradeItemStackHandler(10) {

            @Override
            protected void onContentsChanged(int slot) {
                NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
                tag.setTag(ICraftingUpgrade.STORAGE_TAG, this.serializeNBT());
                OmoshiroiKamo.okLog(
                    tag.getTag(ICraftingUpgrade.STORAGE_TAG)
                        .toString());
            }
        };
    }

    @Override
    public UpgradeItemStackHandler getStorage() {
        if (!initialized) {
            NBTTagCompound handlerTag = ItemNBTHelpers.getCompound(upgrade, STORAGE_TAG, false);
            if (handlerTag != null) handler.deserializeNBT(handlerTag);
            initialized = true;
        }
        return handler;
    }

    @Override
    public void setStorage(UpgradeItemStackHandler handler) {
        if (handler != null) {
            ItemNBTHelpers.setCompound(upgrade, STORAGE_TAG, handler.serializeNBT());
        }
    }

    @Override
    public CraftingDestination getCraftingDes() {

        int ordinal = ItemNBTHelpers
            .getInt(upgrade, CRAFTING_DEST_TAG, IBasicFilterable.FilterType.WHITELIST.ordinal());

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
        ItemNBTHelpers.setInt(upgrade, CRAFTING_DEST_TAG, type.ordinal());

    }

    @Override
    public boolean isUseBackpack() {
        return ItemNBTHelpers.getBoolean(upgrade, USE_BACKPACK_TAG, false);
    }

    @Override
    public void setUseBackpack(boolean used) {
        ItemNBTHelpers.setBoolean(upgrade, USE_BACKPACK_TAG, used);
    }

}
