package ruiseki.omoshiroikamo.module.cable.common.programmer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import lombok.Getter;

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
        handler = new ItemStackHandler(10);
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
