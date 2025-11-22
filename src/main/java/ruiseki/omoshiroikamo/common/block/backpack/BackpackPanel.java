package ruiseki.omoshiroikamo.common.block.backpack;

import static java.lang.Math.min;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackGuiHolder.SLOT_SIZE;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.ceilDiv;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.gtnewhorizon.gtnhlib.capability.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.PlayerInvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.container.BackPackContainer;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IToggleable;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IUpgrade;
import ruiseki.omoshiroikamo.common.block.backpack.slot.ModularBackpackSlot;
import ruiseki.omoshiroikamo.common.block.backpack.slot.ModularUpgradeSlot;
import ruiseki.omoshiroikamo.common.block.backpack.syncHandler.BackpackSlotSH;
import ruiseki.omoshiroikamo.common.block.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.block.backpack.widget.TabWidget;
import ruiseki.omoshiroikamo.common.block.backpack.widget.UpgradeSlotGroupWidget;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;

public class BackpackPanel extends ModularPanel {

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

    private final List<ItemSlot> upgradeSlotWidgets = new ArrayList<>();
    private final UpgradeSlotGroupWidget upgradeSlotGroupWidget;
    private final List<TabWidget> tabWidgets = new ArrayList<>();

    private final int rowSize;
    private final int colSize;

    private final BackpackSlotSH[] backpackSlotSyncHandlers;
    private final UpgradeSlotSH[] upgradeSlotSyncHandlers;

    public BackpackPanel(EntityPlayer player, TileEntity tileEntity, PanelSyncManager syncManager, UISettings settings,
                         BackpackHandler handler) {
        super("test");
        this.player = player;
        this.tileEntity = tileEntity;
        this.syncManager = syncManager;
        this.settings = settings;
        this.handler = handler;

        this.rowSize = handler.getSlots() > 81 ? 12 : 9;
        this.colSize = ceilDiv(handler.getSlots(), rowSize);
        this.upgradeSlotGroupWidget = new UpgradeSlotGroupWidget(this, handler.getUpgradeSlots());
        upgradeSlotSyncHandlers = new UpgradeSlotSH[handler.getUpgradeSlots()];
        // upgradeSlotGroups = new UpgradeSlotUpdateGroup[backpackWrapper.upgradeSlotsSize()];
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
            ModularUpgradeSlot modularUpgradeSlot = new ModularUpgradeSlot(handler.getUpgradeHandler(), i, handler);
            modularUpgradeSlot.slotGroup("upgrade_inventory");
            UpgradeSlotSH syncHandler = new UpgradeSlotSH(modularUpgradeSlot);
            modularUpgradeSlot.changeListener((lastStack, currentStack, isClient, init) -> {
                if (isClient) {
                    updateUpgradeWidgets();
                }
            });
            syncManager.syncValue("upgrades", i, syncHandler);
            upgradeSlotSyncHandlers[i] = syncHandler;
        }
        // upgradeSlotGroups[i] = new UpgradeSlotUpdateGroup(this, backpackWrapper, i); }
        syncManager.registerSlotGroup(new SlotGroup("upgrade_inventory", 1, 99, true));

