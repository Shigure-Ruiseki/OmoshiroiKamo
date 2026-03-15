package ruiseki.omoshiroikamo.module.backpack.integration.nei;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.GuiOverlayButton;
import codechicken.nei.recipe.IRecipeHandler;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackPackContainer;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.IndexedModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.IndexedModularCraftingSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularFilterSlot;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperFactory;

public class BackpackOverlay extends DefaultOverlayHandler {

    public BackpackOverlay() {
        super(0, 0);
    }

    @Override
    public boolean canMoveFrom(Slot slot, GuiContainer gui) {
        return !(slot instanceof IndexedModularCraftingSlot) && !(slot instanceof ModularFilterSlot);
    }

    @Override
    protected Set<Slot> getCraftMatrixSlots(GuiContainer gui, IRecipeHandler handler) {

        final Set<Slot> ingredSlots = new HashSet<>();

        if (!(gui.inventorySlots instanceof BackPackContainer container)) {
            return ingredSlots;
        }

        BackpackWrapper wrapper = container.wrapper;

        int activeUpgrade = -1;

        for (int i = 0; i < wrapper.getUpgradeHandler()
            .getSlots(); i++) {

            UpgradeWrapper upgrade = UpgradeWrapperFactory.createWrapper(
                wrapper.getUpgradeHandler()
                    .getStackInSlot(i));

            if (upgrade != null && upgrade.isTabOpened()) {
                activeUpgrade = i;
                break;
            }
        }

        if (activeUpgrade == -1) {
            return ingredSlots;
        }

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof IndexedModularCraftingMatrixSlot matrix
                && matrix.getUpgradeSlotIndex() == activeUpgrade) {

                ingredSlots.add(slot);
            }
        }

        return ingredSlots;
    }

    @Override
    public Slot[][] mapIngredSlots(GuiContainer gui, List<PositionedStack> ingredients) {

        Slot[][] recipeSlotList = new Slot[ingredients.size()][];

        for (int i = 0; i < ingredients.size(); i++) {
            recipeSlotList[i] = new Slot[0];
        }

        if (!(gui.inventorySlots instanceof BackPackContainer container)) {
            return recipeSlotList;
        }

        BackpackWrapper wrapper = container.wrapper;

        int activeUpgrade = -1;

        for (int i = 0; i < wrapper.getUpgradeHandler()
            .getSlots(); i++) {

            UpgradeWrapper upgrade = UpgradeWrapperFactory.createWrapper(
                wrapper.getUpgradeHandler()
                    .getStackInSlot(i));

            if (upgrade != null && upgrade.isTabOpened()) {
                activeUpgrade = i;
                break;
            }
        }

        if (activeUpgrade == -1) {
            return recipeSlotList;
        }

        List<Slot> craftingSlots = new ArrayList<>();

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof IndexedModularCraftingMatrixSlot matrix
                && matrix.getUpgradeSlotIndex() == activeUpgrade) {

                craftingSlots.add(slot);
            }
        }

        int startX = 25;
        int startY = 6;
        int slotSize = 18;

        for (int i = 0; i < ingredients.size(); i++) {

            PositionedStack ps = ingredients.get(i);
            if (ps == null) continue;

            int col = (ps.relx - startX) / slotSize;
            int row = (ps.rely - startY) / slotSize;

            int index = row * 3 + col;

            if (index >= 0 && index < craftingSlots.size()) {
                recipeSlotList[i] = new Slot[] { craftingSlots.get(index) };
            }
        }

        return recipeSlotList;
    }

    @Override
    public List<GuiOverlayButton.ItemOverlayState> presenceOverlay(GuiContainer firstGui, IRecipeHandler recipe,
        int recipeIndex) {

        final List<GuiOverlayButton.ItemOverlayState> itemPresenceSlots = new ArrayList<>();
        final List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

        if (!(firstGui.inventorySlots instanceof BackPackContainer container)) {
            return itemPresenceSlots;
        }

        BackpackWrapper wrapper = container.wrapper;
        EntityPlayer player = container.getPlayer();

        final List<ItemStack> invStacks = new ArrayList<>();

        // backpack inventory
        for (int i = 0; i < wrapper.getSlots(); i++) {
            ItemStack stack = wrapper.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                invStacks.add(stack.copy());
            }
        }

        // player inventory
        if (player != null) {
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack != null && stack.stackSize > 0) {
                    invStacks.add(stack.copy());
                }
            }
        }

        for (PositionedStack stack : ingredients) {

            ItemStack used = null;

            for (ItemStack is : invStacks) {
                if (is.stackSize > 0 && stack.contains(is)) {
                    used = is;
                    break;
                }
            }

            itemPresenceSlots.add(new GuiOverlayButton.ItemOverlayState(stack, used != null));

            if (used != null) {
                used.stackSize -= 1;
            }
        }

        return itemPresenceSlots;
    }
}
