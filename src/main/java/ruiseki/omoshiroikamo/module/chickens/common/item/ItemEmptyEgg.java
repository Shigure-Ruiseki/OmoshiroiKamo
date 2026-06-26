package ruiseki.omoshiroikamo.module.chickens.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

/**
 * Transitional item used during liquid egg consumption.
 * It automatically removes itself from any inventory it's in.
 */
public class ItemEmptyEgg extends ItemOK {

    public ItemEmptyEgg() {
        super(ModObject.EMPTY_EGG.name);
        setMaxStackSize(64);
        setTextureName(Reference.PREFIX_MOD + "chicken/liquid_egg"); // Placeholder if no dedicated icon
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote) return;

        // Set stack size to 0 to signal cleanup
        stack.stackSize = 0;

        // If it's in a player inventory, clear it directly
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.inventory.getStackInSlot(slot) == stack) {
                player.inventory.setInventorySlotContents(slot, null);
            }
        }
    }
}
