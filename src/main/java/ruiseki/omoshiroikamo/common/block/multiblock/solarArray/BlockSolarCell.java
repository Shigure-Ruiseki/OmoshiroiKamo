package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.render.block.solarArray.SolarCellISBRH;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockSolarCell extends BlockOK {

    @SideOnly(Side.CLIENT)
    IIcon top, side;

    protected BlockSolarCell() {
        super(ModObject.blockSolarCell);
    }

    public static BlockSolarCell create() {
        return new BlockSolarCell();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public int getRenderType() {
        return SolarCellISBRH.renderId;
    }

    @Override
    public void registerBlockIcons(IIconRegister iIconRegister) {
        top = iIconRegister.registerIcon(LibResources.PREFIX_MOD + "solar_cell_top");
        side = iIconRegister.registerIcon(LibResources.PREFIX_MOD + "basalt");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return top;
        }
        return this.side;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0f, 0f, 0f, 1f, 13f / 16f, 1f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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
