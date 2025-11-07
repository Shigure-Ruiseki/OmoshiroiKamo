package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.core;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.BlockOK;

public class BlockLaserCore extends BlockOK {

    protected BlockLaserCore() {
        super(ModObject.blockLaserCore, TELaserCore.class);
        setTextureName("laser_core");
    }

    public static BlockLaserCore create() {
        return new BlockLaserCore();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TELaserCore();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

}
