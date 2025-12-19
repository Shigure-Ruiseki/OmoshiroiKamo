package ruiseki.omoshiroikamo.common.item.deepMobLearning;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;

public class DeepLearnerHandler implements IItemHandlerModifiable {

    @Getter
    private final ItemStack deepLearner;

    @Getter
    private final ItemStackHandler handler;

    public static final String INVENTORY = "Inventory";

    public DeepLearnerHandler(ItemStack deepLearner) {
        this.deepLearner = deepLearner;
        handler = new ItemStackHandler(4) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                writeToItem();
            }
        };
        readFromItem();
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    public void writeToItem() {
        if (deepLearner == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(deepLearner);
        writeToNBT(tag);
        deepLearner.setTagCompound(tag);
    }

    public void readFromItem() {
        if (deepLearner == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(deepLearner);
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
