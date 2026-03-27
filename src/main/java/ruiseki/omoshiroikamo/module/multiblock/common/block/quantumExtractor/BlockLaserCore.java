package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.core.block.BlockOK;

public class BlockLaserCore extends BlockOK implements IMBBlock {

    protected BlockLaserCore() {
        super(ModObject.LASER_CORE.name);
        isFullSize = isOpaque = false;
    }

    public static BlockLaserCore create() {
        return new BlockLaserCore();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

}
