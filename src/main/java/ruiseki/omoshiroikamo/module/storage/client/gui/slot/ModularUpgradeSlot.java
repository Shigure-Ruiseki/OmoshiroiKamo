package ruiseki.omoshiroikamo.module.storage.client.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.storage.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class ModularUpgradeSlot extends ModularSlot {

    private final StoragePanel panel;
    private final StorageWrapper wrapper;

    public ModularUpgradeSlot(StorageWrapper wrapper, int index, StoragePanel panel) {
        super(wrapper.upgradeItemStackHandler, index);
        this.panel = panel;
        this.wrapper = wrapper;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        ItemStack originalStack = this.getStack();
        if (originalStack == null) {
            return true;
        }

        ItemStack cursor = player.inventory.getItemStack();
        boolean cursorEmpty = (cursor == null);

        Item originalItem = originalStack.getItem();

        if (originalItem instanceof ItemStackUpgrade) {
            int slotIndex = getSlotIndex();

            if (cursorEmpty) {
                return wrapper.canReplaceStackUpgrade(slotIndex, null);
            }

            if (cursor.getItem() instanceof ItemStackUpgrade) {
                return wrapper.canReplaceStackUpgrade(slotIndex, cursor);
            }

            return wrapper.canReplaceStackUpgrade(slotIndex, null);
        }

        return true;
    }

    @Override
    public int getItemStackLimit(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemStackUpgrade upgrade) {
            return wrapper.canAddStackUpgrade(upgrade.multiplier(stack));
        }

        return item instanceof ItemUpgrade;
    }

}
