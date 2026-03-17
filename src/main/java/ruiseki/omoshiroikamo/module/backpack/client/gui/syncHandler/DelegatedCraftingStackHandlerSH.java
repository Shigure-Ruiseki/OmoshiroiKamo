package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.EmptyHandler;
import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackPackContainer;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.IndexedInventoryCraftingWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperFactory;

public class DelegatedCraftingStackHandlerSH extends DelegatedStackHandlerSH {

    public static final int UPDATE_CRAFTING = 1;

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

        UpgradeWrapper upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack);

        if (!(upgradeWrapper instanceof ICraftingUpgrade craftingWrapper)) {
            return;
        }

        inventoryCrafting.setCraftingDestination(craftingWrapper.getCraftingDes());

        inventoryCrafting.detectChanges();

        if (isValid() && !getSyncManager().isClient() && !(delegatedStackHandler.get() instanceof EmptyHandler)) {

            var result = delegatedStackHandler.get()
                .getStackInSlot(9);

            syncToClient(UPDATE_CRAFTING, buffer -> buffer.writeItemStackToBuffer(result));
        }
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {

        ItemStack stack = wrapper.getUpgradeHandler()
            .getStackInSlot(slotIndex);

        if (id == UPDATE_CRAFTING) {
            UpgradeWrapper upgradeWrapper = UpgradeWrapperFactory.createWrapper(stack);
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

        switch (id) {

            case UPDATE_FILTERABLE: {
                UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
                if (wrapper instanceof IBasicFilterable upgrade) {
                    setDelegatedStackHandler(upgrade::getFilterItems);
                }
                break;
            }

            case UPDATE_CRAFTING: {
                UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
                if (wrapper instanceof CraftingUpgradeWrapper upgrade) {
                    setDelegatedStackHandler(upgrade::getStorage);
                }
                break;
            }
        }
    }
}
