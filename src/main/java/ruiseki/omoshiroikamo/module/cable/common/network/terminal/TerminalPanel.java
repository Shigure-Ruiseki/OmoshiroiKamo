package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.Map;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;

import ruiseki.omoshiroikamo.core.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableItemSlot;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public class TerminalPanel extends ModularPanel {

    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT_COUNT = COLUMNS * ROWS;

    private SlotGroupWidget itemSlots;
    private final CableItemSlotSH[] itemSlotSH = new CableItemSlotSH[SLOT_COUNT];
    private final CableItemSlot[] slots = new CableItemSlot[SLOT_COUNT];

    public TerminalPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings,
        CableTerminal terminal) {
        super("cable_terminal");
        this.size(176, 223);

        ItemIndexClient clientIndex = new ItemIndexClient();
        ItemIndexSH sh = new ItemIndexSH(terminal::getItemNetwork, clientIndex);
        syncManager.syncValue("cable_terminal_sh", sh);

        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(terminal.getItemNetwork());
            itemSlotSH[i] = slotSH;
            syncManager.syncValue("itemSlot_" + i, i, slotSH);
        }

        itemSlots = new SlotGroupWidget().name("itemSlots")
            .pos(7, 14);
        this.child(itemSlots);
        buildItemGrid();
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

        syncManager.addCloseListener(p -> clientIndex.destroy());

        this.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());
    }

    private void buildItemGrid() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            CableItemSlot slot = new CableItemSlot().setStack(null)
                .syncHandler("itemSlot_" + i, i);
            slots[i] = slot;
            itemSlots.child(slot.pos(col * 18, row * 18));
        }
    }

    private void updateGrid(Map<ItemStackKey, Integer> db) {

        int i = 0;
        for (var e : db.entrySet()) {
            if (i >= slots.length) break;

            ItemStack stack = e.getKey()
                .toStack(e.getValue());
            slots[i].setStack(stack);
            i++;
        }

        while (i < slots.length) {
            slots[i].setStack(null);
            i++;
        }
    }
}
