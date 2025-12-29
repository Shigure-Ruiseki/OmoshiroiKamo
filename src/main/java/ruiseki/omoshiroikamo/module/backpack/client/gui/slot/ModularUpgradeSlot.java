package ruiseki.omoshiroikamo.module.backpack.client.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemInceptionUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemUpgrade;

public class ModularUpgradeSlot extends ModularSlot {

    private final BackpackPanel panel;
    private final BackpackHandler handler;

    public ModularUpgradeSlot(BackpackHandler handler, int index, BackpackPanel panel) {
        super(handler.getUpgradeHandler(), index);
        this.panel = panel;
        this.handler = handler;
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
                return handler.canReplaceStackUpgrade(slotIndex, null);
            }

            if (cursor.getItem() instanceof ItemStackUpgrade) {
                return handler.canReplaceStackUpgrade(slotIndex, cursor);
            }

            return handler.canReplaceStackUpgrade(slotIndex, null);
        }

        if (originalItem instanceof ItemInceptionUpgrade) {

            if (cursorEmpty) {
                return handler.canRemoveInceptionUpgrade();
            }

            if (!(cursor.getItem() instanceof ItemInceptionUpgrade)) {
                return handler.canRemoveInceptionUpgrade();
            }

            return true;
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

        if (item instanceof ItemCraftingUpgrade) {
            return handler.canAddCrafting();
        }

        return item instanceof ItemUpgrade;
    }

}
