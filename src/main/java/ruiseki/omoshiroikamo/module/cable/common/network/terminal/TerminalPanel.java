package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.Map;

import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableItemSlot;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableScrollBar;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableSearchBar;
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

        this.size(176, 284);

        ItemIndexClient clientIndex = new ItemIndexClient();
        ItemIndexSH sh = new ItemIndexSH(terminal::getItemNetwork, clientIndex);
        syncManager.syncValue("cable_terminal_sh", sh);

        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(terminal.getItemNetwork());
            itemSlotSH[i] = slotSH;
            syncManager.syncValue("itemSlot_" + i, i, slotSH);
        }

        buildItemGrid();

        this.child(
            new CableScrollBar().pos(178, 18)
                .size(8, ROWS * 18)
                .total(
                    () -> clientIndex.view()
                        .size())
                .onChange(offset -> {
                    indexOffset = offset;
                    updateGrid(clientIndex.view());
                })
                .columns(COLUMNS)
                .rows(ROWS));

        this.child(
            new TileWidget(
                terminal.getItemStack()
                    .getDisplayName()).maxWidth(176));
        this.child(
            new CableSearchBar().top(5)
                .left(5)
                .width(166)
                .height(12));
        this.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        final int[] last = { -1 };
        syncManager.onClientTick(() -> {
            int v = clientIndex.getServerVersion();
            if (v != last[0]) {
                last[0] = v;
                updateGrid(clientIndex.view());
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
        itemSlots = new SlotGroupWidget().name("itemSlots")
            .pos(7, 18);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            CableItemSlot slot = new CableItemSlot().setStack(null)
                .syncHandler("itemSlot_" + i, i);
            slots[i] = slot;
            itemSlots.child(slot.pos(col * 18, row * 18));
        }
        this.child(itemSlots);
    }

    private void updateGrid(Map<ItemStackKey, Integer> db) {
        int startIndex = indexOffset * COLUMNS;

        int i = 0;
        int skipped = 0;

        for (var e : db.entrySet()) {
            if (skipped++ < startIndex) continue;
            if (i >= slots.length) break;

            slots[i++].setStack(
                e.getKey()
                    .toStack(e.getValue()));
        }

        while (i < slots.length) {
            slots[i++].setStack(null);
        }
    }
}
