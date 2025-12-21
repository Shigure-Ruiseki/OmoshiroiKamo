package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.widget;

import static ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures.DML_INVENTORY_SLOT;

import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

public class InventoryWidget extends SlotGroupWidget {

    public static SlotGroupWidget playerInventory(boolean positioned) {
        return positioned ? playerInventory(7, true) : playerInventory((index, slot) -> slot);
    }

    public static SlotGroupWidget playerInventory(int bottom, boolean horizontalCentered) {
        return playerInventory(bottom, horizontalCentered, (index, slot) -> slot);
    }

    public static SlotGroupWidget playerInventory(int bottom, boolean horizontalCentered, SlotConsumer slotConsumer) {
        SlotGroupWidget widget = playerInventory(slotConsumer);
        if (bottom != 0) widget.bottom(bottom);
        if (horizontalCentered) widget.leftRel(0.5f);
        return widget;
    }

    public static SlotGroupWidget playerInventory(SlotConsumer slotConsumer) {
        SlotGroupWidget slotGroupWidget = new SlotGroupWidget();
        slotGroupWidget.coverChildren();
        slotGroupWidget.name("player_inventory");
        String key = "player";
        for (int i = 0; i < 9; i++) {
            slotGroupWidget.child(
                slotConsumer.apply(
                    i,
                    new ItemSlot().background(DML_INVENTORY_SLOT)
                        .hoverBackground(DML_INVENTORY_SLOT))
                    .syncHandler(key, i)
                    .pos(i * 18, 3 * 18 + 4)
                    .name("slot_" + i));
        }
        for (int i = 0; i < 27; i++) {
            slotGroupWidget.child(
                slotConsumer.apply(
                    i + 9,
                    new ItemSlot().background(DML_INVENTORY_SLOT)
                        .hoverBackground(DML_INVENTORY_SLOT))
                    .syncHandler(key, i + 9)
                    .pos(i % 9 * 18, i / 9 * 18)
                    .name("slot_" + (i + 9)));
        }
        return slotGroupWidget;
    }

}
