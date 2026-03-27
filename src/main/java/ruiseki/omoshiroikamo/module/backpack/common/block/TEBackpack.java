package ruiseki.omoshiroikamo.module.backpack.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IVoidUpgrade;

public class TEBackpack extends AbstractTE implements ISidedInventory, IGuiHolder<SidedPosGuiData> {

    private int[] allSlots;

    private BackpackWrapper wrapper;

    @NBTPersist
    private boolean sleepingBagDeployed;
    @NBTPersist
    private int sbx;
    @NBTPersist
    private int sby;
    @NBTPersist
    private int sbz;

    public TEBackpack() {
        this.wrapper = new BackpackWrapper();
    }

    public void setWrapper(BackpackWrapper wrapper) {
        this.wrapper = wrapper;
        allSlots = new int[wrapper.getSlots()];
        for (int i = 0; i < allSlots.length; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return false;
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

    public int getMainColor() {
        return wrapper.getMainColor();
    }

    public int getAccentColor() {
        return wrapper.getAccentColor();
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        if (wrapper.canPlayerAccess(player.getUniqueID())) {
            openGui(player);
        }
        return true;
    }

    @Override
    public void openGui(EntityPlayer player) {
        if (!worldObj.isRemote) {
            GuiFactories.sidedTileEntity()
                .open(player, xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN);
        }
    }

    @Override
    public ModularScreen createScreen(SidedPosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BackpackGuiHolder.TileEntityGuiHolder(wrapper).buildUI(data, syncManager, settings);
    }

    public boolean isSleepingBagDeployed() {
        return this.sleepingBagDeployed;
    }

    public void setSleepingBagDeployed(boolean state) {
        this.sleepingBagDeployed = state;
        markDirty();
        onSendUpdate();
    }

    public boolean deploySleepingBag(EntityPlayer player, World world, int meta, int cX, int cY, int cZ) {
        if (world.isRemote) return false;

        sleepingBagDeployed = BlockSleepingBag.spawnSleepingBag(player, world, meta, cX, cY, cZ);
        if (sleepingBagDeployed) {
            sbx = cX;
            sby = cY;
            sbz = cZ;
            markDirty();
            onSendUpdate();
        }
        return sleepingBagDeployed;
    }

    public void removeSleepingBag(World world) {
        if (sleepingBagDeployed && world.getBlock(sbx, sby, sbz) == BackpackBlocks.SLEEPING_BAG.getBlock())
            world.func_147480_a(sbx, sby, sbz, false);

        sleepingBagDeployed = false;
        markDirty();
    }
}
