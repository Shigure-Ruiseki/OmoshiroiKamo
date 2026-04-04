package ruiseki.omoshiroikamo.api.storage.widget;

import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.api.storage.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.api.storage.syncHandler.FoodFilterSlotSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularFilterSlot;

public class AdvancedFeedingFilterSlotGroupFactory implements IUpgradeSlotGroupFactory {

    @Override
    public void build(UpgradeSlotUpdateGroup group) {

        DelegatedStackHandlerSH commonFilterStackHandler = group.get("adv_common_filter_handler");

        ModularFilterSlot[] slots = new ModularFilterSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("adv_feeding_filters_" + group.slotIndex);
            group.syncManager.syncValue("adv_feeding_filter_" + group.slotIndex, i, new FoodFilterSlotSH(slot));
        }
        group.put("adv_feeding_filter_slots", slots);

        group.syncManager.registerSlotGroup(new SlotGroup("adv_feeding_filters_" + group.slotIndex, 9, false));
    }
}
