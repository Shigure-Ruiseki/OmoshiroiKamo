package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ModularCraftingSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.module.storage.client.gui.slot.CraftingSlotInfo;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.IndexedModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.IndexedModularCraftingSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.ModularFilterSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.DelegatedCraftingStackHandlerSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.FilterSlotSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.FoodFilterSlotSH;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IAdvancedFilterable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IStorageUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class UpgradeSlotUpdateGroup {

    private final StoragePanel panel;
    private final StorageWrapper wrapper;
    private final int slotIndex;

    // Common filters
    public DelegatedStackHandlerSH commonFilterStackHandler;
    public ModularFilterSlot[] commonFilterSlots;

    // Advanced common filters
    public DelegatedStackHandlerSH advancedCommonFilterStackHandler;
    public ModularFilterSlot[] advancedCommonFilterSlots;

    // Feeding filters
    public ModularFilterSlot[] feedingFilterSlots;
    public ModularFilterSlot[] advancedFeedingFilterSlots;

    // Crafting
    public DelegatedStackHandlerSH craftingStackHandler;
    public ModularSlot[] craftingMatrixSlots;
    public ModularCraftingSlot craftingOutputSlot;

    public CraftingSlotInfo craftingInfo;

    public UpgradeSlotUpdateGroup(StoragePanel panel, StorageWrapper wrapper, int slotIndex) {
        this.panel = panel;
        this.wrapper = wrapper;
        this.slotIndex = slotIndex;

        PanelSyncManager syncManager = panel.syncManager;

        // COMMON FILTER
        this.commonFilterStackHandler = new DelegatedStackHandlerSH(wrapper, slotIndex, 9);
        syncManager.syncValue("common_filter_delegation_" + slotIndex, commonFilterStackHandler);

        this.commonFilterSlots = new ModularFilterSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("common_filters_" + slotIndex);
            syncManager.syncValue("common_filter_" + slotIndex, i, new FilterSlotSH(slot));
            commonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("common_filters_" + slotIndex, 9, false));

        // ADVANCED COMMON FILTER
        this.advancedCommonFilterStackHandler = new DelegatedStackHandlerSH(wrapper, slotIndex, 16);
        syncManager.syncValue("adv_common_filter_delegation_" + slotIndex, advancedCommonFilterStackHandler);

        this.advancedCommonFilterSlots = new ModularFilterSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(advancedCommonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("adv_common_filters_" + slotIndex);
            syncManager.syncValue("adv_common_filter_" + slotIndex, i, new FilterSlotSH(slot));

            advancedCommonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("adv_common_filters_" + slotIndex, 16, false));

        // FEEDING FILTER
        this.feedingFilterSlots = new ModularFilterSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("feeding_filters_" + slotIndex);
            syncManager.syncValue("feeding_filter_" + slotIndex, i, new FoodFilterSlotSH(slot));

            feedingFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("feeding_filters_" + slotIndex, 9, false));

        // ADVANCED FEEDING FILTER
        this.advancedFeedingFilterSlots = new ModularFilterSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(advancedCommonFilterStackHandler.delegatedStackHandler, i);
            slot.slotGroup("adv_feeding_filters_" + slotIndex);
            syncManager.syncValue("adv_feeding_filter_" + slotIndex, i, new FoodFilterSlotSH(slot));

            advancedFeedingFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("adv_feeding_filters_" + slotIndex, 16, false));

        // CRAFTING
        craftingUpgradeGroup();
    }

    public void updateFilterDelegate(IBasicFilterable wrapper) {
        commonFilterStackHandler.setDelegatedStackHandler(wrapper::getFilterItems);
        commonFilterStackHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);
    }

    public void updateAdvancedFilterDelegate(IAdvancedFilterable wrapper) {
        advancedCommonFilterStackHandler.setDelegatedStackHandler(wrapper::getFilterItems);
        advancedCommonFilterStackHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);
    }

    public void updateCraftingDelegate(IStorageUpgrade wrapper) {
        craftingStackHandler.setDelegatedStackHandler(wrapper::getStorage);
        craftingStackHandler.syncToServer(DelegatedCraftingStackHandlerSH.UPDATE_CRAFTING);
    }

    private void craftingUpgradeGroup() {
        PanelSyncManager syncManager = panel.syncManager;

        this.craftingStackHandler = new DelegatedCraftingStackHandlerSH(
            panel::getStorageContainer,
            wrapper,
            slotIndex,
            10);
        syncManager.syncValue("crafting_delegation_" + slotIndex, craftingStackHandler);

        this.craftingMatrixSlots = new ModularSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularSlot slot = new IndexedModularCraftingMatrixSlot(
                slotIndex,
                craftingStackHandler.delegatedStackHandler,
                i);
            slot.slotGroup("crafting_result_" + slotIndex);
            syncManager.syncValue("crafting_slot_" + slotIndex, i, new ItemSlotSH(slot));
            craftingMatrixSlots[i] = slot;
        }
        syncManager.registerSlotGroup(new SlotGroup("crafting_matrix_$slotIndex", 3, false));
        craftingOutputSlot = new IndexedModularCraftingSlot(
            slotIndex,
            wrapper,
            craftingStackHandler.delegatedStackHandler,
            9);
        craftingOutputSlot.slotGroup("crafting_result_" + slotIndex);
        syncManager.syncValue("crafting_result_" + slotIndex, 0, new ItemSlotSH(craftingOutputSlot));
        craftingInfo = new CraftingSlotInfo(craftingMatrixSlots, craftingOutputSlot);

        syncManager.registerSlotGroup(new SlotGroup("crafting_result_" + slotIndex, 1, false));
    }

}
