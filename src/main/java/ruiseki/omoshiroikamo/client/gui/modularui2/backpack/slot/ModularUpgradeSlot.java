package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;

public class ModularUpgradeSlot extends ModularSlot {

    protected final BackpackHandler handler;
    private UpgradeSlotSH syncHandler = null;

    public ModularUpgradeSlot(IItemHandler itemHandler, int index, BackpackHandler gui) {
        super(itemHandler, index);
        this.handler = gui;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        ItemStack originalUpgradeItem = this.getStack();
        if (originalUpgradeItem == null) {
            return true;
        }

        ItemStack newUpgradeItem = player.inventory.getItemStack();
        if (newUpgradeItem == null) {
            if (originalUpgradeItem.getItem() instanceof ItemStackUpgrade original) {
                return handler.canRemoveStackUpgrade(original.multiplier(originalUpgradeItem));
            }
            return true;
        }

        if (originalUpgradeItem.getItem() instanceof ItemStackUpgrade original
            && newUpgradeItem.getItem() instanceof ItemStackUpgrade newer) {
            return handler
                .canReplaceStackUpgrade(original.multiplier(originalUpgradeItem), newer.multiplier(newUpgradeItem));
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
            return handler.canAddStackUpgrade(upgrade.multiplier(stack));
        }
        return item instanceof ItemUpgrade;
    }

}
