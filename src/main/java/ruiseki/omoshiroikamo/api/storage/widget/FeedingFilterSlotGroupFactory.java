package ruiseki.omoshiroikamo.api.storage.widget;

import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.api.storage.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.api.storage.syncHandler.FoodFilterSlotSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularFilterSlot;

public class FeedingFilterSlotGroupFactory implements IUpgradeSlotGroupFactory {

    @Override
    public void build(UpgradeSlotUpdateGroup group) {

        DelegatedStackHandlerSH commonFilterStackHandler = group.get("common_filter_handler");

        ModularFilterSlot[] slots = new ModularFilterSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("feeding_filters_" + group.slotIndex);
            group.syncManager.syncValue("feeding_filter_" + group.slotIndex, i, new FoodFilterSlotSH(slot));
        }
        group.put("feeding_filter_slots", slots);

        group.syncManager.registerSlotGroup(new SlotGroup("feeding_filters_" + group.slotIndex, 9, false));
    }
}
