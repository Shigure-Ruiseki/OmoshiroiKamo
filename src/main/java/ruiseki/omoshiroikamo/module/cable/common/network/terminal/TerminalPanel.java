package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.core.client.gui.widget.ExpandedWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.cable.client.gui.container.TerminalContainer;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableCraftingSlot;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableItemSlot;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableScrollBar;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public class TerminalPanel extends ModularPanel {

    private final GuiData data;
    private final PanelSyncManager syncManager;
    private final UISettings settings;
    private final CableTerminal terminal;

    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT_COUNT = COLUMNS * ROWS;

    private SlotGroupWidget itemSlots;
    private CableScrollBar scrollBar;
    private SearchBarWidget searchBar;
    private CableItemSlot[] slots = new CableItemSlot[SLOT_COUNT];
    private InventoryCraftingWrapper craftMatrix;

    @Getter
    @Setter
    private int indexOffset = 0;

    public TerminalPanel(GuiData data, PanelSyncManager syncManager, UISettings settings, CableTerminal terminal) {
        super("cable_terminal");

        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;
        this.terminal = terminal;

        settings.customContainer(() -> new TerminalContainer(terminal));

        this.size(176, 294);

        ItemIndexClient clientIndex = new ItemIndexClient();
        ItemIndexSH sh = new ItemIndexSH(terminal::getItemNetwork, clientIndex);
        syncManager.syncValue("cable_terminal_sh", sh);

        EnumSyncValue<SortType> sortTypeSyncer = new EnumSyncValue<>(
            SortType.class,
            terminal::getSortType,
            terminal::setSortType);
        syncManager.syncValue("sortTypeSyncer", sortTypeSyncer);

        buildItemGrid();

        buildCraftingGrid();

        searchBar = (SearchBarWidget) new SearchBarWidget() {

            @Override
            public void doSearch(String search) {
                updateGrid(clientIndex, getText());
            }
        }.top(5)
            .left(5)
            .width(166)
            .height(12);
        this.child(searchBar);

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
                updateGrid(clientIndex, searchBar.getText());
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
                .pos(8, 200));
        this.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        final int[] last = { -1 };
        syncManager.onClientTick(() -> {
            int v = clientIndex.getServerVersion();
            if (v != last[0]) {
                last[0] = v;
                updateGrid(clientIndex, searchBar.getText());
            }
        });
        syncManager.onServerTick(() -> {
            ItemNetwork net = terminal.getItemNetwork();
            if (net != null) {
                sh.requestSync(clientIndex.getServerVersion());
            }
        });
    }

    private void buildItemGrid() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(terminal.getItemNetwork());
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

            slot.setStack(null);
            slot.syncHandler("itemSlot_" + i, i);

            slots[i] = slot;
            itemSlots.child(slot.pos(col * 18, row * 18));
        }
        this.child(itemSlots);
    }

    private void updateGrid(ItemIndexClient index, String search) {
        List<ItemStackKey> keys = index.viewGrid(indexOffset, COLUMNS, ROWS);

        int i = 0;

        for (ItemStackKey key : keys) {
            if (i >= slots.length) break;

            int count = index.get(key);
            slots[i++].setStack(count > 0 ? key.toStack(count) : null);
        }

        while (i < slots.length) {
            slots[i++].setStack(null);
        }
    }

    private void buildCraftingGrid() {
        CableCraftingSlot craftingResultSlot = new CableCraftingSlot(terminal.craftingStackHandler, 9, terminal);
        craftingResultSlot.accessibility(false, true)
            .slotGroup("craftingMatrix");

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
                craftingResultSlot.updateResult(
                    CraftingManager.getInstance()
                        .findMatchingRecipe(craftMatrix, data.getPlayer().worldObj));
            }

        }, 3, 3, terminal.craftingStackHandler, 0);
        craftingResultSlot.setCraftMatrix(craftMatrix);

        ModularCraftingMatrixSlot[] craftingMatrixSlots = new ModularCraftingMatrixSlot[9];
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

        SlotGroupWidget craftingGroupsWidget = new SlotGroupWidget().name("crafting_matrix")
            .pos(33, 140)
            .coverChildren();
        ItemSlot resultSlot = new ItemSlot().slot(craftingResultSlot)
            .pos(97, 18)
            .background(OKGuiTextures.EMPTY_SLOT);
        craftingGroupsWidget.child(resultSlot);

        for (int i = 0; i < 9; i++) {
            ItemSlot itemSlot = new ItemSlot().slot(craftingMatrixSlots[i])
                .pos(i % 3 * 18, i / 3 * 18)
                .name("craftingMatrix_" + i);
            craftingGroupsWidget.child(itemSlot);
        }

        this.child(craftingGroupsWidget);
    }

    public static final UITexture ARROW = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(48, 221, 22, 15)
        .adaptable(8)
        .build();

    public static final UITexture CRAFTING_SLOT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(71, 216, 26, 26)
        .adaptable(1)
        .build();

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawBackground(context, widgetTheme);
        ARROW.draw(context, 94, 158, 24, 16, widgetTheme.getTheme());
        CRAFTING_SLOT.draw(context, 126, 154, 26, 26, widgetTheme.getTheme());
    }
}
