package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularCraftingSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularFilterSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.FilterSlotSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.FoodFilterSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IAdvancedFilterable;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IStorageUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IUpgrade;

public class UpgradeSlotUpdateGroup {

    private final BackpackPanel panel;
    private final BackpackHandler handler;
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
    public DelegatedStackHandlerSH craftingMatrixStackHandler;
    public ModularCraftingMatrixSlot[] craftingMatrixSlots;

    private InventoryCraftingWrapper craftMatrix;
    public ModularCraftingSlot craftingResultSlot;

    public UpgradeSlotUpdateGroup(BackpackPanel panel, BackpackHandler handler, int slotIndex) {
        this.panel = panel;
        this.handler = handler;
        this.slotIndex = slotIndex;

        PanelSyncManager syncManager = panel.getSyncManager();

        // COMMON FILTER
        this.commonFilterStackHandler = new DelegatedStackHandlerSH(handler, slotIndex, 9);
        syncManager.syncValue("common_filter_delegation_" + slotIndex, commonFilterStackHandler);

        this.commonFilterSlots = new ModularFilterSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.getDelegatedStackHandler(), i);
            slot.slotGroup("common_filters_" + slotIndex);
            slot.changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (client) {
                    handler.syncToServer();
                }
            });

            syncManager.syncValue("common_filter_" + slotIndex, i, new FilterSlotSH(slot));

            commonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("common_filters_" + slotIndex, 9, false));

        // ADVANCED COMMON FILTER
        this.advancedCommonFilterStackHandler = new DelegatedStackHandlerSH(handler, slotIndex, 16);
        syncManager.syncValue("adv_common_filter_delegation_" + slotIndex, advancedCommonFilterStackHandler);

        this.advancedCommonFilterSlots = new ModularFilterSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(
                advancedCommonFilterStackHandler.getDelegatedStackHandler(),
                i);
            slot.slotGroup("adv_common_filters_" + slotIndex);
            slot.changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (client) {
                    handler.syncToServer();
                }
            });

            syncManager.syncValue("adv_common_filter_" + slotIndex, i, new FilterSlotSH(slot));

            advancedCommonFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("adv_common_filters_" + slotIndex, 16, false));

        // FEEDING FILTER
        this.feedingFilterSlots = new ModularFilterSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(commonFilterStackHandler.getDelegatedStackHandler(), i);
            slot.slotGroup("feeding_filters_" + slotIndex);
            slot.changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (client) {
                    handler.syncToServer();
                }
            });

            syncManager.syncValue("feeding_filter_" + slotIndex, i, new FoodFilterSlotSH(slot));

            feedingFilterSlots[i] = slot;
        }

        syncManager.registerSlotGroup(new SlotGroup("feeding_filters_" + slotIndex, 9, false));

        // ADVANCED FEEDING FILTER
        this.advancedFeedingFilterSlots = new ModularFilterSlot[16];
        for (int i = 0; i < 16; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(
                advancedCommonFilterStackHandler.getDelegatedStackHandler(),
                i);
            slot.slotGroup("adv_feeding_filters_" + slotIndex);
            slot.changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (client) {
                    handler.syncToServer();
                }
            });

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
        craftingMatrixStackHandler.setDelegatedStackHandler(wrapper::getStorage);
        craftingMatrixStackHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_STORAGE);
    }

    public void updateCraftingSlotIndex() {
        if (craftingMatrixSlots != null) {
            for (ModularCraftingMatrixSlot slot : craftingMatrixSlots) {
                slot.setActive(false);
            }
        }

        IUpgrade wrapper = handler != null ? handler.gatherCapabilityUpgrades(IUpgrade.class)
            .get(slotIndex) : null;

        if (wrapper != null) {
            for (ModularCraftingMatrixSlot slot : craftingMatrixSlots) {
                slot.setActive(wrapper.isTabOpened());
            }
        }
    }

    private void craftingUpgradeGroup() {
        PanelSyncManager syncManager = panel.getSyncManager();

        this.craftingMatrixStackHandler = new DelegatedStackHandlerSH(handler, slotIndex, 10);
        syncManager.syncValue("crafting_delegation_" + slotIndex, craftingMatrixStackHandler);

        this.craftMatrix = new InventoryCraftingWrapper(new Container() {

            @Override
            public boolean canInteractWith(EntityPlayer var1) {
                return panel.getSettings()
                    .canPlayerInteractWithUI(var1);
            }

            @Override
            public void detectAndSendChanges() {
                super.detectAndSendChanges();
                craftMatrix.detectChanges();
            }

            @Override
            public void onCraftMatrixChanged(IInventory p_75130_1_) {
                if (panel.getPlayer().worldObj.isRemote) return;
                craftingResultSlot.updateResult(
                    CraftingManager.getInstance()
                        .findMatchingRecipe(craftMatrix, panel.getPlayer().worldObj));
            }
        }, 3, 3, craftingMatrixStackHandler.getDelegatedStackHandler(), 0);

        this.craftingMatrixSlots = new ModularCraftingMatrixSlot[9];
        for (int i = 0; i < 9; i++) {
            ModularCraftingMatrixSlot slot = new ModularCraftingMatrixSlot(
                craftingMatrixStackHandler.getDelegatedStackHandler(),
                i);
            slot.slotGroup("crafting_workbench_slot_" + slotIndex)
                .changeListener((newItem, onlyAmountChanged, client, init) -> {
                    if (client) {
                        handler.syncToServer();
                    }

                    if (client) return;
                    boolean empty = true;
                    for (ModularCraftingMatrixSlot craftingMatrixSlot : craftingMatrixSlots) {
                        ItemStack stack = craftingMatrixSlot.getStack();
                        if (stack != null && stack.stackSize > 0) {
                            empty = false;
                            break;
                        }
                    }

                    if (empty) {
                        craftingResultSlot.updateResult(null);
                    } else {
                        craftingResultSlot.updateResult(
                            CraftingManager.getInstance()
                                .findMatchingRecipe(craftMatrix, panel.getPlayer().worldObj));
                    }
                });
            syncManager.syncValue("crafting_slot_" + slotIndex, i, new ItemSlotSH(slot));
            craftingMatrixSlots[i] = slot;
        }

        ModularCraftingSlot resultSlot = new ModularCraftingSlot(
            craftingMatrixStackHandler.getDelegatedStackHandler(),
            9,
            handler,
            slotIndex);
        resultSlot.slotGroup("crafting_workbench_slot_" + slotIndex)
            .accessibility(false, true);
        resultSlot.setCraftMatrix(craftMatrix);
        syncManager.syncValue("crafting_result_" + slotIndex, 0, new ItemSlotSH(resultSlot));
        craftingResultSlot = resultSlot;

        syncManager.registerSlotGroup(new SlotGroup("crafting_workbench_slot_" + slotIndex, 10, false));
    }

}
