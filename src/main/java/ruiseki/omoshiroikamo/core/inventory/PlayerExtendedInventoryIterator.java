package ruiseki.omoshiroikamo.core.inventory;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.core.lib.LibMods;

/**
 * Iterate over a player's inventory and any other attached inventory like baubles.
 *
 * @author rubensworks
 */
@Optional.Interface(modid = "Baubles", iface = "baubles.api.IBauble")
public class PlayerExtendedInventoryIterator implements Iterator<ItemStack> {

    private PlayerInventoryIterator innerIt;
    private boolean hasIteratedInner = false;
    private int maxBaublesSize = 4;
    private int baublesIterator = maxBaublesSize;

    /**
     * Create a new HotbarIterator.
     *
     * @param player The player to iterate the hotbar from.
     */
    public PlayerExtendedInventoryIterator(EntityPlayer player) {
        innerIt = new PlayerInventoryIterator(player);
        if (LibMods.Baubles.isLoaded()) {
            setBaublesData();
        }
    }

    @Override
    public boolean hasNext() {
        return !hasIteratedInner || baublesIterator < maxBaublesSize;
    }

    @Override
    public ItemStack next() {
        if (hasIteratedInner && hasNext()) {
            ItemStack itemStack = getBaublesStack(baublesIterator);
            baublesIterator++;
            return itemStack;
        } else {
            ItemStack next = innerIt.next();
            if (!innerIt.hasNext()) {
                hasIteratedInner = true;
            }
            return next;
        }
    }

    @Optional.Method(modid = "Baubles")
    protected ItemStack getBaublesStack(int index) {
        return BaublesApi.getBaubles(innerIt.getPlayer())
            .getStackInSlot(index);
    }

    @Optional.Method(modid = "Baubles")
    protected void setBaublesStack(int index, ItemStack itemStack) {
        BaublesApi.getBaubles(innerIt.getPlayer())
            .setInventorySlotContents(index, itemStack);
    }

    @Optional.Method(modid = "Baubles")
    protected void setBaublesData() {
        maxBaublesSize = BaublesApi.getBaubles(innerIt.getPlayer())
            .getSizeInventory();
        baublesIterator = 0;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Replaces the itemstack on the position of the last returned itemstack.
     *
     * @param itemStack The itemstack to place.
     */
    public void replace(ItemStack itemStack) {
        if (hasIteratedInner && baublesIterator > 0) {
            setBaublesStack(baublesIterator - 1, itemStack);
        } else {
            innerIt.replace(itemStack);
        }
    }

}
