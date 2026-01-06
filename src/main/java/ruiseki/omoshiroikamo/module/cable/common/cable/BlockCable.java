package ruiseki.omoshiroikamo.module.cable.common.cable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class BlockCable extends BlockOK {

    public static int rendererId = -1;

    public BlockCable() {
        super(ModObject.blockCable.unlocalisedName, TECable.class);
        this.setBaseBounds(6f / 16f, 6f / 16f, 6f / 16f, 10f / 16f, 10f / 16f, 10f / 16f);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, name);
        GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TECable();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + "cable/cable");
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public int getRenderType() {
        return rendererId;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.updateConnections();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onNeighborBlockChange(block);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onBlockRemoved();
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable bundle)) return false;
        return bundle.onBlockActivated(world, x, y, z, player, ForgeDirection.getOrientation(side), hitX, hitY, hitZ);
    }

    @Override
    protected ICustomCollision getCustomCollision(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICustomCollision collision) {
            return collision;
        }
        return null;
    }
}
