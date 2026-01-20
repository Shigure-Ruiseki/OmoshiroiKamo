package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.StorageTerminal;

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
