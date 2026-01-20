package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.api.item.CraftingFilter;
import ruiseki.omoshiroikamo.api.item.ItemStackKey;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.core.client.gui.widget.ExpandedWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.integration.nei.NEISearchField;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.cable.client.gui.container.TerminalContainer;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CraftingSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableCraftingSlot;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableItemSlot;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableScrollBar;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CraftingFilterButton;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.SortOrderButton;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.SortTypeButton;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;

public class TerminalPanel extends ModularPanel {

    private final GuiData data;
    private final PanelSyncManager syncManager;
    private final UISettings settings;
    private final CableTerminal terminal;

    public static final int COLUMNS = 9;
    public static final int ROWS = 6;
    public static final int SLOT_COUNT = COLUMNS * ROWS;

    public final ItemIndexClient clientIndex;
    public ItemIndexSH syncHandler;
    public SlotGroupWidget itemSlots;
    public CableScrollBar scrollBar;
    public SearchBarWidget searchBar;
    public StringSyncValue searchSyncer;
    public CableItemSlot[] slots = new CableItemSlot[SLOT_COUNT];

    public ModularCraftingMatrixSlot[] craftingMatrixSlots = new ModularCraftingMatrixSlot[9];
    public CraftingSlotSH[] craftingSlotSH = new CraftingSlotSH[9];
    public InventoryCraftingWrapper craftMatrix;

    private int indexOffset = 0;

    public TerminalPanel(GuiData data, PanelSyncManager syncManager, UISettings settings, CableTerminal terminal) {
        super("cable_terminal");

        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;
        this.terminal = terminal;

        this.size(176, 282);

        clientIndex = new ItemIndexClient();
        syncHandler = new ItemIndexSH(terminal, this, clientIndex);
        syncManager.syncValue("cable_terminal_sh", syncHandler);

        buildLeftBar();

        buildItemGrid();

        buildCraftingGrid();

        buildTopBar();

        ExpandedWidget rightExpanded = ExpandedWidget.right()
            .coverChildrenWidth()
            .height(134)
            .pos(173, 0)
            .paddingRight(6)
            .excludeAreaInRecipeViewer();
        scrollBar = new CableScrollBar().top(18)
            .size(14, ROWS * 18)
            .total(clientIndex::size)
            .onChange(offset -> {
                indexOffset = offset;
                updateGrid(clientIndex);
            })
            .columns(COLUMNS)
            .rows(ROWS);
        Column rightCol = (Column) new Column().coverChildren()
            .child(scrollBar);
        rightExpanded.child(rightCol);
        this.child(rightExpanded);

        this.child(
            new TileWidget(
                terminal.getItemStack()
                    .getDisplayName()).maxWidth(176));
        this.child(
            IKey.lang(data.getPlayer().inventory.getInventoryName())
                .asWidget()
                .pos(8, 188));
        this.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        final int[] last = { -1 };
        syncManager.onClientTick(() -> {
            int v = clientIndex.getServerVersion();
            if (v != last[0]) {
                last[0] = v;
                updateGrid(clientIndex);
            }
            String neiText = NEISearchField.getText();
            if (neiText != null && terminal.getSyncNEI() && searchBar != null && !searchBar.isFocused()) {
                if (!neiText.equals(searchSyncer.getStringValue())) {
                    searchSyncer.setStringValue(neiText);
                    clientIndex.setSearch(neiText);
                    updateGrid(clientIndex);
                }
            }
        });
        syncManager.onServerTick(() -> {
            ItemNetwork net = terminal.getItemNetwork();
            if (net != null) {
                syncHandler.requestSync(clientIndex.getServerVersion());
            }
        });

        settings.customContainer(() -> new TerminalContainer(terminal, this));
    }

