package ruiseki.omoshiroikamo.module.ids.integration.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import codechicken.nei.FastTransferManager;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.GuiOverlayButton;
import codechicken.nei.recipe.IRecipeHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ruiseki.omoshiroikamo.core.client.gui.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;
import ruiseki.omoshiroikamo.core.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.module.ids.client.gui.container.TerminalContainer;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.ItemIndexClient;

public class TerminalOverlay implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {
        transferRecipe(firstGui, recipe, recipeIndex, maxTransfer ? Integer.MAX_VALUE : 1);
    }

    @Override
    public int transferRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, int multiplier) {
        if (!(gui.inventorySlots instanceof TerminalContainer container)) {
            return 0;
        }

        ItemIndexClient index = container.panel.clientIndex;
        if (index == null) return 0;
        clearGridIntoNetwork(gui, container);
        if (!waitUntilGridCleared(gui, 100)) {
            return 0;
        }

        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

        Object2IntOpenHashMap<ItemStackKey> used = new Object2IntOpenHashMap<>();
        for (PositionedStack ps : ingredients) {
            if (ps == null || !consumeIfEnough(index, ps.item, used)) {
                return 0;
            }
        }

        Slot[][] slotMap = mapIngredSlots(gui, ingredients);
        EntityClientPlayerMP player = NEIClientUtils.mc().thePlayer;

        for (int i = 0; i < ingredients.size(); i++) {
            PositionedStack ps = ingredients.get(i);
            if (ps == null || ps.item == null) continue;
            if (slotMap[i] == null || slotMap[i].length == 0) continue;

            Slot target = slotMap[i][0];
            if (!target.isItemValid(ps.item)) continue;

            container.panel.syncHandler.extractToMouse(ps.item.copy(), ps.item.stackSize);
            FastTransferManager.clickSlot(gui, target.slotNumber);

            if (player.inventory.getItemStack() != null) {
                FastTransferManager.clickSlot(gui, target.slotNumber);
            }
        }

        return 1;
    }

    private void clearGridIntoNetwork(GuiContainer gui, TerminalContainer container) {
        EntityClientPlayerMP player = NEIClientUtils.mc().thePlayer;

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (!(slot instanceof ModularCraftingMatrixSlot matrix) || !matrix.isActive()) {
                continue;
            }

            if (!slot.getHasStack()) continue;

            FastTransferManager.clickSlot(gui, slot.slotNumber);

            ItemStack mouse = player.inventory.getItemStack();
            if (mouse == null) continue;

            container.panel.syncHandler.insertFromMouse();

            if (player.inventory.getItemStack() != null) {
                FastTransferManager.clickSlot(gui, slot.slotNumber);
            }
        }
    }

    private boolean waitUntilGridCleared(GuiContainer gui, int timeoutMs) {
        long end = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < end) {
            NEIClientUtils.mc().playerController.updateController();

            if (isCraftingGridCleared(gui)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCraftingGridCleared(GuiContainer gui) {
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof ModularCraftingMatrixSlot matrix && matrix.isActive() && slot.getHasStack()) {

                return false;
            }
        }
        return true;
    }

    private Slot[][] mapIngredSlots(GuiContainer gui, List<PositionedStack> ingredients) {
        Slot[][] recipeSlotList = new Slot[ingredients.size()][];

        List<Slot> craftingSlots = new ArrayList<>();
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot instanceof ModularCraftingMatrixSlot matrix && matrix.isActive()) {
                craftingSlots.add(slot);
            }
        }

        int startX = 25;
        int startY = 6;
        int slotSize = 18;

        for (int i = 0; i < ingredients.size(); i++) {
            PositionedStack ps = ingredients.get(i);
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
    public List<GuiOverlayButton.ItemOverlayState> presenceOverlay(GuiContainer gui, IRecipeHandler recipe,
        int recipeIndex) {
        List<GuiOverlayButton.ItemOverlayState> result = new ArrayList<>();

        if (!(gui.inventorySlots instanceof TerminalContainer container)) {
            return result;
        }

        ItemIndexClient index = container.panel.clientIndex;
        if (index == null) return result;

        Object2IntOpenHashMap<ItemStackKey> used = new Object2IntOpenHashMap<>();

        for (PositionedStack ps : recipe.getIngredientStacks(recipeIndex)) {
            boolean present = ps != null && consumeIfEnough(index, ps.item, used);

            result.add(new GuiOverlayButton.ItemOverlayState(ps, present));
        }

        return result;
    }

    private boolean consumeIfEnough(ItemIndexClient index, ItemStack stack, Object2IntOpenHashMap<ItemStackKey> used) {
        if (stack == null) return false;

        ItemStackKey key = ItemStackKeyPool.get(stack);
        int need = stack.stackSize;
        int available = index.getStored(key) - used.getOrDefault(key, 0);

        if (available < need) return false;

        used.addTo(key, need);
        return true;
    }

}
