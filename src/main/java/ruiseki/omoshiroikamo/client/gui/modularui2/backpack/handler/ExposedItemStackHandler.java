package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler;

import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.common.item.backpack.capabilities.FilterableWrapper;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class ExposedItemStackHandler extends ItemStackHandler {

    private final FilterableWrapper wrapper;

    public ExposedItemStackHandler(int size, FilterableWrapper wrapper) {
        super(size);
        this.wrapper = wrapper;
    }

    @Override
    protected void onContentsChanged(int slot) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(wrapper.getUpgrade());
        tag.setTag(FilterableWrapper.FILTER_ITEMS_TAG, this.serializeNBT());
    }
}
