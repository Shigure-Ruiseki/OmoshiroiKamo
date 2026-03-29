package ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.EmptyHandler;
import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.module.storage.client.gui.container.StorageContainer;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.IndexedInventoryCraftingWrapper;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IStorageUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperFactory;

public class DelegatedCraftingStackHandlerSH extends DelegatedStackHandlerSH {

    public static final int UPDATE_CRAFTING = 2;
    public static final int DETECT_CHANGES = 3;

    private final Supplier<StorageContainer> containerProvider;
    private final StorageWrapper wrapper;
    private final int slotIndex;

    private IndexedInventoryCraftingWrapper inventoryCrafting;

    public DelegatedCraftingStackHandlerSH(Supplier<StorageContainer> containerProvider, StorageWrapper wrapper,
        int slotIndex, int wrappedSlotAmount) {
        super(wrapper, slotIndex, wrappedSlotAmount);

        this.containerProvider = containerProvider;
        this.wrapper = wrapper;
        this.slotIndex = slotIndex;
    }

    @Override
    public void setDelegatedStackHandler(Supplier<IItemHandler> delegated) {
        super.setDelegatedStackHandler(delegated);
        updateInventoryCrafting();
    }

    public void updateInventoryCrafting() {

        if (inventoryCrafting == null) {

            inventoryCrafting = new IndexedInventoryCraftingWrapper(
                slotIndex,
                containerProvider.get(),
                3,
                3,
                delegatedStackHandler,
                0);

            containerProvider.get()
                .registerInventoryCrafting(slotIndex, inventoryCrafting);
        }

        ItemStack stack = wrapper.upgradeItemStackHandler.getStackInSlot(slotIndex);

        UpgradeWrapperBase upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
        if (!(upgradeWrapper instanceof ICraftingUpgrade craftingWrapper)) {
            return;
        }

        inventoryCrafting.setCraftingDestination(craftingWrapper.getCraftingDes());

        inventoryCrafting.detectChanges();

        if (isValid() && !getSyncManager().isClient() && !(delegatedStackHandler.get() instanceof EmptyHandler)) {
            int resultSlot = inventoryCrafting.getSizeInventory() - 1;

            ItemStack result = delegatedStackHandler.get()
                .getStackInSlot(resultSlot);

            syncToClient(UPDATE_CRAFTING, buffer -> buffer.writeItemStackToBuffer(result));
        }
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        ItemStack stack = wrapper.upgradeItemStackHandler.getStackInSlot(slotIndex);

        if (id == UPDATE_CRAFTING) {
            UpgradeWrapperBase upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
            if (!(upgradeWrapper instanceof CraftingUpgradeWrapper craftingWrapper)) return;

            try {
                craftingWrapper.getStorage()
                    .setStackInSlot(9, buf.readItemStackFromBuffer());
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        ItemStack stack = wrapper.upgradeItemStackHandler.getStackInSlot(slotIndex);
        UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);

        switch (id) {

            case UPDATE_FILTERABLE: {
                if (wrapper instanceof IBasicFilterable upgrade) {
                    setDelegatedStackHandler(upgrade::getFilterItems);
                }
                break;
            }

            case UPDATE_STORAGE: {
                if (wrapper instanceof IStorageUpgrade upgrade) {
                    setDelegatedStackHandler(upgrade::getStorage);
                }
                break;
            }

            case UPDATE_CRAFTING: {
                if (wrapper instanceof ICraftingUpgrade upgrade) {
                    setDelegatedStackHandler(upgrade::getStorage);
                }
                break;
            }

            case DETECT_CHANGES: {

                if (inventoryCrafting != null) {
                    inventoryCrafting.detectChanges();

                    if (!(delegatedStackHandler.get() instanceof EmptyHandler)) {
                        int resultSlot = inventoryCrafting.getSizeInventory() - 1;

                        ItemStack result = delegatedStackHandler.get()
                            .getStackInSlot(resultSlot);

                        syncToClient(UPDATE_CRAFTING, buffer -> buffer.writeItemStackToBuffer(result));
                    }
                }

                break;
            }
        }
    }
}
