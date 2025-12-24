package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class BackpackSlotSH extends ItemSlotSH {

    public static final int UPDATE_SET_MEMORY_STACK = 6;
    public static final int UPDATE_UNSET_MEMORY_STACK = 7;
    public static final int UPDATE_SET_SLOT_LOCK = 8;
    public static final int UPDATE_UNSET_SLOT_LOCK = 9;

    private final BackpackHandler handler;
    private final BackpackPanel panel;

    public BackpackSlotSH(BackpackPanel panel, BackpackHandler handler, ModularSlot slot) {
        super(slot);
        this.panel = panel;
        this.handler = handler;
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

        switch (id) {
            case UPDATE_SET_MEMORY_STACK: {
                handler.setMemoryStack(getSlot().getSlotIndex(), buf.readBoolean());
                break;
            }

            case UPDATE_UNSET_MEMORY_STACK:
                handler.unsetMemoryStack(getSlot().getSlotIndex());
                break;

            case UPDATE_SET_SLOT_LOCK:
                handler.setSlotLocked(getSlot().getSlotIndex(), true);
                break;

            case UPDATE_UNSET_SLOT_LOCK:
                handler.setSlotLocked(getSlot().getSlotIndex(), false);
                break;

            default:
                super.readOnServer(id, buf);
                break;
        }
        handler.writeToItem();
    }
}
