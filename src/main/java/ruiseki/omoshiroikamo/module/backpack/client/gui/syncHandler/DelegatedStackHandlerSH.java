package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.SyncHandler;

import lombok.Getter;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.DelegatedItemHandler;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IStorageUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperFactory;

public class DelegatedStackHandlerSH extends SyncHandler {

    public static final int UPDATE_FILTERABLE = 0;
    public static final int UPDATE_STORAGE = 1;

    private final BackpackHandler handler;
    private final int slotIndex;
    @Getter
    public DelegatedItemHandler delegatedStackHandler;

    public DelegatedStackHandlerSH(BackpackHandler handler, int slotIndex, int numSlot) {
        this.handler = handler;
        this.slotIndex = slotIndex;
        this.delegatedStackHandler = new DelegatedItemHandler(() -> new ItemStackHandler(numSlot));
    }

    public DelegatedStackHandlerSH(BackpackHandler handler, int slotIndex) {
        this(handler, slotIndex, 10);
    }

    public void setDelegatedStackHandler(IDelegatedSupplier delegated) {
        this.delegatedStackHandler.setDelegated(delegated::get);
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        // NO OP
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        ItemStack stack = handler.getUpgradeHandler()
            .getStackInSlot(slotIndex);
        if (id == UPDATE_FILTERABLE) {
            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper instanceof IBasicFilterable upgrade) {
                setDelegatedStackHandler(upgrade::getFilterItems);
            }
        }
        if (id == UPDATE_STORAGE) {
            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper instanceof IStorageUpgrade upgrade) {
                setDelegatedStackHandler(upgrade::getStorage);
            }
        }
        handler.writeToItem();
    }

    @FunctionalInterface
    public interface IDelegatedSupplier {

        IItemHandler get();
    }
}
