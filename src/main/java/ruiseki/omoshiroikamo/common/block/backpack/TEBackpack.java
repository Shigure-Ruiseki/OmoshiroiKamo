package ruiseki.omoshiroikamo.common.block.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiFactories;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IVoidUpgrade;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public class TEBackpack extends AbstractTE implements ISidedInventory, IGuiHolder<PosGuiData> {

    private final int[] allSlots;
    private final int slots;
    private final int upgradeSlots;
    private final BackpackHandler handler;

    public TEBackpack() {
        this(120, 7);
    }

    public TEBackpack(int slots, int upgradeSlots) {
        this.slots = slots;
        this.upgradeSlots = upgradeSlots;
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
    protected boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return allSlots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
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
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
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
    public void setInventorySlotContents(int slot, ItemStack contents) {
        if (contents == null) {
            handler.setStackInSlot(slot, null);
            return;
        }

        boolean isVoided = handleVoid(slot, contents, IVoidUpgrade.VoidInput.ALL);
        if (isVoided) return;

        handler.setStackInSlot(slot, contents.copy());
        ItemStack stack = handler.getStackInSlot(slot);
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
            handler.setStackInSlot(slot, stack);
        }
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
    protected void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        handler.writeToNBT(root);
    }

    @Override
    protected void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        handler.readFromNBT(root);
    }

    protected boolean handleVoid(int slot, ItemStack stack, IVoidUpgrade.VoidInput input) {
        if (handler == null || stack == null) return false;

        // Void ANY
        if (handler.canVoid(stack, IVoidUpgrade.VoidType.ANY, input)) {
            handler.setStackInSlot(slot, null);
            return true;
        }

        // Void OVERFLOW
        if (handler.canVoid(stack, IVoidUpgrade.VoidType.OVERFLOW, input)) {
            for (int i = 0; i < handler.getBackpackSlots(); i++) {
                ItemStack inSlot = handler.getStackInSlot(i);
                if (ItemUtils.areStackMergable(inSlot, stack)) {
                    int maxStack = Math.min(getInventoryStackLimit(), inSlot.getMaxStackSize());
                    int toAdd = Math.min(maxStack - inSlot.stackSize, stack.stackSize);
                    if (toAdd > 0) {
                        inSlot.stackSize += toAdd;
                        handler.setStackInSlot(i, inSlot);
                    }
                    stack.stackSize = 0;
                    return true;
                }
            }

            handler.setStackInSlot(slot, stack);
            return true;
        }

        return false;
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
            MGuiFactories.tileEntity()
                .open(player, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BackpackGuiHolder.TileEntityGuiHolder(handler).buildUI(data, syncManager, settings);
    }
}
