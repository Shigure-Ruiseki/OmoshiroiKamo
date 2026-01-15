package ruiseki.omoshiroikamo.module.cable.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.inventory.ClickType;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.cable.common.network.terminal.CableTerminal;

public class TerminalContainer extends ModularContainer {

    public final CableTerminal terminal;

    public TerminalContainer(CableTerminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
        ClickType clickType = ClickType.fromNumber(mode);
        if (clickType == ClickType.QUICK_MOVE) {
            if (slotId < 0 || slotId >= inventorySlots.size()) {
                return null;
            }

            Slot slot = inventorySlots.get(slotId);
            if (!(slot instanceof ModularSlot modularSlot)) {
                return null;
            }

            String group = modularSlot.getSlotGroupName();
            if (!"player_inventory".equals(group) && !"craftingMatrix".equals(group)) {
                return null;
            }

            if (!slot.getHasStack() || !slot.canTakeStack(player)) {
                return null;
            }

            if (terminal.getItemNetwork() == null) {
                return null;
            }

            ItemStack stack = slot.getStack();
            ItemStack remainder = terminal.getItemNetwork()
                .insert(stack);
            slot.putStack(remainder);
            slot.onSlotChanged();
            player.inventory.markDirty();
            detectAndSendChanges();
            return remainder;
        }

        return super.slotClick(slotId, mouseButton, mode, player);
    }
}
