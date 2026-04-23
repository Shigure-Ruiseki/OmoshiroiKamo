package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.EmptyHandler;
import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.IndexedInventoryCraftingWrapper;
import ruiseki.omoshiroikamo.api.storage.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.api.storage.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperFactory;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackPackContainer;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.CraftingUpgradeWrapper;

public class DelegatedCraftingStackHandlerSH extends DelegatedStackHandlerSH {

    public static final int UPDATE_CRAFTING = 3;
    public static final int DETECT_CHANGES = 4;

    private final Supplier<BackPackContainer> containerProvider;
    private final BackpackWrapper wrapper;
    private final int slotIndex;

    private IndexedInventoryCraftingWrapper inventoryCrafting;

    public DelegatedCraftingStackHandlerSH(Supplier<BackPackContainer> containerProvider, BackpackWrapper wrapper,
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

        ItemStack stack = wrapper.getUpgradeHandler()
            .getStackInSlot(slotIndex);

        UpgradeWrapperBase upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);

        if (!(upgradeWrapper instanceof ICraftingUpgrade craftingWrapper)) {
            return;
        }

        inventoryCrafting.setCraftingDestination(craftingWrapper.getCraftingDes());

        inventoryCrafting.detectChanges();

        if (isValid() && !getSyncManager().isClient() && !(delegatedStackHandler.get() instanceof EmptyHandler)) {
            int resultSlot = inventoryCrafting.getSizeInventory() - 1;

            var result = delegatedStackHandler.get()
                .getStackInSlot(resultSlot);

            syncToClient(UPDATE_CRAFTING, buffer -> buffer.writeItemStackToBuffer(result));
        }
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {

        if (id == UPDATE_CRAFTING) {
            ItemStack stack = wrapper.getUpgradeHandler()
                .getStackInSlot(slotIndex);
            UpgradeWrapperBase upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);
            if (!(upgradeWrapper instanceof CraftingUpgradeWrapper craftingWrapper)) return;

            try {
                craftingWrapper.getStorage()
                    .setStackInSlot(9, buf.readItemStackFromBuffer());
            } catch (IOException ignored) {}
        }

        if (id == UPDATE_FILTERABLE || id == UPDATE_CRAFTING) {
            wrapper.syncToServer();
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        ItemStack stack = wrapper.getUpgradeHandler()
            .getStackInSlot(slotIndex);
        UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this.wrapper);

        switch (id) {

            case UPDATE_FILTERABLE: {
                if (wrapper instanceof IBasicFilterable upgrade) {
                    setDelegatedStackHandler(upgrade::getFilterItems);
                }
                break;
            }

            case UPDATE_CRAFTING: {
                if (wrapper instanceof CraftingUpgradeWrapper upgrade) {
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
