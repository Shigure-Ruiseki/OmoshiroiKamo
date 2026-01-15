package ruiseki.omoshiroikamo.module.cable.client.gui.container;

import java.util.Objects;

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
            Slot slot = inventorySlots.get(slotId);
            if (!(slot instanceof ModularSlot modularSlot)) {
                return null;
            }

            if (!Objects.equals(modularSlot.getSlotGroupName(), "player_inventory")) {
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
