package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;

import ruiseki.omoshiroikamo.client.gui.modularui2.SidedInvWrapper;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;

public class BackpackInventoryHelper {

    public static boolean attemptDepositOnTileEntity(BackpackHandler backpackHandler, TileEntity destination,
        ForgeDirection facing) {
        IItemHandler handler = getHandler(destination, facing);
        if (handler == null) {
            return false;
        }
        return attemptDepositOnItemHandler(backpackHandler, handler);
    }

    public static boolean attemptDepositOnEntity(BackpackHandler backpackHandler, Entity destination) {
        IItemHandler handler = getHandler(destination, null);
        if (handler == null) {
            return false;
        }
        return attemptDepositOnItemHandler(backpackHandler, handler);
    }

    public static boolean attemptDepositOnItemHandler(BackpackHandler handler, IItemHandler destination) {
        IItemHandler backpackInventory = handler.getBackpackHandler();
        boolean transferred = false;

        if (isFull(destination)) {
            return false;
        }

        for (int i = 0; i < backpackInventory.getSlots(); i++) {
            if (handler.canDeposit(i)) {
                ItemStack stack = backpackInventory.getStackInSlot(i);

                if (stack == null) {
                    continue;
                }

                ItemStack copiedStack = stack.copy();
                copiedStack = ItemHandlerHelper.insertItemStacked(destination, copiedStack, false);

                if (copiedStack != null && !ItemStack.areItemStacksEqual(stack, copiedStack)) {
                    transferred = true;
                    handler.extractItem(i, stack.stackSize - copiedStack.stackSize, false);
                }
            }
        }

        return transferred;
    }

    public static boolean attemptRestockFromTileEntity(BackpackHandler backpackHandler, TileEntity source,
        ForgeDirection facing) {
        IItemHandler handler = getHandler(source, facing);
        if (handler == null) {
            return false;
        }
        return attemptRestockFromItemHandler(backpackHandler, handler);
    }

    public static boolean attemptRestockFromEntity(BackpackHandler backpackHandler, Entity source) {
        IItemHandler handler = getHandler(source, null);
        if (handler == null) {
            return false;
        }
        return attemptRestockFromItemHandler(backpackHandler, handler);
    }

    public static boolean attemptRestockFromItemHandler(BackpackHandler handler, IItemHandler source) {
        IItemHandler backpackInventory = handler.getBackpackHandler();
        boolean transferred = false;

        if (!(source instanceof IItemHandlerModifiable modifiableSource)) {
            return false;
        }
        if (isFull(backpackInventory)) {
            return false;
        }

        for (int i = 0; i < modifiableSource.getSlots(); i++) {
            ItemStack sourceStack = modifiableSource.getStackInSlot(i);

            if (sourceStack == null) {
                continue;
            }

            ItemStack copiedSourceStack = sourceStack.copy();

            if (handler.canRestock(copiedSourceStack)) {
                copiedSourceStack = ItemHandlerHelper.insertItemStacked(backpackInventory, copiedSourceStack, false);

                if (!ItemStack.areItemStacksEqual(sourceStack, copiedSourceStack)) {
                    transferred = true;
                    modifiableSource.setStackInSlot(i, copiedSourceStack);
                }
            }
        }

        return transferred;
    }

    private static IItemHandler getHandler(Object handler, ForgeDirection facing) {
        if (handler instanceof ISidedInventory) {
            return new SidedInvWrapper((ISidedInventory) handler, facing);
        } else if (handler instanceof IInventory) {
            return new InvWrapper((IInventory) handler);
        } else if (handler instanceof IItemHandler) {
            return (IItemHandler) handler;
        }
        return null;
    }

    private static boolean isFull(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack == null || stack.stackSize != handler.getSlotLimit(i)) {
                return false;
            }
        }
        return true;
    }

}
