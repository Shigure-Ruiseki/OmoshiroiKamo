package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularFilterSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.FilterSlotSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.FoodFilterSlotSH;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.IAdvancedFilterable;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.IBasicFilterable;

public class UpgradeSlotUpdateGroup {

    private final BackpackPanel panel;
    private final BackpackHandler handler;
    private final int slotIndex;

    // Common filters
    public DelegatedStackHandlerSH commonFilterStackHandler;
    public ModularSlot[] commonFilterSlots;

    // Advanced common filters
    public DelegatedStackHandlerSH advancedCommonFilterStackHandler;
    public ModularSlot[] advancedCommonFilterSlots;

    // Feeding filters
    public ModularSlot[] feedingFilterSlots;
    public ModularSlot[] advancedFeedingFilterSlots;

    public UpgradeSlotUpdateGroup(BackpackPanel panel, BackpackHandler handler, int slotIndex) {
        this.panel = panel;
        this.handler = handler;
        this.slotIndex = slotIndex;

        var syncManager = panel.getSyncManager();

        // COMMON FILTER
        this.commonFilterStackHandler = new DelegatedStackHandlerSH(handler, slotIndex);
        syncManager.syncValue("common_filter_delegation_" + slotIndex, commonFilterStackHandler);

        this.commonFilterSlots = new ModularSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.getDelegatedStackHandler(), i);
            slot.slotGroup("common_filters_" + slotIndex);

            syncManager.syncValue("common_filter_" + slotIndex, i, new FilterSlotSH(slot));

            commonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("common_filters_" + slotIndex, 9, false));

        // ADVANCED COMMON FILTER
        this.advancedCommonFilterStackHandler = new DelegatedStackHandlerSH(handler, slotIndex);
        syncManager.syncValue("adv_common_filter_delegation_" + slotIndex, advancedCommonFilterStackHandler);

        this.advancedCommonFilterSlots = new ModularSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(
                advancedCommonFilterStackHandler.getDelegatedStackHandler(),
                i);
            slot.slotGroup("adv_common_filters_" + slotIndex);

            syncManager.syncValue("adv_common_filter_" + slotIndex, i, new FilterSlotSH(slot));

            advancedCommonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("adv_common_filters_" + slotIndex, 16, false));

        // FEEDING FILTER
        this.feedingFilterSlots = new ModularSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.getDelegatedStackHandler(), i);
            slot.slotGroup("feeding_filters_" + slotIndex);

            syncManager.syncValue("feeding_filter_" + slotIndex, i, new FoodFilterSlotSH(slot));

            feedingFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("feeding_filters_" + slotIndex, 9, false));

        // ADVANCED FEEDING FILTER
        this.advancedFeedingFilterSlots = new ModularSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(
                advancedCommonFilterStackHandler.getDelegatedStackHandler(),
                i);
            slot.slotGroup("adv_feeding_filters_" + slotIndex);

            syncManager.syncValue("adv_feeding_filter_" + slotIndex, i, new FoodFilterSlotSH(slot));

            advancedFeedingFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("adv_feeding_filters_" + slotIndex, 16, false));
    }

    public void updateFilterDelegate(IBasicFilterable wrapper) {
        commonFilterStackHandler.setDelegatedStackHandler(wrapper::getFilterItems);
        commonFilterStackHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);
    }

    public void updateAdvancedFilterDelegate(IAdvancedFilterable wrapper) {
        advancedCommonFilterStackHandler.setDelegatedStackHandler(wrapper::getFilterItems);
        advancedCommonFilterStackHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);
    }

}
