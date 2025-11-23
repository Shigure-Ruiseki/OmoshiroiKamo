package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.createWrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.SyncHandler;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.DelegatedItemHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.FilterableWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.UpgradeWrapper;

public class DelegatedStackHandlerSH extends SyncHandler {

    public static final int UPDATE_FILTERABLE = 0;

    private final BackpackHandler handler;
    private final int slotIndex;
    @Getter
    public DelegatedItemHandler delegatedStackHandler;

    public DelegatedStackHandlerSH(BackpackHandler handler, int slotIndex) {
        this.handler = handler;
        this.slotIndex = slotIndex;
        this.delegatedStackHandler = new DelegatedItemHandler(() -> new ItemStackHandler(16));
    }

    public void setDelegatedStackHandler(IDelegatedSupplier delegated) {
        this.delegatedStackHandler.setDelegated(delegated::get);
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        // Client side không cần implement
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        ItemStack stack = handler.getUpgradeHandler()
            .getStackInSlot(slotIndex);
        if (id == UPDATE_FILTERABLE) {
            UpgradeWrapper wrapper = createWrapper(stack);
            if (wrapper instanceof FilterableWrapper filterable) {
                setDelegatedStackHandler(filterable::getFilterItems);
            }
        }
    }

    @FunctionalInterface
    public interface IDelegatedSupplier {

        IItemHandler get();
    }
}
