package ruiseki.omoshiroikamo.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSource;
import com.gtnewhorizon.gtnhlib.item.InventoryItemSink;
import com.gtnewhorizon.gtnhlib.item.InventoryItemSource;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.SlotDefinition;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.fluid.SmartTank;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.network.packet.PacketFluidTanks;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;

public abstract class AbstractStorageTE extends AbstractTE implements ISidedInventory, CapabilityProvider {

    protected final SlotDefinition slotDefinition;

    @NBTPersist("item_inv")
    public ItemStackHandlerBase inv;
    private final int[] allSlots;

    @NBTPersist("FluidTanks")
    public List<SmartTank> fluidTanks;
    protected boolean tanksDirty = false;

    public AbstractStorageTE(SlotDefinition slotDefinition) {
        this.slotDefinition = slotDefinition;

        inv = new ItemStackHandlerBase(slotDefinition.getItemSlots()) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                onContentsChange(slot);
            }
        };

        int fluidSlots = slotDefinition.getFluidSlots();
        fluidTanks = new ArrayList<>(fluidSlots);
        for (int i = 0; i < fluidSlots; i++) {
            fluidTanks.add(new SmartTank(8000));
        }

        allSlots = new int[inv.getSlots()];
        for (int i = 0; i < allSlots.length; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (tanksDirty && shouldDoWorkThisTick(10)) {
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(new PacketFluidTanks(this), this);
            tanksDirty = false;
            return true;
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return allSlots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        if (!slotDefinition.isInputSlot(slot)) {
            return false;
        }
        ItemStack existing = inv.getStackInSlot(slot);
        if (existing != null) {
            return ItemUtils.areStackMergable(existing, itemstack);
        }
        return isItemValidForSlot(slot, itemstack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        if (!slotDefinition.isOutputSlot(slot)) {
            return false;
        }
        ItemStack existing = inv.getStackInSlot(slot);
        if (existing == null || existing.stackSize < itemstack.stackSize) {
            return false;
        }
        return itemstack.getItem() == existing.getItem();
    }

    @Override
    public int getSizeInventory() {
        return inv.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= inv.getSlots()) {
            return null;
        }
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack fromStack = inv.getStackInSlot(fromSlot);
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            inv.setStackInSlot(fromSlot, null);
            return fromStack;
        }
        ItemStack result = fromStack.splitStack(amount);
        inv.setStackInSlot(fromSlot, fromStack.stackSize > 0 ? fromStack : null);
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        if (contents == null) {
            inv.setStackInSlot(slot, null);
        } else {
            inv.setStackInSlot(slot, contents.copy());
        }

        ItemStack stack = inv.getStackInSlot(slot);
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
            inv.setStackInSlot(slot, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return getLocalizedName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canInteractWith(player);
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public abstract boolean isItemValidForSlot(int slot, ItemStack stack);

    public void onContentsChange(int slot) {}

    @Override
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        if (capability == ItemSink.class) {
            ItemSink sink = new InventoryItemSink(this, side);
            sink.setAllowedSinkSlots(slotDefinition.getItemInputSlots());
            return capability.cast(sink);
        }

        if (capability == ItemSource.class) {
            ItemSource sink = new InventoryItemSource(this, side);
            sink.setAllowedSourceSlots(slotDefinition.getItemOutputSlots());
            return capability.cast(sink);
        }
        return null;
    }
}
