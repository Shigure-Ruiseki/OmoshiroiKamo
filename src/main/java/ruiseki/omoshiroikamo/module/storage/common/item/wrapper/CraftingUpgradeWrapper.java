package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.UpgradeItemStackHandler;

public class CraftingUpgradeWrapper extends UpgradeWrapperBase implements ICraftingUpgrade {

    protected UpgradeItemStackHandler handler;

    public CraftingUpgradeWrapper(ItemStack upgrade, IStorageWrapper storage) {
        super(upgrade, storage);
        handler = new UpgradeItemStackHandler(10);
        handler.setOnSlotChanged((slot, stack) -> {
            NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
            tag.setTag(STORAGE_TAG, handler.serializeNBT());
        });

        NBTTagCompound handlerTag = ItemNBTHelpers.getCompound(upgrade, STORAGE_TAG, false);
        if (handlerTag != null) handler.deserializeNBT(handlerTag);
    }

    @Override
    public UpgradeItemStackHandler getStorage() {
        return handler;
    }

    @Override
    public CraftingDestination getCraftingDes() {

        int ordinal = ItemNBTHelpers
            .getInt(upgrade, CRAFTING_DEST_TAG, IBasicFilterable.FilterType.WHITELIST.ordinal());

        CraftingDestination[] types = CraftingDestination.values();

        if (ordinal < 0 || ordinal >= types.length) {
            return CraftingDestination.STORAGE;
        }

        return types[ordinal];
    }

    @Override
    public void setCraftingDes(CraftingDestination type) {
        if (type == null) type = CraftingDestination.STORAGE;
        ItemNBTHelpers.setInt(upgrade, CRAFTING_DEST_TAG, type.ordinal());
    }

    @Override
    public boolean isUseBackpack() {
        return ItemNBTHelpers.getBoolean(upgrade, USE_STORAGE_TAG, false);
    }

    @Override
    public void setUseBackpack(boolean used) {
        ItemNBTHelpers.setBoolean(upgrade, USE_STORAGE_TAG, used);
    }

}
