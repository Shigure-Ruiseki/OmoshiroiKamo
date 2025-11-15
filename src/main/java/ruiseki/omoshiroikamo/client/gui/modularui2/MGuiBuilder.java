package ruiseki.omoshiroikamo.client.gui.modularui2;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

public class MGuiBuilder {

    public static SlotGroupWidget buildPlayerInventorySlotGroup(IItemHandler playerInventory) {
        return SlotGroupWidget.builder()
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .key('P', i -> new ItemSlot().slot(new ModularSlot(playerInventory, 9 + i).slotGroup("player_inventory")))
            .build();
    }

    public static SlotGroupWidget buildPlayerHotbarSlotGroup(IItemHandler playerInventory) {
        return SlotGroupWidget.builder()
            .row("HHHHHHHHH")
            .key('H', i -> new ItemSlot().slot(new ModularSlot(playerInventory, i).slotGroup("player_inventory")))
            .build();
    }
}
