package ruiseki.omoshiroikamo.module.backpack.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IVoidUpgrade;

public class TEBackpack extends AbstractTE implements ISidedInventory, IGuiHolder<SidedPosGuiData> {

    private final int[] allSlots;
    private final BackpackHandler handler;

    public TEBackpack() {
        this(BackpackConfig.obsidianBackpackSlots, BackpackConfig.obsidianUpgradeSlots);
    }

    public TEBackpack(int slots, int upgradeSlots) {
        handler = new BackpackHandler(null, this, slots, upgradeSlots);
        allSlots = new int[handler.getSlots()];
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
        if (!handler.canInsert(stack)) {
            return false;
        }
        ItemStack existing = handler.getStackInSlot(slot);
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
        ItemStack existing = handler.getStackInSlot(slot);
        if (existing == null || existing.stackSize < stack.stackSize) {
            return false;
        }
        if (!handler.canExtract(stack)) {
            return false;
        }
        return stack.getItem() == existing.getItem();
    }

    @Override
    public int getSizeInventory() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return null;
        }
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 0 || slot >= getSizeInventory()) {
            return null;
        }
        ItemStack fromStack = handler.getStackInSlot(slot);
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            handler.setStackInSlot(slot, null);
            return fromStack;
        }
        ItemStack result = fromStack.splitStack(amount);
        handler.setStackInSlot(slot, fromStack.stackSize > 0 ? fromStack : null);
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
            handler.setStackInSlot(slot, null);
            return;
        }

        if (handler.canVoid(stack, IVoidUpgrade.VoidType.ANY, IVoidUpgrade.VoidInput.AUTOMATION)
            || handler.canVoid(stack, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.AUTOMATION)) {

            handler.setStackInSlot(slot, null);
            return;
        }

        ItemStack copy = stack.copy();
        if (copy.stackSize > getInventoryStackLimit()) {
            copy.stackSize = getInventoryStackLimit();
        }

        handler.setStackInSlot(slot, copy);
    }

    @Override
    public String getInventoryName() {
        return handler.getDisplayName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return handler.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64 * handler.getTotalStackMultiplier();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        handler.writeToNBT(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        handler.readFromNBT(root);
    }

    public int getMainColor() {
        return handler.getMainColor();
    }

    public int getAccentColor() {
        return handler.getAccentColor();
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        if (handler.canPlayerAccess(player.getUniqueID())) {
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
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BackpackGuiHolder.TileEntityGuiHolder(handler).buildUI(data, syncManager, settings);
    }
}
