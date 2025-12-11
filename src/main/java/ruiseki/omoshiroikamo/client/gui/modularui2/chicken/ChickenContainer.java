package ruiseki.omoshiroikamo.client.gui.modularui2.chicken;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularContainer;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;

/**
 * Container used by Roost/Breeder style GUIs to ensure chicken stacks never
 * exceed the
 * configurable cap when players manipulate their inventory while the GUI is
 * open.
 */
public class ChickenContainer extends ModularContainer {

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
        ItemStack result = super.slotClick(slotId, mouseButton, mode, player);
        if (player == null || player.worldObj == null) {
            return result;
        }

        boolean serverSide = !player.worldObj.isRemote;
        if (enforceChickenStackLimit(player, serverSide)) {
            if (serverSide) {
                detectAndSendChanges();
                if (player instanceof EntityPlayerMP mp) {
                    mp.updateHeldItem();
                }
            }
        }
        return result;
    }

    private boolean enforceChickenStackLimit(EntityPlayer player, boolean allowDrops) {
        InventoryPlayer inventory = player.inventory;
        if (inventory == null) {
            return false;
        }

        int limit = ChickenConfig.getChickenStackLimit();
        AtomicBoolean changed = new AtomicBoolean(false);

        normalizeCarriedStack(player, inventory, limit, changed, allowDrops);

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!DataChicken.isChicken(stack) || stack.stackSize <= limit) {
                continue;
            }

            int overflow = stack.stackSize - limit;
            stack.stackSize = limit;
            inventory.setInventorySlotContents(i, stack);
            changed.set(true);
            overflow = redistributeOverflow(player, inventory, stack, overflow, limit, i, changed, allowDrops);

            if (overflow > 0) {
                dropOverflow(player, stack, overflow, limit, changed, allowDrops);
            }
        }

        if (changed.get()) {
            inventory.markDirty();
        }
        return changed.get();
    }

    private void normalizeCarriedStack(EntityPlayer player, InventoryPlayer inventory, int limit,
            AtomicBoolean changed, boolean allowDrops) {
        ItemStack carried = inventory.getItemStack();
        if (!DataChicken.isChicken(carried) || carried.stackSize <= limit) {
            return;
        }

        int overflow = carried.stackSize - limit;
        carried.stackSize = limit;
        inventory.setItemStack(carried);
        changed.set(true);
        overflow = redistributeOverflow(player, inventory, carried, overflow, limit, -1, changed, allowDrops);
        if (overflow > 0) {
            dropOverflow(player, carried, overflow, limit, changed, allowDrops);
        }
    }

    private int redistributeOverflow(EntityPlayer player, InventoryPlayer inventory, ItemStack template, int overflow,
            int limit, int sourceSlot, AtomicBoolean changed, boolean allowDrops) {
        if (overflow <= 0) {
            return 0;
        }

        int remaining = overflow;

        if (sourceSlot != -1) {
            remaining = fillCarriedStack(inventory, template, remaining, limit, changed);
        }

        remaining = fillExistingSlots(inventory, template, remaining, limit, sourceSlot, changed);
        remaining = fillEmptySlots(inventory, template, remaining, limit, sourceSlot, changed);

        return remaining;
    }

    private int fillCarriedStack(InventoryPlayer inventory, ItemStack template, int overflow, int limit,
            AtomicBoolean changed) {
        if (overflow <= 0) {
            return overflow;
        }

        ItemStack carried = inventory.getItemStack();

        if (carried == null) {
            int move = Math.min(limit, overflow);
            inventory.setItemStack(copyWithAmount(template, move));
            changed.set(true);
            return overflow - move;
        }

        if (!stacksMatch(carried, template) || carried.stackSize >= limit) {
            return overflow;
        }

        int move = Math.min(limit - carried.stackSize, overflow);
        carried.stackSize += move;
        inventory.setItemStack(carried);
        changed.set(true);
        return overflow - move;
    }

    private int fillExistingSlots(InventoryPlayer inventory, ItemStack template, int overflow, int limit,
            int sourceSlot, AtomicBoolean changed) {
        if (overflow <= 0) {
            return overflow;
        }

        int remaining = overflow;
        for (int i = 0; i < inventory.getSizeInventory() && remaining > 0; i++) {
            if (i == sourceSlot) {
                continue;
            }
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (!stacksMatch(slotStack, template) || slotStack.stackSize >= limit) {
                continue;
            }
            int move = Math.min(limit - slotStack.stackSize, remaining);
            slotStack.stackSize += move;
            inventory.setInventorySlotContents(i, slotStack);
            remaining -= move;
            changed.set(true);
        }

        return remaining;
    }

    private int fillEmptySlots(InventoryPlayer inventory, ItemStack template, int overflow, int limit, int sourceSlot,
            AtomicBoolean changed) {
        if (overflow <= 0) {
            return overflow;
        }

        int remaining = overflow;
        for (int i = 0; i < inventory.getSizeInventory() && remaining > 0; i++) {
            if (i == sourceSlot) {
                continue;
            }
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (slotStack != null) {
                continue;
            }
            int move = Math.min(limit, remaining);
            inventory.setInventorySlotContents(i, copyWithAmount(template, move));
            remaining -= move;
            changed.set(true);
        }

        return remaining;
    }

    private void dropOverflow(EntityPlayer player, ItemStack template, int overflow, int limit,
            AtomicBoolean changed, boolean allowDrops) {
        if (!allowDrops || player == null || player.worldObj == null) {
            changed.set(true);
            return;
        }

        int remaining = overflow;
        while (remaining > 0) {
            int move = Math.min(limit, remaining);
            ItemStack drop = copyWithAmount(template, move);
            if (!player.worldObj.isRemote) {
                player.dropPlayerItemWithRandomChoice(drop, false);
            }
            remaining -= move;
        }
        changed.set(true);
    }

    private boolean stacksMatch(ItemStack stack, ItemStack template) {
        if (!DataChicken.isChicken(stack) || !DataChicken.isChicken(template)) {
            return false;
        }
        if (stack.getItem() != template.getItem()) {
            return false;
        }
        if (stack.getItemDamage() != template.getItemDamage()) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(stack, template);
    }

    private ItemStack copyWithAmount(ItemStack source, int amount) {
        ItemStack copy = source.copy();
        copy.stackSize = amount;
        return copy;
    }
}
