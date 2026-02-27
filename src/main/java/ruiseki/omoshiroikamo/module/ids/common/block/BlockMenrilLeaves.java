package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.IDsConfig;
import ruiseki.omoshiroikamo.core.block.tree.BlockLeavesOK;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;

public class BlockMenrilLeaves extends BlockLeavesOK {

    public BlockMenrilLeaves() {
        super(ModObject.blockMenrilLeaves.unlocalisedName);
        setTextureName("ids/menril_leaves");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return IDsBlocks.MENRIL_SAPLING.getItem();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);
        if (world instanceof World && !world.isRemote) {
            if (world.rand.nextInt(IDsConfig.berriesDropChance) == 0) {
                drops.add(new ItemStack(IDsItems.MENRIL_BERRIES.getItem()));
            }
        }
        return drops;
    }
}
