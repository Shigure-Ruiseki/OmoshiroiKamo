package ruiseki.omoshiroikamo.common.block.backpack;

import com.gtnewhorizon.gtnhlib.capability.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;
import com.gtnewhorizon.gtnhlib.capability.item.ItemIO;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSource;

import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;
import ruiseki.omoshiroikamo.common.util.item.capability.OKItemIO;

public class TEBackpack extends AbstractTE implements ISidedInventory, IGuiHolder<PosGuiData>, CapabilityProvider {

    private final int[] allSlots;
    private final int slots;
    private final int upgradeSlots;

    public TEBackpack() {
        this(27, 1);
    }

    public TEBackpack(int slots, int upgradeSlots) {
        this.slots = slots;
        this.upgradeSlots = upgradeSlots;
        BackpackHandler handler = Capabilities.getCapability(this, BackpackHandler.class);
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
        ItemStack existing = getHandler().getStackInSlot(slot);
        if (existing != null) {
            return ItemUtils.areStackMergable(existing, stack);
        }
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        ItemStack existing = getHandler().getStackInSlot(slot);
        if (existing == null || existing.stackSize < stack.stackSize) {
            return false;
        }
        return stack.getItem() == existing.getItem();
    }

    @Override
    public int getSizeInventory() {
        return getHandler().getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getHandler().getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack fromStack = getHandler().getStackInSlot(slot);
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            getHandler().setStackInSlot(slot, null);
            return fromStack;
        }
        ItemStack result = fromStack.splitStack(amount);
        getHandler().setStackInSlot(slot, fromStack.stackSize > 0 ? fromStack : null);
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        if (contents == null) {
            getHandler().setStackInSlot(slot, null);
        } else {
            getHandler().setStackInSlot(slot, contents.copy());
        }

        ItemStack stack = getHandler().getStackInSlot(slot);
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
            getHandler().setStackInSlot(slot, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return "Backpack";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64 * getHandler().getTotalStackMultiplier();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    protected void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        getHandler().writeToNBT(root);
    }

    @Override
    protected void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        getHandler().readFromNBT(root);
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
                                    float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BackpackGuiHolder.TileEntityGuiHolder(getHandler()).buildUI(data, syncManager, settings);
    }

    private BackpackHandler handler;

    private BackpackHandler getHandler() {
        if (handler == null) {
            handler = new BackpackHandler(null, this, slots, upgradeSlots);
        }
        return handler;
    }

    @Override
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        if (capability == ItemSource.class || capability == ItemSink.class || capability == ItemIO.class) {
            return capability.cast(new OKItemIO(this, side));
        }
        if (capability == BackpackHandler.class) {
            return capability.cast(new BackpackHandler(null, this, slots, upgradeSlots));
        }
        return null;
    }
}
