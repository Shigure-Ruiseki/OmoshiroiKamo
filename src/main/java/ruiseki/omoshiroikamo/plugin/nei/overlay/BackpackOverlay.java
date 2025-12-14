package ruiseki.omoshiroikamo.plugin.nei.overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.nei.FastTransferManager;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.StackInfo;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularCraftingSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularFilterSlot;

public class BackpackOverlay implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {
        transferRecipe(firstGui, recipe, recipeIndex, maxTransfer ? Integer.MAX_VALUE : 1);
    }

    @Override
    public int transferRecipe(GuiContainer gui, IRecipeHandler handler, int recipeIndex, int multiplier) {
        final List<PositionedStack> recipeIngredients = handler.getIngredientStacks(recipeIndex);
        final List<DefaultOverlayHandler.DistributedIngred> inventoryDistribute = getPermutationIngredients(
            recipeIngredients);

        if (!clearIngredients(gui)) return 0;

        findInventoryQuantities(gui, inventoryDistribute);

        final List<DefaultOverlayHandler.IngredientDistribution> assignedIngredients = assignIngredients(
            recipeIngredients,
            inventoryDistribute);
        if (assignedIngredients == null) return 0;

        assignIngredSlots(gui, recipeIngredients, assignedIngredients);
        multiplier = Math.min(multiplier == 0 ? 64 : multiplier, calculateRecipeQuantity(assignedIngredients));

        moveIngredients(gui, assignedIngredients, Math.max(1, multiplier));

        return assignedIngredients.stream()
            .anyMatch(distrib -> distrib.distrib.distributed == 0) ? 0 : multiplier;
    }

    private List<DefaultOverlayHandler.DistributedIngred> getPermutationIngredients(List<PositionedStack> ingredients) {
        ArrayList<DefaultOverlayHandler.DistributedIngred> ingredStacks = new ArrayList<>();
        for (PositionedStack posstack : ingredients) /* work out what we need */ {
            for (ItemStack pstack : posstack.items) {
                DefaultOverlayHandler.DistributedIngred istack = findIngred(ingredStacks, pstack);
                if (istack == null) ingredStacks.add(istack = new DefaultOverlayHandler.DistributedIngred(pstack));
                istack.recipeAmount += pstack.stackSize;
            }
        }
        return ingredStacks;
    }

    public DefaultOverlayHandler.DistributedIngred findIngred(
        List<DefaultOverlayHandler.DistributedIngred> ingredStacks, ItemStack pstack) {
        for (DefaultOverlayHandler.DistributedIngred istack : ingredStacks)
            if (canStack(istack.stack, pstack)) return istack;
        return null;
    }

    protected boolean canStack(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) return true;
        return NEIClientUtils.areStacksSameTypeCraftingWithNBT(stack1, stack2);
    }

    private void findInventoryQuantities(GuiContainer gui, List<DefaultOverlayHandler.DistributedIngred> ingredStacks) {
        for (Slot slot : gui.inventorySlots.inventorySlots)
        /* work out how much we have to go round */ {
            if (slot instanceof ModularCraftingMatrixSlot) continue;
            if (slot.getHasStack() && canMoveFrom(slot, gui)) {
                final ItemStack pstack = slot.getStack();
                final DefaultOverlayHandler.DistributedIngred istack = findIngred(ingredStacks, pstack);

                if (istack != null) {
                    istack.invAmount += pstack.stackSize;

                    if (!istack.isContainerItem && pstack.getMaxStackSize() == 1
                        && pstack.getItem()
                            .hasContainerItem(pstack)) {
                        final NBTTagCompound tagCompound = pstack.getTagCompound();

                        if (tagCompound != null && tagCompound.hasKey("GT.ToolStats")) {
                            istack.isContainerItem = true;
                        } else {
                            final boolean isPausedItemDamageSound = StackInfo.isPausedItemDamageSound();
                            StackInfo.pauseItemDamageSound(true);

                            final ItemStack containerItem = pstack.getItem()
                                .getContainerItem(pstack);
                            if (containerItem != null) {
                                istack.isContainerItem = pstack.getItem() == containerItem.getItem();
                            }

                            StackInfo.pauseItemDamageSound(isPausedItemDamageSound);
                        }
                    }
                }
            }
        }
    }

    private List<DefaultOverlayHandler.IngredientDistribution> assignIngredients(List<PositionedStack> ingredients,
        List<DefaultOverlayHandler.DistributedIngred> ingredStacks) {
        ArrayList<DefaultOverlayHandler.IngredientDistribution> assignedIngredients = new ArrayList<>();
        for (PositionedStack posstack : ingredients) // assign what we need and have
        {
            DefaultOverlayHandler.DistributedIngred biggestIngred = null;
            ItemStack permutation = null;
            int biggestSize = 0;
            for (ItemStack pstack : posstack.items) {
                for (DefaultOverlayHandler.DistributedIngred istack : ingredStacks) {
                    if (!canStack(pstack, istack.stack) || istack.invAmount - istack.distributed < pstack.stackSize
                        || istack.recipeAmount == 0
                        || pstack.stackSize == 0) continue;

                    int relsize = (istack.invAmount - istack.invAmount / istack.recipeAmount * istack.distributed)
                        / pstack.stackSize;
                    if (relsize > biggestSize) {
                        biggestSize = relsize;
                        biggestIngred = istack;
                        permutation = pstack;
                        break;
                    }
                }
            }

            if (biggestIngred == null) {
                biggestIngred = new DefaultOverlayHandler.DistributedIngred(posstack.item);
                permutation = InventoryUtils.copyStack(posstack.item, 0);
            }

            biggestIngred.distributed += permutation.stackSize;
            assignedIngredients.add(new DefaultOverlayHandler.IngredientDistribution(biggestIngred, permutation));
        }

        return assignedIngredients;
    }

    private int calculateRecipeQuantity(List<DefaultOverlayHandler.IngredientDistribution> assignedIngredients) {
        int quantity = Integer.MAX_VALUE;

        for (DefaultOverlayHandler.IngredientDistribution distrib : assignedIngredients) {
            final DefaultOverlayHandler.DistributedIngred istack = distrib.distrib;
            if (istack.distributed == 0) continue;
            if (istack.numSlots == 0) return 0;

            final int maxStackSize = istack.stack.getMaxStackSize();
            if (maxStackSize == 1 && istack.isContainerItem) {
                continue;
            }

            final int allSlots = Math.min(istack.invAmount, istack.numSlots * maxStackSize);
            quantity = Math.min(quantity, allSlots / istack.distributed);
        }

        if (quantity == Integer.MAX_VALUE) {
            quantity = 1;
        }

        return quantity;
    }

    private Slot[][] assignIngredSlots(GuiContainer gui, List<PositionedStack> ingredients,
        List<DefaultOverlayHandler.IngredientDistribution> assignedIngredients) {

        Slot[][] recipeSlots = mapIngredSlots(gui, ingredients); // setup the slot map
        boolean hasAnySlot = false;
        for (Slot[] slots : recipeSlots) {
            if (slots != null && slots.length > 0) {
                hasAnySlot = true;
                break;
            }
        }

        if (!hasAnySlot) {
            return null;
        }

        HashMap<Slot, Integer> distribution = new HashMap<>();
        for (Slot[] recipeSlot : recipeSlots)
            for (Slot slot : recipeSlot) if (!distribution.containsKey(slot)) distribution.put(slot, -1);

        HashSet<Slot> avaliableSlots = new HashSet<>(distribution.keySet());
        HashSet<Integer> remainingIngreds = new HashSet<>();
        ArrayList<LinkedList<Slot>> assignedSlots = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            remainingIngreds.add(i);
            assignedSlots.add(new LinkedList<>());
        }

        while (!avaliableSlots.isEmpty() && !remainingIngreds.isEmpty()) {
            for (Iterator<Integer> iterator = remainingIngreds.iterator(); iterator.hasNext();) {
                int i = iterator.next();
                boolean assigned = false;
                DefaultOverlayHandler.DistributedIngred istack = assignedIngredients.get(i).distrib;

                for (Slot slot : recipeSlots[i]) {
                    if (avaliableSlots.contains(slot)) {
                        avaliableSlots.remove(slot);
                        if (slot.getHasStack()) continue;

                        istack.numSlots++;
                        assignedSlots.get(i)
                            .add(slot);
                        assigned = true;
                        break;
                    }
                }

                if (!assigned || istack.numSlots * istack.stack.getMaxStackSize() >= istack.invAmount) {
                    iterator.remove();
                }
            }
        }

        for (int i = 0; i < ingredients.size(); i++) {
            assignedIngredients.get(i).slots = assignedSlots.get(i)
                .toArray(new Slot[0]);
        }

        return recipeSlots;
    }

    private Slot[][] mapIngredSlots(GuiContainer gui, List<PositionedStack> ingredients) {
        Slot[][] recipeSlotList = new Slot[ingredients.size()][];

        List<Slot> availableSlots = new ArrayList<>();
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof ModularCraftingMatrixSlot craftingSlot && craftingSlot.isActive()) {
                availableSlots.add(slot);
            }
        }

        int startX = 25;
        int startY = 6;
        int slotWidth = 18;
        int slotHeight = 18;

        for (int i = 0; i < ingredients.size(); i++) {
            PositionedStack ps = ingredients.get(i);
            int index = getSlotIndex(ps, startX, startY, slotWidth, slotHeight);
            if (index >= 0 && index < availableSlots.size()) {
                recipeSlotList[i] = new Slot[] { availableSlots.get(index) };
            }
        }

        return recipeSlotList;
    }

    private int getSlotIndex(PositionedStack ps, int startX, int startY, int slotWidth, int slotHeight) {
        int col = (ps.relx - startX) / slotWidth;
        int row = (ps.rely - startY) / slotHeight;
        return row * 3 + col;
    }

    private void moveIngredients(GuiContainer gui,
        List<DefaultOverlayHandler.IngredientDistribution> assignedIngredients, int multiplier) {
        final EntityClientPlayerMP thePlayer = NEIClientUtils.mc().thePlayer;

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof ModularCraftingMatrixSlot matrixSlot && matrixSlot.isActive() || !slot.getHasStack()
                || !canMoveFrom(slot, gui)
                || !slot.canTakeStack(thePlayer)) continue;

            ItemStack stack = slot.getStack();
            int slotTransferCap = stack.getMaxStackSize();

            for (DefaultOverlayHandler.IngredientDistribution distrib : assignedIngredients) {
                if (distrib.slots == null || distrib.slots.length == 0
                    || !slot.getHasStack()
                    || !canStack(distrib.permutation, stack)) continue;

                int transferCap = Math.min(slotTransferCap, multiplier * distrib.permutation.stackSize);
                int stackSize = slot.getStack().stackSize;
                boolean pickup = false;

                for (Slot dest : distrib.slots) {
                    int destCurrent = dest.getHasStack() ? dest.getStack().stackSize : 0;
                    int amount = Math.min(transferCap - destCurrent, stackSize);

                    ItemStack item = stack.copy();
                    item.stackSize = amount;

                    if (stackSize <= amount) {
                        if (!pickup) {
                            FastTransferManager.clickSlot(gui, slot.slotNumber);
                        }

                        FastTransferManager.clickSlot(gui, dest.slotNumber);
                        break;
                    } else {
                        for (int c = 0; c < amount; c++) {
                            if (pickup != (pickup = true)) {
                                FastTransferManager.clickSlot(gui, slot.slotNumber);
                            }

                            FastTransferManager.clickSlot(gui, dest.slotNumber, 1);
                            stackSize--;
                        }
                    }
                }

                if (thePlayer.inventory.getItemStack() != null) {
                    FastTransferManager.clickSlot(gui, slot.slotNumber);
                }
            }
        }
    }

    private boolean canMoveFrom(Slot slot, GuiContainer gui) {
        return slot instanceof ModularSlot && !(slot instanceof ModularFilterSlot);
    }

    private boolean clearIngredients(GuiContainer gui) {
        final EntityClientPlayerMP thePlayer = NEIClientUtils.mc().thePlayer;

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof ModularCraftingSlot) continue;
            if (!(slot instanceof ModularCraftingMatrixSlot matrixSlot)) continue;
            if (!matrixSlot.isActive()) continue;
            if (!slot.getHasStack()) continue;
            if (!canMoveFrom(slot, gui)) continue;
            if (!slot.canTakeStack(thePlayer)) continue;
            FastTransferManager.clickSlot(gui, slot.slotNumber, 0, 1);
            if (slot.getHasStack()) {
                return false;
            }
        }

        return dropOffMouseStack(thePlayer, gui);
    }

    private boolean dropOffMouseStack(EntityPlayer entityPlayer, GuiContainer gui) {
        if (entityPlayer.inventory.getItemStack() == null) {
            return true;
        }

        for (int i = 0; i < gui.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = gui.inventorySlots.inventorySlots.get(i);

            if (slot.inventory == entityPlayer.inventory) {
                ItemStack mouseItem = entityPlayer.inventory.getItemStack();
                ItemStack slotStack = slot.getStack();

                if (slotStack == null || NEIClientUtils.areStacksSameType(mouseItem, slotStack)) {
                    FastTransferManager.clickSlot(gui, i, 0, 0);
                }

                if (entityPlayer.inventory.getItemStack() == null) {
                    return true;
                }
            }
        }

        return entityPlayer.inventory.getItemStack() == null;
    }
}
