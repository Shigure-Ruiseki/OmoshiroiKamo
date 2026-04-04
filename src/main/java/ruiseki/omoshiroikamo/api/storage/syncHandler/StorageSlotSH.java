package ruiseki.omoshiroikamo.api.storage.syncHandler;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class StorageSlotSH extends ItemSlotSH {

    public static final int UPDATE_SET_MEMORY_STACK = 6;
    public static final int UPDATE_UNSET_MEMORY_STACK = 7;
    public static final int UPDATE_SET_SLOT_LOCK = 8;
    public static final int UPDATE_UNSET_SLOT_LOCK = 9;

    public final BackpackWrapper wrapper;

    public StorageSlotSH(ModularSlot slot, BackpackWrapper wrapper) {
        super(slot);
        this.wrapper = wrapper;
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

        switch (id) {
            case UPDATE_SET_MEMORY_STACK: {
                wrapper.setMemoryStack(getSlot().getSlotIndex(), buf.readBoolean());
                break;
            }

            case UPDATE_UNSET_MEMORY_STACK:
                wrapper.unsetMemoryStack(getSlot().getSlotIndex());
                break;

            case UPDATE_SET_SLOT_LOCK:
                wrapper.setSlotLocked(getSlot().getSlotIndex(), true);
                break;

            case UPDATE_UNSET_SLOT_LOCK:
                wrapper.setSlotLocked(getSlot().getSlotIndex(), false);
                break;

            default:
                super.readOnServer(id, buf);
                return;
        }
        wrapper.markDirty();
    }
}
