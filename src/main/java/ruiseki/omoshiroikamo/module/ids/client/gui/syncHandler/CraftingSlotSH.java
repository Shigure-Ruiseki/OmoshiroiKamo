package ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item.ItemNetwork;

public class CraftingSlotSH extends ItemSlotSH {

    private final StorageTerminal terminal;

    public CraftingSlotSH(ModularSlot slot, StorageTerminal terminal) {
        super(slot);
        this.terminal = terminal;
    }

    public ItemNetwork getNetwork() {
        return terminal.getItemNetwork();
    }
}
