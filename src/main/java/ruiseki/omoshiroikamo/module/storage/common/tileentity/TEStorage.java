package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IVoidUpgrade;

public class TEStorage extends TileEntityOK
    implements TileEntityOK.ITickingTile, IGuiHolder<PosGuiData>, ISidedInventory {

    @Delegate
    private final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    private int[] allSlots;

    @NBTPersist(StorageWrapper.STORAGE_NBT)
    public StorageWrapper wrapper;

    public TEStorage() {
        this.wrapper = new StorageWrapper();
    }

    public void setWrapper(StorageWrapper wrapper) {
        this.wrapper = wrapper;
        allSlots = new int[wrapper.getSlots()];
        for (int i = 0; i < allSlots.length; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            GuiFactories.tileEntity()
                .open(player, x, y, z);
        }
        return true;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        int baseRowSize = wrapper.getSlots() > 81 ? 12 : 9;
        int width = 20 + baseRowSize * ItemSlot.SIZE;
        StoragePanel panel = new StoragePanel(data, syncManager, settings, this, wrapper, width);
        panel.addSortingButtons();
        panel.addTransferButtons();
        panel.addStorageInventorySlots();
        panel.addSearchBar();
        panel.addUpgradeSlots();
        panel.addSettingTab();
        panel.addUpgradeTabs();
        panel.addTexts();
        return panel;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return allSlots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return false;
        }
        if (!wrapper.canInsert(stack)) {
            return false;
        }
        ItemStack existing = wrapper.getStackInSlot(slot);
        if (existing != null) {
            return ItemUtils.areStackMergable(existing, stack);
        }
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return false;
        }
        ItemStack existing = wrapper.getStackInSlot(slot);
        if (existing == null || existing.stackSize < stack.stackSize) {
            return false;
        }
        if (!wrapper.canExtract(stack)) {
            return false;
        }
        return stack.getItem() == existing.getItem();
    }

    @Override
    public int getSizeInventory() {
        return wrapper.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return null;
        }
        return wrapper.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return null;
        }
        ItemStack fromStack = wrapper.getStackInSlot(slot);
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            wrapper.setStackInSlot(slot, null);
            return fromStack;
        }
        ItemStack result = fromStack.splitStack(amount);
        wrapper.setStackInSlot(slot, fromStack.stackSize > 0 ? fromStack : null);
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return;
        }

        if (stack == null) {
            wrapper.setStackInSlot(slot, null);
            return;
        }

        if (wrapper.canVoid(stack, IVoidUpgrade.VoidType.ANY, IVoidUpgrade.VoidInput.AUTOMATION)
            || wrapper.canVoid(stack, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.AUTOMATION)) {

            wrapper.setStackInSlot(slot, null);
            return;
        }

        ItemStack copy = stack.copy();
        if (copy.stackSize > getInventoryStackLimit()) {
            copy.stackSize = getInventoryStackLimit();
        }

        wrapper.setStackInSlot(slot, copy);
    }

    @Override
    public String getInventoryName() {
        return wrapper.getDisplayName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return wrapper.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64 * wrapper.getTotalStackMultiplier();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canInteractWith(player);
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}
