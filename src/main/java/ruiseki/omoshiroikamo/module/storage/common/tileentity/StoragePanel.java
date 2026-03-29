package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.item.PlayerMainInvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.storage.client.gui.container.StorageContainer;
import ruiseki.omoshiroikamo.module.storage.client.gui.container.StorageGuiContainer;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.CraftingSlotInfo;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.ModularStorageSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.ModularUpgradeSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.StorageSlot;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.StorageSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.StorageSlotSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.AdvancedExpandedTabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.AdvancedFeedingUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.AdvancedFilterUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.AdvancedMagnetUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.AdvancedVoidUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.BasicExpandedTabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.CraftingUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.FeedingUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.FilterUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.MagnetUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.SettingTabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.ShiftButtonWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.SmeltingUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.StorageList;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.StorageSearchBarWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.TabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.UpgradeSlotGroupWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.VoidUpgradeWidget;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.helper.StorageInventoryHelpers;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedFeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedMagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedVoidUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.BasicUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.FeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.FilterUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IToggleable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.MagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.SmeltingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperFactory;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.VoidUpgradeWrapper;

public class StoragePanel extends ModularPanel {

    public static final AdaptableUITexture LAYERED_TAB_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(132, 0, 124, 256)
        .adaptable(4)
        .tiled()
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> SORT_TYPE_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.sort_by_name"), OKGuiTextures.SMALL_A_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.sort_by_mod_id"), OKGuiTextures.SMALL_M_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.sort_by_count"), OKGuiTextures.SMALL_1_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.sort_by_ore_dict"), OKGuiTextures.SMALL_O_ICON));

    public final PosGuiData data;
    public final EntityPlayer player;
    public final TileEntity tileEntity;
    public final PanelSyncManager syncManager;
    public final UISettings settings;
    public final StorageWrapper wrapper;

    public final int rowSize;
    public StorageList storageList;
    public Column storageInvCol;
    public final IPanelHandler settingPanel;

    public final StorageSH storageSH;
    public final StorageSlotSH[] storageSlotSHs;

    public final List<TabWidget> tabWidgets;
    public final List<ItemSlot> upgradeSlotWidgets = new ArrayList<>();
    final UpgradeSlotGroupWidget upgradeSlotGroupWidget;
    public final UpgradeSlotSH[] upgradeSlotSyncHandlers;
    private final UpgradeSlotUpdateGroup[] upgradeSlotGroups;
    private final ItemStack[] lastUpgradeStacks;

    private StorageSearchBarWidget searchBarWidget;

    public boolean isMemorySettingTabOpened = false;
    public boolean shouldMemorizeRespectNBT = false;
    public boolean isSortingSettingTabOpened = false;
    public boolean isResetOpenedTabs = false;

    public StoragePanel(PosGuiData data, PanelSyncManager syncManager, UISettings settings, TEStorage tileEntity,
        StorageWrapper wrapper, int width) {
        super("gui");

        this.width(width);
        int calculated = (width - 14) / ItemSlot.SIZE;
        this.rowSize = Math.max(9, Math.min(12, calculated));

        this.data = data;
        this.player = data.getPlayer();
        this.tileEntity = tileEntity;
        this.syncManager = syncManager;
        this.settings = settings;
        this.wrapper = wrapper;

        this.storageSH = new StorageSH(new PlayerMainInvWrapper(player.inventory), this.wrapper);
        this.syncManager.syncValue("storage_wrapper", this.storageSH);

        this.storageSlotSHs = new StorageSlotSH[this.wrapper.storageSlots];
        for (int i = 0; i < this.wrapper.storageSlots; i++) {
            ModularStorageSlot slot = new ModularStorageSlot(this.wrapper, i);
            slot.slotGroup("storage_inventory");
            slot.changeListener((lastStack, currentStack, isClient, init) -> {
                if (isClient) {
                    searchBarWidget.research();
                }
            });
            StorageSlotSH syncHandler = new StorageSlotSH(slot, this.wrapper, this);
            this.syncManager.syncValue("storage", i, syncHandler);
            this.storageSlotSHs[i] = syncHandler;
        }
        this.syncManager.registerSlotGroup(new SlotGroup("storage_inventory", this.wrapper.storageSlots, 100, true));

        this.tabWidgets = new ArrayList<>();
        this.upgradeSlotGroupWidget = new UpgradeSlotGroupWidget(this, this.wrapper.upgradeSlots);
        this.upgradeSlotSyncHandlers = new UpgradeSlotSH[this.wrapper.upgradeSlots];
        this.upgradeSlotGroups = new UpgradeSlotUpdateGroup[this.wrapper.upgradeSlots];
        this.lastUpgradeStacks = new ItemStack[this.wrapper.upgradeSlots];
        for (int i = 0; i < this.wrapper.upgradeSlots; i++) {
            int slotIndex = i;
            ModularUpgradeSlot slot = new ModularUpgradeSlot(this.wrapper, i, this);
            slot.slotGroup("upgrade_inventory");
            UpgradeSlotSH syncHandler = new UpgradeSlotSH(slot, this.wrapper, this);
            slot.changeListener((stack, onlyAmountChanged, client, init) -> {
                if (!client) return;
                ItemStack last = lastUpgradeStacks[slotIndex];

                boolean itemChanged = !ItemUtils.areStacksEqual(last, stack, true);
                boolean tabChanged = isTabStateDifferent(last, stack);

                if (!itemChanged && !tabChanged) return;
                lastUpgradeStacks[slotIndex] = stack == null ? null : stack.copy();

                updateUpgradeWidgets();
            });

            this.syncManager.syncValue("upgrades", i, syncHandler);
            this.upgradeSlotSyncHandlers[i] = syncHandler;
            this.upgradeSlotGroups[i] = new UpgradeSlotUpdateGroup(this, this.wrapper, i);
        }
        this.syncManager.registerSlotGroup(new SlotGroup("upgrade_inventory", 1, 99, true));

        this.settingPanel = this.syncManager
            .syncedPanel("setting_panel", true, (syncManager1, syncHandler) -> new StorageSettingPanel(this));

        this.settings.customContainer(() -> new StorageContainer(wrapper));
        this.settings.customGui(() -> StorageGuiContainer::new);

        syncManager.bindPlayerInventory(player);
        this.bindPlayerInventory();
    }

    @Override
    public void onInit() {
        super.onInit();
        updateListHeight();
    }

    @Override
    public void onResized() {
        super.onResized();
        updateListHeight();
    }

    private void updateListHeight() {
        int totalSlots = wrapper.storageSlots;
        int rows = (totalSlots + rowSize - 1) / rowSize;

        int screenHeight = getScreen() != null ? getScreen().getScreenArea().height : 240;

        int slotSize = ItemSlot.SIZE;

        int maxRows = (screenHeight - 136) / slotSize;
        int visibleRows = Math.min(rows, maxRows);

        // set panel height
        height(visibleRows * slotSize + 118);

        // set list height
        int backpackSlotsHeight = visibleRows * slotSize;
        this.storageList.maxSize(backpackSlotsHeight);
        this.storageList.scheduleResize();

        this.scheduleResize();
    }

    public void addSortingButtons() {

        ShiftButtonWidget sortButton = new ShiftButtonWidget(
            OKGuiTextures.SOLID_DOWN_ARROW_ICON,
            OKGuiTextures.SOLID_UP_ARROW_ICON).top(4)
                .right(21)
                .size(12)
                .setEnabledIf(w -> !settingPanel.isPanelOpen())
                .onMousePressed((button) -> {
                    if (button == 0) {
                        Interactable.playButtonClickSound();
                        boolean reverse = !Interactable.hasShiftDown();

                        StorageInventoryHelpers.sortInventory(wrapper, reverse);

                        storageSH.syncToServer(StorageSH.UPDATE_SORT_INV, buf -> {
                            for (int i = 0; i < wrapper.storageSlots; i++) {
                                buf.writeItemStackToBuffer(wrapper.getStackInSlot(i));
                            }
                        });
                        return true;
                    }
                    return false;
                })
                .tooltipStatic(
                    (tooltip) -> tooltip.addLine(IKey.lang("gui.backpack.sort_inventory"))
                        .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

        CyclicVariantButtonWidget sortTypeButton = new CyclicVariantButtonWidget(
            SORT_TYPE_VARIANTS,
            wrapper.sortType.ordinal(),
            0,
            12,
            (index) -> {
                SortType nextSortType = SortType.values()[index];
                wrapper.sortType = nextSortType;
                storageSH.syncToServer(
                    StorageSH.UPDATE_SET_SORT_TYPE,
                    buf -> NetworkUtils.writeEnumValue(buf, nextSortType));
            }).setEnabledIf(cyclicVariantButtonWidget -> !settingPanel.isPanelOpen())
                .top(4)
                .right(7)
                .size(12);
        child(sortButton).child(sortTypeButton);
    }

    public void addTransferButtons() {
        ShiftButtonWidget transferToPlayerButton = new ShiftButtonWidget(
            OKGuiTextures.DOT_DOWN_ARROW_ICON,
            OKGuiTextures.SOLID_DOWN_ARROW_ICON).bottom(85)
                .right(21)
                .size(12)
                .setEnabledIf(shiftButtonWidget -> !settingPanel.isPanelOpen())
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) {
                        boolean transferMatched = !Interactable.hasShiftDown();

                        Interactable.playButtonClickSound();
                        storageSH.transferToPlayerInventory(transferMatched);
                        storageSH.syncToServer(
                            StorageSH.UPDATE_TRANSFER_TO_PLAYER_INV,
                            buf -> buf.writeBoolean(transferMatched));
                        return true;
                    }
                    return false;
                })
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    if (Interactable.hasShiftDown()) {
                        tooltip.addLine(IKey.lang("gui.backpack.transfer_to_player_inv"));
                    } else {
                        tooltip.addLine(IKey.lang("gui.backpack.transfer_to_player_inv_matched_1"))
                            .addLine(
                                IKey.lang("gui.backpack.transfer_to_player_inv_matched_2")
                                    .style(IKey.GRAY));
                    }

                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

        ShiftButtonWidget transferToBackpackButton = new ShiftButtonWidget(
            OKGuiTextures.DOT_UP_ARROW_ICON,
            OKGuiTextures.SOLID_UP_ARROW_ICON).bottom(85)
                .right(7)
                .size(12)
                .setEnabledIf(shiftButtonWidget -> !settingPanel.isPanelOpen())
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) {
                        boolean transferMatched = !Interactable.hasShiftDown();

                        Interactable.playButtonClickSound();
                        storageSH.transferToBackpack(transferMatched);
                        storageSH.syncToServer(
                            StorageSH.UPDATE_TRANSFER_TO_BACKPACK_INV,
                            buf -> buf.writeBoolean(transferMatched));
                        return true;
                    }
                    return false;
                })
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    if (Interactable.hasShiftDown()) {
                        tooltip.addLine(IKey.lang("gui.backpack.transfer_to_backpack_inv"));
                    } else {
                        tooltip.addLine(IKey.lang("gui.backpack.transfer_to_backpack_inv_matched_1"))
                            .addLine(
                                IKey.lang("gui.backpack.transfer_to_backpack_inv_matched_2")
                                    .style(IKey.GRAY));
                    }

                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

        child(transferToPlayerButton).child(transferToBackpackButton);
    }

    public void addStorageInventorySlots() {
        Row backpackInvRow = (Row) new Row().coverChildren()
            .alignX(0.5f)
            .top(18)
            .childPadding(4);

        storageList = new StorageList(this);

        storageInvCol = (Column) new Column().coverChildren();

        for (int i = 0; i < wrapper.getSlots(); i++) {
            int col = i % rowSize;
            int row = i / rowSize;

            StorageSlot slot = (StorageSlot) new StorageSlot(this, wrapper).syncHandler("storage", i)
                .size(ItemSlot.SIZE)
                .name("slot_" + i)
                .left(col * ItemSlot.SIZE)
                .top(row * ItemSlot.SIZE);

            storageInvCol.child(slot);
        }

        storageList.maxSizeRel(1f)
            .child(storageInvCol);
        backpackInvRow.child(storageList);

        this.child(backpackInvRow);
    }

    public void addSearchBar() {
        searchBarWidget = (StorageSearchBarWidget) new StorageSearchBarWidget(this).widthRel(0.75f)
            .height(10)
            .top(5)
            .left(5);

        searchBarWidget.setEnabledIf(tf -> !settingPanel.isPanelOpen());

        child(searchBarWidget);
    }

    public void addUpgradeSlots() {
        upgradeSlotGroupWidget.name("upgrade_inventory");
        upgradeSlotGroupWidget.resizer()
            .size(23, 10 + wrapper.upgradeSlots * ItemSlot.SIZE)
            .left(-21);
        for (int i = 0; i < wrapper.upgradeSlots; i++) {
            ItemSlot itemSlot = new ItemSlot().syncHandler("upgrades", i)
                .pos(5, 5 + i * ItemSlot.SIZE)
                .name("slot_" + i);
            upgradeSlotWidgets.add(itemSlot);
            upgradeSlotGroupWidget.child(itemSlot);
        }
        this.child(upgradeSlotGroupWidget);
    }

    public void addUpgradeTabs() {
        for (int i = 0; i < wrapper.upgradeSlots; i++) {
            TabWidget tab = new TabWidget(i + 1).name("upgrade_tab_" + i);
            tab.setEnabled(false);
            tabWidgets.add(tab);
        }

        for (int i = tabWidgets.size() - 1; i >= 0; i--) {
            child(tabWidgets.get(i));
        }
    }

    public void addSettingTab() {
        child(new SettingTabWidget());
    }

    public void addTexts() {
        child(new TileWidget(wrapper.getDisplayName()).widthRel(0.8f));
        child(
            IKey.lang(this.player.inventory.getInventoryName())
                .asWidget()
                .left(8)
                .bottom(85));
    }

    public void updateUpgradeWidgets() {
        int tabIndex = 0;
        Integer openedTabIndex = null;

        resetTabState();

        for (int slotIndex = 0; slotIndex < upgradeSlotWidgets.size(); slotIndex++) {
            ItemSlot slotWidget = upgradeSlotWidgets.get(slotIndex);
            ItemStack stack = slotWidget.getSlot()
                .getStack();
            if (!(stack != null && stack.getItem() instanceof ItemUpgrade<?>item)) {
                continue;
            }
            if (!item.hasTab()) {
                continue;
            }

            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
            if (wrapper == null) continue;

            if (wrapper.isTabOpened()) {
                if (openedTabIndex != null) {
                    wrapper.setTabOpened(false);
                    upgradeSlotSyncHandlers[slotIndex]
                        .syncToServer(UpgradeSlotSH.UPDATE_UPGRADE_TAB_STATE, buf -> buf.writeBoolean(false));
                    return;
                }
                openedTabIndex = slotIndex;
            }
        }

        for (int slotIndex = 0; slotIndex < wrapper.upgradeSlots; slotIndex++) {
            ItemSlot slotWidget = upgradeSlotWidgets.get(slotIndex);
            ItemStack stack = slotWidget.getSlot()
                .getStack();
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();

            if (!(item instanceof ItemUpgrade) || !((ItemUpgrade<?>) item).hasTab()) {
                continue;
            }

            TabWidget tabWidget = tabWidgets.get(tabIndex);
            UpgradeSlotUpdateGroup upgradeSlotGroup = upgradeSlotGroups[slotIndex];
            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
            if (wrapper == null) {
                continue;
            }

            tabWidget.setShowExpanded(wrapper.isTabOpened());
            tabWidget.setEnabled(true);
            tabWidget.setTabIcon(
                new ItemDrawable(stack).asIcon()
                    .size(18));
            tabWidget.tooltip(
                tooltip -> tooltip.clearText()
                    .addLine(IKey.str(item.getItemStackDisplayName(stack)))
                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

            // spotless: off

            // Crafting
            if (wrapper instanceof CraftingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateCraftingDelegate(upgrade);
                tabWidget.setExpandedWidget(new CraftingUpgradeWidget(slotIndex, stack, upgrade, this));
            }

            // Smelting
            else if (wrapper instanceof SmeltingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateSmeltingDelegate(upgrade);
                tabWidget.setExpandedWidget(new SmeltingUpgradeWidget(slotIndex, stack, upgrade, this));
            }

            // Feeding
            else if (wrapper instanceof AdvancedFeedingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedFeedingUpgradeWidget(slotIndex, stack, upgrade));
            } else if (wrapper instanceof FeedingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new FeedingUpgradeWidget(slotIndex, stack, upgrade));
            }

            // Magnet
            else if (wrapper instanceof AdvancedMagnetUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedMagnetUpgradeWidget(slotIndex, stack, upgrade));
            } else if (wrapper instanceof MagnetUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new MagnetUpgradeWidget(slotIndex, stack, upgrade));
            }

            // Filter
            else if (wrapper instanceof AdvancedFilterUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedFilterUpgradeWidget(slotIndex, stack, upgrade));
            } else if (wrapper instanceof FilterUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new FilterUpgradeWidget(slotIndex, stack, upgrade));
            }

            // Void
            else if (wrapper instanceof AdvancedVoidUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedVoidUpgradeWidget(slotIndex, stack, upgrade));
            } else if (wrapper instanceof VoidUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new VoidUpgradeWidget(slotIndex, stack, upgrade));
            }

            // Base
            else if (wrapper instanceof AdvancedUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(
                    new AdvancedExpandedTabWidget<>(slotIndex, stack, upgrade, upgrade.getSettingLangKey()));
            } else if (wrapper instanceof BasicUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(
                    new BasicExpandedTabWidget<>(slotIndex, stack, upgrade, upgrade.getSettingLangKey()));
            }

            // spotless: on
            if (tabWidget.getExpandedWidget() != null) {
                getContext().getUISettings()
                    .getRecipeViewerSettings()
                    .addExclusionArea(tabWidget.getExpandedWidget());
            }
            tabIndex++;
        }

        if (openedTabIndex != null) {
            TabWidget openedTab = tabWidgets.get(openedTabIndex);
            int covered = openedTab.getExpandedWidget() != null ? openedTab.getExpandedWidget()
                .getCoveredTabSize() : 0;

            int upperBound = Math.min(openedTabIndex + covered, tabWidgets.size());

            for (int i = openedTabIndex + 1; i < upperBound; i++) {
                tabWidgets.get(i)
                    .setEnabled(false);
            }
        }

        resetOpenedTabsIfNotKeep();

        syncToggles();
        disableUnusedTabWidgets(tabIndex);
        this.scheduleResize();
    }

    private void resetTabState() {
        for (TabWidget tabWidget : tabWidgets) {
            if (tabWidget.getExpandedWidget() != null) {
                getContext().getUISettings()
                    .getRecipeViewerSettings()
                    .removeExclusionArea(tabWidget.getExpandedWidget());
            }
        }
    }

    private void disableUnusedTabWidgets(int startTabIndex) {
        for (int i = startTabIndex; i < wrapper.upgradeSlots; i++) {
            TabWidget tabWidget = tabWidgets.get(i);
            if (tabWidget != null) {
                tabWidget.setEnabled(false);
            }
        }
        this.scheduleResize();
    }

    public void disableAllTabWidgets() {
        for (int i = 0; i < wrapper.upgradeSlots; i++) {
            TabWidget tabWidget = tabWidgets.get(i);
            if (tabWidget != null) {
                tabWidget.setEnabled(false);
                tabWidget.setShowExpanded(false);
            }
        }
        this.scheduleResize();
    }

    private void syncToggles() {
        for (int i = 0; i < wrapper.upgradeSlots; i++) {
            UpgradeSlotGroupWidget.UpgradeToggleWidget toggleWidget = upgradeSlotGroupWidget.getToggleWidget(i);
            IToggleable wrapper = toggleWidget.getWrapper();

            if (wrapper != null) {
                toggleWidget.setEnabled(true);
                toggleWidget.setToggleEnabled(wrapper.isEnabled());
            } else {
                toggleWidget.setEnabled(false);
            }
        }
    }

    public void resetOpenedTabsIfNotKeep() {
        if (!wrapper.keepTab && !isResetOpenedTabs) {
            for (int slotIndex = 0; slotIndex < upgradeSlotWidgets.size(); slotIndex++) {
                ItemSlot slotWidget = upgradeSlotWidgets.get(slotIndex);
                ItemStack stack = slotWidget.getSlot()
                    .getStack();
                if (stack == null || !(stack.getItem() instanceof ItemUpgrade<?>item) || !item.hasTab()) continue;

                UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
                if (wrapper != null && wrapper.isTabOpened()) {
                    wrapper.setTabOpened(false);
                    upgradeSlotSyncHandlers[slotIndex]
                        .syncToServer(UpgradeSlotSH.UPDATE_UPGRADE_TAB_STATE, buf -> buf.writeBoolean(false));
                }
            }
            isResetOpenedTabs = true;
        }
    }

    public StorageContainer getStorageContainer() {
        return (StorageContainer) syncManager.getContainer();
    }

    public int getOpenCraftingUpgradeSlot() {
        for (int slotIndex = 0; slotIndex < wrapper.upgradeSlots; slotIndex++) {
            ItemSlot slot = upgradeSlotWidgets.get(slotIndex);
            ItemStack stack = slot.getSlot()
                .getStack();
            Item item = stack.getItem();

            if (!(item instanceof ItemUpgrade<?> && ((ItemUpgrade<?>) item).hasTab())) {
                continue;
            }
            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
            if (wrapper == null) continue;

            if (wrapper instanceof CraftingUpgradeWrapper && wrapper.isTabOpened()) {
                return slotIndex;
            }
        }
        return -1;
    }

    public CraftingSlotInfo getCraftingInfo(int slotIndex) {
        return upgradeSlotGroups[slotIndex].craftingInfo;
    }

    private boolean isTabStateDifferent(ItemStack a, ItemStack b) {
        boolean aState = a != null && a.hasTagCompound()
            && a.getTagCompound()
                .getBoolean(IUpgradeWrapper.TAB_STATE_TAG);

        boolean bState = b != null && b.hasTagCompound()
            && b.getTagCompound()
                .getBoolean(IUpgradeWrapper.TAB_STATE_TAG);

        return aState != bState;
    }

    @Override
    public void postDraw(ModularGuiContext context, boolean transformed) {
        super.postDraw(context, transformed);
        LAYERED_TAB_TEXTURE.draw(
            context,
            resizer().getArea().width - 6,
            0,
            6,
            resizer().getArea().height,
            WidgetTheme.getDefault()
                .getTheme());
    }
}
