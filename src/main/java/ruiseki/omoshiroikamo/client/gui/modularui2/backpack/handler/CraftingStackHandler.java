package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.common.item.backpack.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class CraftingStackHandler extends UpgradeItemStackHandler {

    private final UpgradeWrapper wrapper;

    public CraftingStackHandler(int size, UpgradeWrapper wrapper) {
        super(size);
        this.wrapper = wrapper;
    }

    @Override
    protected void onContentsChanged(int slot) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(wrapper.getUpgrade());
        tag.setTag(ICraftingUpgrade.MATRIX_TAG, this.serializeNBT());
    }
}
