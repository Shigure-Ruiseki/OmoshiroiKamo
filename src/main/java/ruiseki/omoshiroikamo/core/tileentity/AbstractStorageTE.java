package ruiseki.omoshiroikamo.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.SmartTank;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.IItemSink;
import ruiseki.okcore.item.capability.IItemSource;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSink;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSource;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.network.PacketFluidTanks;
import ruiseki.omoshiroikamo.core.util.SlotDefinition;

public abstract class AbstractStorageTE extends AbstractTE implements ISidedInventory {

    protected final SlotDefinition slotDefinition;

    @NBTPersist("item_inv")
    public ItemStackHandlerBase inv;
    private final int[] allSlots;

    @NBTPersist("FluidTanks")
    public List<SmartTank> fluidTanks;
    protected boolean tanksDirty = false;

    protected LazyOptional<IItemSink> sinkCap = null;
    protected LazyOptional<IItemSource> sourceCap = null;

    public AbstractStorageTE(SlotDefinition slotDefinition) {
        this.slotDefinition = slotDefinition;

        inv = new ItemStackHandlerBase(slotDefinition.getItemSlots());
        inv.setOnSlotChanged((slot, stack) -> {
            markDirty();
            onContentsChange(slot);
        });

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
            return ItemStackHelpers.areStackMergable(existing, itemstack);
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

    public SlotDefinition getSlotDefinition() {
        return slotDefinition;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capability == CapabilityItemHandler.ITEM_SINK_CAPABILITY) {
            if (sinkCap == null) {
                IItemSink sink = new InventoryItemSink(this, facing);
                sink.setAllowedSinkSlots(slotDefinition.getItemInputSlots());
                sinkCap = LazyOptional.of(() -> sink);
            }
            return sinkCap.cast();
        }

        if (capability == CapabilityItemHandler.ITEM_SOURCE_CAPABILITY) {
            if (sourceCap == null) {
                IItemSource source = new InventoryItemSource(this, facing);
                source.setAllowedSourceSlots(slotDefinition.getItemOutputSlots());
                sourceCap = LazyOptional.of(() -> source);
            }
            return sourceCap.cast();
        }

        return super.getCapability(capability, facing);
    }
}
