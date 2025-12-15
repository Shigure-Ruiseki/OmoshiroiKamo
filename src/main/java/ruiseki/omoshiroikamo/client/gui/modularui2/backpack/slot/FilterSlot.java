package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.FilterSlotSH;

public class FilterSlot extends ItemSlot implements RecipeViewerGhostIngredientSlot<ItemStack> {

    private FilterSlotSH syncHandler;

    @Override
    protected void setSyncHandler(@Nullable SyncHandler syncHandler) {
        super.setSyncHandler(syncHandler);
        this.syncHandler = castIfTypeElseNull(syncHandler, FilterSlotSH.class);
    }

    @Override
    public boolean handleDragAndDrop(@NotNull ItemStack draggedStack, int button) {
        if (!areAncestorsEnabled() || !this.syncHandler.isItemValid(draggedStack)) return false;
        draggedStack.stackSize = 0;
        this.syncHandler.updateFromClient(draggedStack);
        return true;
    }
}
