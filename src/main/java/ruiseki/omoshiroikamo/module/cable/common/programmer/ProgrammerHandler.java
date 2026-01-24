package ruiseki.omoshiroikamo.module.cable.common.programmer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;

public class ProgrammerHandler {

    @Getter
    private final ItemStack itemStack;

    @Getter
    private final ItemStackHandler handler;

    public static final String INVENTORY = "Inventory";

    public ProgrammerHandler() {
        this(null);
    }

    public ProgrammerHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
        handler = new ItemStackHandler(10) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                writeToItem();
            }
        };
        readFromItem();
    }

    public void writeToItem() {
        if (itemStack == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(itemStack);
        writeToNBT(tag);
        itemStack.setTagCompound(tag);
    }

    public void readFromItem() {
        if (itemStack == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(itemStack);
        readFromNBT(tag);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setTag(INVENTORY, handler.serializeNBT());
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(INVENTORY)) {
            handler.deserializeNBT(tag.getCompoundTag(INVENTORY));
        }
    }
}
