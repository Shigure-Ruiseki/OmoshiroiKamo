package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * External Item Port Proxy.
 * Adapts an external IInventory (like chests) to be used as a modular port.
 *
 * Design Pattern: Adapter Pattern
 * - Implements IInventory by delegating all calls to the target TileEntity
 * - Uses AbstractExternalProxy for common proxy functionality
 */
public class ExternalItemProxy extends AbstractExternalProxy implements IInventory {

    public ExternalItemProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    // ========== IInventory Implementation (Delegated) ==========

    @Override
    public int getSizeInventory() {
        return delegate(IInventory.class, IInventory::getSizeInventory, 0);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate(IInventory.class, inv -> inv.getStackInSlot(slot), null);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return delegate(IInventory.class, inv -> inv.decrStackSize(slot, amount), null);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return delegate(IInventory.class, inv -> inv.getStackInSlotOnClosing(slot), null);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        delegateVoid(IInventory.class, inv -> inv.setInventorySlotContents(slot, stack));
    }

    @Override
    public String getInventoryName() {
        return delegate(IInventory.class, IInventory::getInventoryName, "External Inventory Proxy");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return delegate(IInventory.class, IInventory::hasCustomInventoryName, false);
    }

    @Override
    public int getInventoryStackLimit() {
        return delegate(IInventory.class, IInventory::getInventoryStackLimit, 64);
    }

    @Override
    public void markDirty() {
        delegateVoid(IInventory.class, IInventory::markDirty);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return delegate(IInventory.class, inv -> inv.isUseableByPlayer(player), false);
    }

    @Override
    public void openInventory() {
        delegateVoid(IInventory.class, IInventory::openInventory);
    }

    @Override
    public void closeInventory() {
        delegateVoid(IInventory.class, IInventory::closeInventory);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return delegate(IInventory.class, inv -> inv.isItemValidForSlot(slot, stack), false);
    }
}
