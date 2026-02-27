package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.Random;

import net.minecraft.item.Item;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.tree.BlockLeavesOK;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;

public class BlockMenrilLeaves extends BlockLeavesOK {

    public BlockMenrilLeaves() {
        super(ModObject.blockMenrilLeaves.unlocalisedName);
        setTextureName("ids/menril_leaves");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return IDsBlocks.MENRIL_SAPLING.getItem();
    }
}
