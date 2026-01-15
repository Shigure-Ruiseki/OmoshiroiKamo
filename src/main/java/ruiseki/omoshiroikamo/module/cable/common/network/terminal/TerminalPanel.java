package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.List;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.client.gui.widget.ExpandedWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.module.cable.client.gui.container.TerminalContainer;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
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
    private final CableItemSlotSH[] itemSlotSH = new CableItemSlotSH[SLOT_COUNT];
    private final CableItemSlot[] slots = new CableItemSlot[SLOT_COUNT];

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

        this.size(176, 284);

        ItemIndexClient clientIndex = new ItemIndexClient();
        ItemIndexSH sh = new ItemIndexSH(terminal::getItemNetwork, clientIndex);
        syncManager.syncValue("cable_terminal_sh", sh);

        EnumSyncValue<SortType> sortTypeSyncer = new EnumSyncValue<>(
            SortType.class,
            terminal::getSortType,
            terminal::setSortType);
        syncManager.syncValue("sortTypeSyncer", sortTypeSyncer);

        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(terminal.getItemNetwork());
            itemSlotSH[i] = slotSH;
            syncManager.syncValue("itemSlot_" + i, i, slotSH);
        }
        buildItemGrid();

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
}
