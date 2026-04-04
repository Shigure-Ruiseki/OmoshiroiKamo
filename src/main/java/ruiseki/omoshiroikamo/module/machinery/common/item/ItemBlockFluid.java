package ruiseki.omoshiroikamo.module.machinery.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Specialized ItemBlock for fluids.
 * Implements getColorFromItemStack to ensure tints are applied correctly in the
 * inventory/NEI.
 */
public class ItemBlockFluid extends ItemBlock {

    public ItemBlockFluid(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        // Apply the same tint as the block (using getRenderColor)
        return field_150939_a.getRenderColor(stack.getItemDamage());
    }
}
