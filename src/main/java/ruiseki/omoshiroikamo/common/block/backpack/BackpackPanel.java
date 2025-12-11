package ruiseki.omoshiroikamo.common.block.backpack;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackGuiHolder.SLOT_SIZE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.item.PlayerInvWrapper;
import com.cleanroommc.modularui.utils.item.PlayerMainInvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.container.BackPackContainer;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.BackpackSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularBackpackSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularUpgradeSlot;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.BackpackSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.BackpackSlotSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.AdvancedExpandedTabWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.AdvancedFeedingUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.AdvancedFilterUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.AdvancedMagnetUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.AdvancedVoidUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.BackpackList;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.BackpackRow;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.BasicExpandedTabWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.CraftingUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.FeedingUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.FilterUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.MagnetUpgradeWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.SettingTabWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TabWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TileWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TransferButtonWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.UpgradeSlotGroupWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.VoidUpgradeWidget;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedMagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedVoidUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.BasicUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.FeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.FilterUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IToggleable;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.MagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapperFactory;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.VoidUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class BackpackPanel extends ModularPanel {

    public static final AdaptableUITexture LAYERED_TAB_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(132, 0, 124, 256)
        .adaptable(4)
        .tiled()
        .build();

    public static final AdaptableUITexture TILE_TAB_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(128, 0, 128, 10)
        .adaptable(4)
        .tiled()
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> SORT_TYPE_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(
            IKey.lang(LibMisc.LANG.localize("gui.sort_by_name")),
            MGuiTextures.SMALL_A_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_by_mod_id"), MGuiTextures.SMALL_M_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_by_count"), MGuiTextures.SMALL_1_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_by_ore_dict"), MGuiTextures.SMALL_O_ICON));

    public static BackpackPanel defaultPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
        TileEntity tileEntity, BackpackHandler handler, int width, int height, Integer slotIndex) {
        BackpackPanel panel = new BackpackPanel(player, tileEntity, syncManager, settings, handler, width, height);

        panel.settings.customContainer(() -> new BackPackContainer(handler, slotIndex));

        syncManager.bindPlayerInventory(player);
        panel.bindPlayerInventory();

        return panel;
    }

    @Getter
    private final EntityPlayer player;
    @Getter
    private final TileEntity tileEntity;
    @Getter
    private final PanelSyncManager syncManager;
    @Getter
    private final UISettings settings;
    @Getter
    private final BackpackHandler handler;

    @Getter
    private final UpgradeSlotGroupWidget upgradeSlotGroupWidget;
    @Getter
    private final List<TabWidget> tabWidgets;
    private final List<ItemSlot> upgradeSlotWidgets = new ArrayList<>();

    @Getter
    private final int width;
    @Getter
    private final int height;
    private final int backpackSlotsHeight;
    @Getter
    private final int rowSize;

    @Getter
    public final BackpackSH backpackSyncHandler;
    @Getter
    private final BackpackSlotSH[] backpackSlotSyncHandlers;
    private final UpgradeSlotSH[] upgradeSlotSyncHandlers;
    private final UpgradeSlotUpdateGroup[] upgradeSlotGroups;

    @Getter
    private final IPanelHandler settingPanel;
    @Getter
    private Column backpackInvCol;

    public boolean isMemorySettingTabOpened = false;
    public boolean shouldMemorizeRespectNBT = false;
    public boolean isSortingSettingTabOpened = false;
    public boolean isResetOpenedTabs = false;

    public BackpackPanel(EntityPlayer player, TileEntity tileEntity, PanelSyncManager syncManager, UISettings settings,
        BackpackHandler handler, int width, int height) {
        super("backpack_gui");
        this.player = player;
        this.tileEntity = tileEntity;
        this.syncManager = syncManager;
        this.settings = settings;
        this.handler = handler;

        this.width = width;
        this.height = height;
        this.size(this.width, this.height);
        this.backpackSlotsHeight = this.height - 112;
        int calculated = (this.width - 14) / SLOT_SIZE;
        this.rowSize = Math.max(9, Math.min(12, calculated));

        this.backpackSyncHandler = new BackpackSH(new PlayerMainInvWrapper(player.inventory), this.handler);
        this.syncManager.syncValue("backpack_wrapper", this.backpackSyncHandler);

        this.backpackSlotSyncHandlers = new BackpackSlotSH[this.handler.getBackpackSlots()];
        for (int i = 0; i < this.handler.getBackpackSlots(); i++) {
            ModularBackpackSlot modularBackpackSlot = new ModularBackpackSlot(this.handler, i);
            modularBackpackSlot.slotGroup("backpack_inventory");
            BackpackSlotSH syncHandler = new BackpackSlotSH(this.handler, modularBackpackSlot);
            this.syncManager.syncValue("backpack", i, syncHandler);
            this.backpackSlotSyncHandlers[i] = syncHandler;
        }
        this.syncManager
            .registerSlotGroup(new SlotGroup("backpack_inventory", this.handler.getBackpackSlots(), 100, true));

        tabWidgets = new ArrayList<>();
        this.upgradeSlotGroupWidget = new UpgradeSlotGroupWidget(this, this.handler.getUpgradeSlots());
        this.upgradeSlotSyncHandlers = new UpgradeSlotSH[this.handler.getUpgradeSlots()];
        this.upgradeSlotGroups = new UpgradeSlotUpdateGroup[this.handler.getUpgradeSlots()];
        for (int i = 0; i < this.handler.getUpgradeSlots(); i++) {
            ModularUpgradeSlot modularUpgradeSlot = new ModularUpgradeSlot(this.handler, i, this);
            modularUpgradeSlot.slotGroup("upgrade_inventory");
            UpgradeSlotSH syncHandler = new UpgradeSlotSH(this.handler, modularUpgradeSlot);
            modularUpgradeSlot.changeListener((lastStack, currentStack, isClient, init) -> {
                if (isClient) {
                    updateUpgradeWidgets();
                }
            });
            this.syncManager.syncValue("upgrades", i, syncHandler);
            this.upgradeSlotSyncHandlers[i] = syncHandler;
            this.upgradeSlotGroups[i] = new UpgradeSlotUpdateGroup(this, this.handler, i);
        }
        this.syncManager.registerSlotGroup(new SlotGroup("upgrade_inventory", 1, 99, true));

        settingPanel = this.syncManager
            .panel("setting_panel", (syncManager1, syncHandler) -> new BackpackSettingPanel(this), true);
    }

    public void modifyPlayerSlot(PanelSyncManager syncManager, InventoryType inventoryType, int slotIndex,
        EntityPlayer player) {
        if (inventoryType == InventoryTypes.BAUBLES) {
            return;
        }

        ModularSlot slot = new ModularSlot(new PlayerInvWrapper(player.inventory), slotIndex) {

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return false;
            }
        }.slotGroup("player_inventory");

        syncManager.itemSlot("player", slotIndex, slot);
    }

    public void addSortingButtons() {

        ButtonWidget<?> sortButton = new ButtonWidget<>().top(4)
            .right(21)
            .size(12)
            .overlay(MGuiTextures.SOLID_UP_ARROW_ICON)
            .setEnabledIf(w -> !settingPanel.isPanelOpen())
            .onMousePressed((button) -> {
                if (button == 0) {
                    Interactable.playButtonClickSound();

                    BackpackInventoryHelper.sortInventory(handler);

                    backpackSyncHandler.syncToServer(BackpackSH.UPDATE_SORT_INV, buf -> {
                        for (int i = 0; i < handler.getBackpackSlots(); i++) {
                            buf.writeItemStackToBuffer(handler.getStackInSlot(i));
                        }
                    });

                    return true;
                }
                return false;
            })
            .tooltipStatic(
                (tooltip) -> {
                    tooltip.addLine(IKey.lang("gui.sort_inventory"))
                        .pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

        CyclicVariantButtonWidget sortTypeButton = new CyclicVariantButtonWidget(
            SORT_TYPE_VARIANTS,
            handler.getSortType()
                .ordinal(),
            0,
            12,
            (index) -> {

                SortType nextSortType = SortType.values()[index];

                backpackSyncHandler.setSortType(nextSortType);

                backpackSyncHandler
                    .syncToServer(BackpackSH.UPDATE_SET_SORT_TYPE, buf -> { buf.writeInt(nextSortType.ordinal()); });

            }).setEnabledIf(cyclicVariantButtonWidget -> !settingPanel.isPanelOpen())
                .top(4)
                .right(7)
                .size(12);
        child(sortButton).child(sortTypeButton);
    }

    public void addTransferButtons() {
        TransferButtonWidget transferToPlayerButton = new TransferButtonWidget(
            MGuiTextures.DOT_DOWN_ARROW_ICON,
            MGuiTextures.SOLID_DOWN_ARROW_ICON).top(17 + backpackSlotsHeight)
                .right(21)
                .size(12)
                .setEnabledIf(transferButtonWidget -> !settingPanel.isPanelOpen())
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) {
                        boolean transferMatched = !Interactable.hasShiftDown();

                        Interactable.playButtonClickSound();
                        backpackSyncHandler.transferToPlayerInventory(transferMatched);
                        backpackSyncHandler.syncToServer(
                            BackpackSH.UPDATE_TRANSFER_TO_PLAYER_INV,
                            buf -> { buf.writeBoolean(transferMatched); });
                        return true;
                    }
                    return false;
                })
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    if (Interactable.hasShiftDown()) {
                        tooltip.addLine(IKey.lang("gui.transfer_to_player_inv"));
                    } else {
                        tooltip.addLine(IKey.lang("gui.transfer_to_player_inv_matched_1"))
                            .addLine(
                                IKey.lang("gui.transfer_to_player_inv_matched_2")
                                    .style(IKey.GRAY));
                    }

                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

        TransferButtonWidget transferToBackpackButton = new TransferButtonWidget(
            MGuiTextures.DOT_UP_ARROW_ICON,
            MGuiTextures.SOLID_UP_ARROW_ICON).top(17 + backpackSlotsHeight)
                .right(7)
                .size(12)
                .setEnabledIf(transferButtonWidget -> !settingPanel.isPanelOpen())
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) {
                        boolean transferMatched = !Interactable.hasShiftDown();

                        Interactable.playButtonClickSound();
                        backpackSyncHandler.transferToBackpack(transferMatched);
                        backpackSyncHandler.syncToServer(
                            BackpackSH.UPDATE_TRANSFER_TO_BACKPACK_INV,
                            buf -> { buf.writeBoolean(transferMatched); });
                        return true;
                    }
                    return false;
                })
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    if (Interactable.hasShiftDown()) {
                        tooltip.addLine(IKey.lang("gui.transfer_to_backpack_inv"));
                    } else {
                        tooltip.addLine(IKey.lang("gui.transfer_to_backpack_inv_matched_1"))
                            .addLine(
                                IKey.lang("gui.transfer_to_backpack_inv_matched_2")
                                    .style(IKey.GRAY));
                    }

                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

        child(transferToPlayerButton).child(transferToBackpackButton);
    }

    public void addBackpackInventorySlots() {

        BackpackList backpackList = new BackpackList().name("backpack_slots")
            .coverChildrenWidth()
            .top(17)
            .alignX(0.5f)
            .scrollDirection(GuiAxis.Y)
            .maxSize(backpackSlotsHeight)
            .wrapTight();

        backpackInvCol = (Column) new Column().coverChildren();
        List<BackpackRow> rows = new ArrayList<>();

        for (int r = 0; r < rowSize; r++) {
            BackpackRow row = (BackpackRow) new BackpackRow().coverChildren()
                .left(0);
            rows.add(row);
            backpackInvCol.child(row);
        }

        for (int i = 0; i < handler.getBackpackSlots(); i++) {
            int row = i / rowSize;

            BackpackSlot slot = (BackpackSlot) new BackpackSlot(this, handler).syncHandler("backpack", i)
                .size(SLOT_SIZE)
                .name("slot_" + i);

            rows.get(row)
                .child(slot);
        }

        backpackList.child(backpackInvCol);
        this.child(backpackList);
    }

    public void addSearchBar() {

        SearchBarWidget searchBarWidget = (SearchBarWidget) new SearchBarWidget(this).top(6)
            .width(this.width - 37)
            .height(10)
            .right(32);
        searchBarWidget.setEnabledIf(textFieldWidget -> !settingPanel.isPanelOpen());

        child(searchBarWidget);
    }

    public void addUpgradeSlots() {
        upgradeSlotGroupWidget.name("upgrade_inventory");
        upgradeSlotGroupWidget.flex()
            .size(23, 10 + handler.getUpgradeSlots() * 18)
            .left(-21);
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
            ItemSlot itemSlot = new ItemSlot().syncHandler("upgrades", i)
                .pos(5, 5 + i * 18)
                .name("slot_" + i);
            upgradeSlotWidgets.add(itemSlot);
            upgradeSlotGroupWidget.child(itemSlot);
        }
        this.child(upgradeSlotGroupWidget);
    }

    public void addUpgradeTabs() {
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
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

    public void addTexts(EntityPlayer player) {
        child(new TileWidget(this));
        child(
            IKey.lang(player.inventory.getInventoryName())
                .asWidget()
                .pos(8, 18 + backpackSlotsHeight));
    }

    public void updateUpgradeWidgets() {
        int tabIndex = 0;
        Integer openedTabIndex = null;

        resetTabState();

        for (int slotIndex = 0; slotIndex < upgradeSlotWidgets.size(); slotIndex++) {
            ItemSlot slotWidget = upgradeSlotWidgets.get(slotIndex);
            ItemStack upgrade = slotWidget.getSlot()
                .getStack();
            if (!(upgrade != null && upgrade.getItem() instanceof ItemUpgrade item)) {
                continue;
            }
            if (!item.hasTab()) {
                continue;
            }

            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(upgrade);
            if (wrapper == null) {
                continue;
            }

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

        for (int slotIndex = 0; slotIndex < handler.getUpgradeSlots(); slotIndex++) {
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

            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper == null) {
                continue;
            }

            tabWidget.setShowExpanded(wrapper.isTabOpened());
            tabWidget.setEnabled(true);
            tabWidget.setTabIcon(
                new ItemDrawable(stack).asIcon()
                    .size(18));
            tabWidget.tooltip(
                tooltip -> {
                    tooltip.clearText()
                        .addLine(IKey.str(item.getItemStackDisplayName(stack)))
                        .pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                });

            // spotless: off

            // Crafting
            if (wrapper instanceof CraftingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateCraftingDelegate(upgrade);
                tabWidget.setExpandedWidget(new CraftingUpgradeWidget(slotIndex, upgrade));
            }

            // Feeding
            else if (wrapper instanceof AdvancedFeedingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedFeedingUpgradeWidget(slotIndex, upgrade));
            } else if (wrapper instanceof FeedingUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new FeedingUpgradeWidget(slotIndex, upgrade));
            }

            // Magnet
            else if (wrapper instanceof AdvancedMagnetUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedMagnetUpgradeWidget(slotIndex, upgrade));
            } else if (wrapper instanceof MagnetUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new MagnetUpgradeWidget(slotIndex, upgrade));
            }

            // Filter
            else if (wrapper instanceof AdvancedFilterUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedFilterUpgradeWidget(slotIndex, upgrade));
            } else if (wrapper instanceof FilterUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new FilterUpgradeWidget(slotIndex, upgrade));
            }

            // Void
            else if (wrapper instanceof AdvancedVoidUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new AdvancedVoidUpgradeWidget(slotIndex, upgrade));
            } else if (wrapper instanceof VoidUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(new VoidUpgradeWidget(slotIndex, upgrade));
            }

            // Base
            else if (wrapper instanceof AdvancedUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateAdvancedFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(
                    new AdvancedExpandedTabWidget<>(slotIndex, upgrade, stack, upgrade.getSettingLangKey()));
            } else if (wrapper instanceof BasicUpgradeWrapper upgrade) {
                upgradeSlotGroup.updateFilterDelegate(upgrade);
                tabWidget.setExpandedWidget(
                    new BasicExpandedTabWidget<>(slotIndex, upgrade, stack, upgrade.getSettingLangKey()));
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
        for (int i = startTabIndex; i < handler.getUpgradeSlots(); i++) {
            TabWidget tabWidget = tabWidgets.get(i);
            if (tabWidget != null) {
                tabWidget.setEnabled(false);
            }
        }
        this.scheduleResize();
    }

    public void disableAllTabWidgets() {
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
            TabWidget tabWidget = tabWidgets.get(i);
            if (tabWidget != null) {
                tabWidget.setEnabled(false);
                tabWidget.setShowExpanded(false);
            }
        }
        this.scheduleResize();
    }

    private void syncToggles() {
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
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
        if (!handler.isKeepTab() && !isResetOpenedTabs) {
            for (int i = 0; i < upgradeSlotWidgets.size(); i++) {
                ItemSlot slotWidget = upgradeSlotWidgets.get(i);
                ItemStack stack = slotWidget.getSlot()
                    .getStack();
                if (stack == null || !(stack.getItem() instanceof ItemUpgrade item) || !item.hasTab()) continue;

                UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
                if (wrapper != null && wrapper.isTabOpened()) {
                    wrapper.setTabOpened(false);
                    upgradeSlotSyncHandlers[i]
                        .syncToServer(UpgradeSlotSH.UPDATE_UPGRADE_TAB_STATE, buf -> buf.writeBoolean(false));
                }
            }
            isResetOpenedTabs = true;
        }
    }

    @Override
    public void postDraw(ModularGuiContext context, boolean transformed) {
        super.postDraw(context, transformed);
        LAYERED_TAB_TEXTURE.draw(
            context,
            getFlex().getArea().width - 6,
            0,
            6,
            getFlex().getArea().height,
            WidgetTheme.getDefault()
                .getTheme());
    }
}
