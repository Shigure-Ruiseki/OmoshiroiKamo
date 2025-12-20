package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Custom ItemBlock for Modifier blocks that displays tooltips from
 * BlockModifier.getTooltipLines()
 */
public class ItemBlockModifier extends ItemBlock {

    public ItemBlockModifier(Block block) {
        super(block);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        super.addInformation(stack, player, list, flag);
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof BlockModifier) {
            BlockModifier modifier = (BlockModifier) block;
            List<String> tooltipLines = modifier.getTooltipLines();
            list.addAll(tooltipLines);
        }
    }
}
