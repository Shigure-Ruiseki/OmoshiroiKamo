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

    public static SlotGroupWidget playerInventory(IItemHandler inv) {
        return playerInventory(inv, true);
    }

    public static SlotGroupWidget playerInventory(IItemHandler inv, boolean positioned) {
        return positioned ? buildFullPlayerInventory(inv, 7, true) : buildFullPlayerInventory(inv);
    }

    public static SlotGroupWidget buildFullPlayerInventory(IItemHandler inv, int bottom, boolean horizontalCentered) {
        SlotGroupWidget widget = buildFullPlayerInventory(inv);
        if (bottom != 0) {
            widget.bottom(bottom);
        }
        if (horizontalCentered) {
            widget.leftRel(0.5f);
        }
        return widget;
    }

    public static SlotGroupWidget buildFullPlayerInventory(IItemHandler inv) {
        SlotGroupWidget g = new SlotGroupWidget();
        g.coverChildren();
        g.name("player_inventory");

        // inventory (27)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = 9 + row * 9 + col;
                g.child(
                    new ItemSlot().slot(new ModularSlot(inv, idx).slotGroup("player_inventory"))
                        .pos(col * 18, row * 18));
            }
        }

        // hotbar (9)
        for (int i = 0; i < 9; i++) {
            g.child(
                new ItemSlot().slot(new ModularSlot(inv, i).slotGroup("player_inventory"))
                    .pos(i * 18, 3 * 18 + 4));
        }

        return g;
    }

}
