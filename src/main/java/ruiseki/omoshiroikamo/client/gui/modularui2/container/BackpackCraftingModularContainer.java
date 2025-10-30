package ruiseki.omoshiroikamo.client.gui.modularui2.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.CraftingManager;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

public class BackpackCraftingModularContainer extends BackPackContainer {

    private final InventoryCraftingWrapper craftMatrix;
    private ModularCraftingSlot craftingSlot;

    public BackpackCraftingModularContainer(int width, int height, IItemHandlerModifiable craftingInventory) {
        this(width, height, craftingInventory, 0);
    }

    public BackpackCraftingModularContainer(int width, int height, IItemHandlerModifiable craftingInventory,
        int startIndex) {
        super();
        this.craftMatrix = new InventoryCraftingWrapper(this, width, height, craftingInventory, startIndex);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        this.craftMatrix.detectChanges();
    }

    @Override
    public void registerSlot(String panelName, ModularSlot slot) {
        super.registerSlot(panelName, slot);
        if (slot instanceof ModularCraftingSlot craftingSlot1) {
            if (this.craftingSlot != null && this.craftingSlot != craftingSlot1) {
                throw new IllegalStateException(
                    "Only one crafting output slot is supported with CraftingModularContainer!");
            }
            this.craftingSlot = craftingSlot1;
            craftingSlot1.setCraftMatrix(this.craftMatrix);
        }
    }

    @Override
    public void onCraftMatrixChanged(@NotNull IInventory inventoryIn) {
        if (!getGuiData().isClient()) {
            this.craftingSlot.updateResult(
                CraftingManager.getInstance()
                    .findMatchingRecipe(this.craftMatrix, getPlayer().worldObj));
        }
    }
}