    private void buildItemGrid() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(terminal);
            syncManager.syncValue("itemSlot_" + i, i, slotSH);
        }

        itemSlots = new SlotGroupWidget().coverChildren()
            .name("itemSlots")
            .pos(7, 18);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            CableItemSlot slot = new CableItemSlot() {

                @Override
                public boolean onMouseScroll(UpOrDown dir, int amount) {
                    scrollBar.onMouseScroll(dir, amount);
                    return true;
                }
            };

            slot.setEntry(null, 0, false);
            slot.syncHandler("itemSlot_" + i, i);

            slots[i] = slot;
            itemSlots.child(slot.pos(col * 18, row * 18));
        }
        this.child(itemSlots);
    }

    private void updateGrid(ItemIndexClient index) {
        List<ItemStackKey> keys = index.viewGrid(indexOffset, COLUMNS, ROWS);

        int i = 0;
        for (ItemStackKey key : keys) {
            if (i >= slots.length) break;

            int stored = index.getStored(key);
            boolean craftable = index.isCraftable(key);

            slots[i++].setEntry(stored > 0 || craftable ? key : null, stored, craftable && stored <= 0);
        }

        for (; i < slots.length; i++) {
            slots[i].setEntry(null, 0, false);
        }
    }

    private void buildLeftBar() {
        EnumSyncValue<SortType> sortTypeSyncer = new EnumSyncValue<>(
            SortType.class,
            terminal::getSortType,
            this::changeSortType);
        syncManager.syncValue("sortTypeSyncer", sortTypeSyncer);

        BooleanSyncValue sortOrderSyncer = new BooleanSyncValue(terminal::getSortOrder, this::changeSortOrder);
        syncManager.syncValue("sortOrderSyncer", sortOrderSyncer);

        BooleanSyncValue syncNEISyncer = new BooleanSyncValue(terminal::getSyncNEI, terminal::setSyncNEI);
        syncManager.syncValue("syncNEI", syncNEISyncer);

        EnumSyncValue<CraftingFilter> craftingSyncer = new EnumSyncValue<>(
            CraftingFilter.class,
            terminal::getCraftingFilter,
            this::changeCraftingFilter);
        syncManager.syncValue("craftingSyncer", craftingSyncer);

        Column col = new Column();
        col.coverChildren()
            .width(18)
            .pos(-20, 6)
            .childPadding(2);

        SortTypeButton sortType = new SortTypeButton(sortTypeSyncer);
        col.child(sortType);

        SortOrderButton sortOrder = new SortOrderButton(sortOrderSyncer);
        col.child(sortOrder);

        ToggleButton syncNEI = new ToggleButton().size(18)
            .overlay(SYNC_NEI)
            .tooltip(tooltip -> tooltip.addLine(IKey.lang("gui.syncNEI")))
            .value(new BoolValue.Dynamic(syncNEISyncer::getValue, syncNEISyncer::setValue));
        col.child(syncNEI);

        CraftingFilterButton button = new CraftingFilterButton(craftingSyncer);
        col.child(button);

        this.child(col);
    }

    private void buildTopBar() {

        Row row = new Row();
        row.coverChildren()
            .pos(7, 5)
            .childPadding(2);

        Row channelRow = new Row();
        channelRow.height(11)
            .width(60);
        channelRow.child(
            IKey.lang("gui.cable.index_channel")
                .asWidget());
        channelRow.child(
            new TextFieldWidget().size(32, 11)
                .setFormatAsInteger(true)
                .setScrollValues(1, 5, 10)
                .setDefaultNumber(-1)
                .setNumbers(-1, Integer.MAX_VALUE)
                .value(SyncHandlers.intNumber(terminal::getChannel, this::changeChannel)));
        row.child(channelRow);

        searchSyncer = new StringSyncValue(terminal::getSearch, terminal::setSearch);
        searchBar = new SearchBarWidget() {

            @Override
            public void doSearch(String search) {
                clientIndex.setSearch(search);
                updateGrid(clientIndex);

                if (terminal.getSyncNEI() && !NEISearchField.isFocused()) {
                    NEISearchField.setText(search);
                }
            }
        };
        searchBar.top(1)
            .value(searchSyncer)
            .size(102, 10);

        row.child(searchBar);

        this.child(row);
    }

    private void buildCraftingGrid() {
        CableCraftingSlot craftingResultSlot = new CableCraftingSlot(terminal.craftingStackHandler, 9, terminal);
        craftingResultSlot.accessibility(false, true)
            .slotGroup("craftingResult");

        craftMatrix = new InventoryCraftingWrapper(new Container() {

            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return settings.canPlayerInteractWithUI(player);
            }

            @Override
            public void detectAndSendChanges() {
                super.detectAndSendChanges();
                craftMatrix.detectChanges();
            }

            @Override
            public void onCraftMatrixChanged(IInventory p_75130_1_) {
                if (data.getPlayer().worldObj.isRemote) return;
                ItemStack result = CraftingManager.getInstance()
                    .findMatchingRecipe(craftMatrix, data.getPlayer().worldObj);
                craftingResultSlot.updateResult(result);
            }

        }, 3, 3, terminal.craftingStackHandler, 0);
        craftingResultSlot.setCraftMatrix(craftMatrix);

        for (int i = 0; i < 9; i++) {
            ModularCraftingMatrixSlot slot = new ModularCraftingMatrixSlot(terminal.craftingStackHandler, i);
            slot.changeListener((newItem, onlyAmountChanged, client, init) -> {
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
                            .findMatchingRecipe(craftMatrix, data.getPlayer().worldObj));
                }
            });
            craftingMatrixSlots[i] = slot;
            slot.slotGroup("craftingMatrix");
        }
        syncManager.registerSlotGroup("craftingMatrix", 9, true);
        syncManager.registerSlotGroup("craftingResult", 1, true);

        SlotGroupWidget craftingGroupsWidget = new SlotGroupWidget().name("crafting_matrix")
            .pos(33, 132)
            .coverChildren();
        ItemSlot resultSlot = new ItemSlot().syncHandler(new CraftingSlotSH(craftingResultSlot, terminal))
            .pos(97, 18)
            .background(OKGuiTextures.EMPTY_SLOT);
        craftingGroupsWidget.child(resultSlot);

        for (int i = 0; i < 9; i++) {
            craftingSlotSH[i] = new CraftingSlotSH(craftingMatrixSlots[i], terminal);
            ItemSlot itemSlot = new ItemSlot().syncHandler(craftingSlotSH[i])
                .pos(i % 3 * 18, i / 3 * 18)
                .name("craftingMatrix_" + i);
            craftingGroupsWidget.child(itemSlot);
        }

        ButtonWidget<?> clearBtn = new ButtonWidget<>().size(8)
            .pos(56, 0)
            .overlay(CLEAR)
            .hoverBackground(BTN)
            .background(BTN)
            .onMousePressed(btn -> {
                if (btn == 0) {
                    Interactable.playButtonClickSound();
                    syncHandler.clearCraftingGrid();
                    return true;
                }
                return false;
            });
        craftingGroupsWidget.child(clearBtn);

        this.child(craftingGroupsWidget);
    }

    private void changeSortType(SortType sortType) {
        terminal.setSortType(sortType);
        clientIndex.setSortType(sortType);
        updateGrid(clientIndex);
    }

    private void changeSortOrder(boolean sortOrder) {
        terminal.setSortOrder(sortOrder);
        clientIndex.setSortOrder(sortOrder);
        updateGrid(clientIndex);
    }

    private void changeCraftingFilter(CraftingFilter craftingFilter) {
        terminal.setCraftingFilter(craftingFilter);
        clientIndex.setCraftingFilter(craftingFilter);
        updateGrid(clientIndex);
    }

    private void changeChannel(int channel) {
        terminal.setChannel(channel);
        syncHandler.syncChannel(channel);
    }

    private static final UITexture CLEAR = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(0, 44, 8, 8)
        .adaptable(1)
        .build();

    private static final UITexture BTN = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(0, 36, 8, 8)
        .adaptable(1)
        .build();

    private static final UITexture ARROW = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(47, 220, 24, 18)
        .adaptable(1)
        .build();

    private static final UITexture CRAFTING_SLOT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(71, 216, 26, 26)
        .adaptable(1)
        .build();

    public static final UITexture SYNC_NEI = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(162, 36, 18, 18)
        .build();

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawBackground(context, widgetTheme);
        ARROW.draw(context, 94, 148, 24, 18, widgetTheme.getTheme());
        CRAFTING_SLOT.draw(context, 126, 146, 26, 26, widgetTheme.getTheme());
    }
}