        backpackSlotSyncHandlers = new BackpackSlotSH[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            ModularBackpackSlot modularBackpackSlot = new ModularBackpackSlot(handler.getBackpackHandler(), i, handler);
            modularBackpackSlot.slotGroup("backpack_inventory");
            BackpackSlotSH syncHandler = new BackpackSlotSH(modularBackpackSlot);
            syncManager.syncValue("backpack", i, syncHandler);
            backpackSlotSyncHandlers[i] = syncHandler;
        }
        syncManager.registerSlotGroup(new SlotGroup("backpack_inventory", handler.getSlots(), 100, true));
    }

    public static BackpackPanel defaultPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
                                             TileEntity tileEntity, BackpackHandler handler, int width, int height) {
        BackpackPanel panel = new BackpackPanel(player, tileEntity, syncManager, settings, handler);
        panel.size(width, height);

        panel.settings.customContainer(BackPackContainer::new);

        syncManager.bindPlayerInventory(player);
        panel.bindPlayerInventory();
        return panel;
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

    public void addBackpackInventorySlots() {
        SlotGroupWidget backpackSlotGroupWidget = new SlotGroupWidget().name("backpack_inventory");
        backpackSlotGroupWidget.flex().coverChildren().leftRel(0.5F).top(17);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemSlot itemSlot = new ItemSlot().syncHandler("backpack", i).pos((i % rowSize) * SLOT_SIZE, (i / rowSize) * SLOT_SIZE).name("slot_" + i);
            backpackSlotGroupWidget.child(itemSlot);
        }
        this.child(backpackSlotGroupWidget);
    }

    public void addUpgradeSlots() {
        upgradeSlotGroupWidget.name("upgrade_inventory");
        upgradeSlotGroupWidget.flex().size(23, 10 + handler.getUpgradeSlots() * 18).left(-21);
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
            ItemSlot itemSlot = new ItemSlot().syncHandler("upgrades", i).pos(5, 5 + i * 18).name("slot_" + i);
            upgradeSlotWidgets.add(itemSlot);
            upgradeSlotGroupWidget.child(itemSlot);
        }
        this.child(upgradeSlotGroupWidget);
    }

    public void addTexts(EntityPlayer player) {
        child(IKey.lang(handler.getDisplayName() + ".name").asWidget().pos(8, 6));
        child(IKey.lang(player.inventory.getInventoryName()).asWidget().pos(8, 18 + colSize * 18));
    }


    public void updateUpgradeWidgets() {
        int tabIndex = 0;
        Integer openedTabIndex = null;

        resetTabState();
        for (int i = 0; i < upgradeSlotWidgets.size(); i++) {
            ItemSlot slotWidget = upgradeSlotWidgets.get(i);
            ItemStack upgrade = slotWidget.getSlot().getStack();
            if (!(upgrade != null && upgrade.getItem() instanceof ItemUpgrade item)) {
                continue;
            }
            if (!item.hasTab()) {
                continue;
            }

            IUpgrade wrapper = Capabilities.getCapability(upgrade, IUpgrade.class);
            if (wrapper == null) {
                continue;
            }

            if (wrapper.getTabOpened(upgrade)) {
                if (openedTabIndex != null) {
                    wrapper.setTabOpened(upgrade, false);
                    upgradeSlotSyncHandlers[i].syncToServer(
                        UpgradeSlotSH.UPDATE_UPGRADE_TAB_STATE,
                        buf -> buf.writeBoolean(false)
                    );
                    return;
                }
                openedTabIndex = i;
            }
        }

        syncToggles();

        disableUnusedTabWidgets(tabIndex);
        this.scheduleResize();

//        for (int slotIndex = 0; slotIndex < handler.getUpgradeSlots(); slotIndex++) {
//            ItemSlot slotWidget = upgradeSlotWidgets.get(slotIndex);
//            ItemStack stack = slotWidget.getSlot().getStack();
//            if (stack == null) {
//                continue;
//            }
//            Item item = stack.getItem();
//
//            if (!(item instanceof ItemUpgrade) || !((ItemUpgrade) item).hasTab()) {
//                continue;
//            }
//
//            TabWidget tabWidget = tabWidgets.get(tabIndex);
//           UpgradeSlotGroup upgradeSlotGroup = upgradeSlotGroups.get(slotIndex);
//
//            IUpgrade wrapper = Capabilities.getCapability(stack, IUpgrade.class);
//            if (wrapper == null) {
//                continue;
//            }
//
//            tabWidget.setShowExpanded(wrapper.getTabOpened(stack));
//            tabWidget.setEnabled(true);
//            tabWidget.setTabIcon(new ItemDrawable(stack));
//            tabWidget.tooltip(tooltip -> {
//                tooltip.clearText()
//                    .addLine(IKey.str(item.getItemStackDisplayName(stack)))
//                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE);
//            });
//
//            if (wrapper instanceof IMagnetUpgrade) {
//               tabWidget.setExpandedWidget(new CraftingUpgradeWidget(slotIndex, (CraftingUpgradeWrapper) wrapper));
//            }
//
//            getContext().getUISettings().getRecipeViewerSettings().addRecipeViewerExclusionArea(tabWidget.getExpandedWidget());
//            tabIndex++;
//        }
//
//        if (openedTabIndex != null) {
//            TabWidget openedTab = tabWidgets.get(openedTabIndex);
//            int covered = openedTab.getExpandedWidget() != null
//                ? openedTab.getExpandedWidget().getCoveredTabSize()
//                : 0;
//
//            int upperBound = Math.min(openedTabIndex + covered, tabWidgets.size());
//
//            for (int i = openedTabIndex + 1; i < upperBound; i++) {
//                tabWidgets.get(i).setEnabled(false);
//            }
//        }

    }

    private void resetTabState() {
        for (TabWidget tabWidget : tabWidgets) {
            if (tabWidget.getExpandedWidget() != null) {
                getContext().getUISettings().getRecipeViewerSettings().removeRecipeViewerExclusionArea(tabWidget.getExpandedWidget());
            }
        }
    }

    private void disableUnusedTabWidgets(int startTabIndex) {
        for (int i = startTabIndex; i < handler.getUpgradeSlots(); i++) {
            tabWidgets.get(i).setEnabled(false);
        }
    }


    private void syncToggles() {
        for (int i = 0; i < handler.getUpgradeSlots(); i++) {
            UpgradeSlotGroupWidget.UpgradeToggleWidget toggleWidget = upgradeSlotGroupWidget.getToggleWidget(i);
            IToggleable wrapper = toggleWidget.getWrapper();
            ItemStack upgrade = handler.getUpgradeHandler().getStackInSlot(i);

            if (wrapper != null) {
                toggleWidget.setEnabled(true);
                toggleWidget.setToggleEnabled(wrapper.isEnabled(upgrade));
            } else {
                toggleWidget.setEnabled(false);
            }
        }
    }
}
