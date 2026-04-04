package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.storage.syncHandler.StorageSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class BackpackSlotSH extends StorageSlotSH {

    public final BackpackWrapper wrapper;

    public BackpackSlotSH(ModularSlot slot, BackpackWrapper wrapper) {
        super(slot, wrapper);
        this.wrapper = wrapper;
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        super.readOnClient(id, buf);
        if (id == UPDATE_SET_MEMORY_STACK || id == UPDATE_UNSET_MEMORY_STACK
            || id == UPDATE_SET_SLOT_LOCK
            || id == UPDATE_UNSET_SLOT_LOCK) {
            wrapper.syncToServer();
        }
    }
}
