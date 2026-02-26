package ruiseki.omoshiroikamo.module.ids.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.inventory.ClickType;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage.StorageTerminalPanel;

public class TerminalContainer extends ModularContainer {

    public final StorageTerminal terminal;
    public final StorageTerminalPanel panel;

    public TerminalContainer(StorageTerminal terminal, StorageTerminalPanel panel) {
        this.terminal = terminal;
        this.panel = panel;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
        if (ClickType.fromNumber(mode) != ClickType.QUICK_MOVE) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        if (player.worldObj.isRemote) {
            return null;
        }

        if (slotId < 0 || slotId >= inventorySlots.size()) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        Slot slot = inventorySlots.get(slotId);

        if (!(slot instanceof ModularSlot modularSlot)) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        if (!slot.getHasStack() || !slot.canTakeStack(player)) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        String group = modularSlot.getSlotGroupName();
        if (!("player_inventory".equals(group) || "craftingMatrix".equals(group))) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        var network = terminal.getItemNetwork();
        if (network == null) {
            return super.slotClick(slotId, mouseButton, mode, player);
        }

        ItemStack original = slot.getStack();
        ItemStack toInsert = original.copy();

        ItemStack remainder = network.insert(toInsert, terminal.getChannel());

        slot.putStack((remainder == null || remainder.stackSize <= 0) ? null : remainder);

        slot.onSlotChanged();
        player.inventory.markDirty();
        detectAndSendChanges();

        return remainder;
    }

}
