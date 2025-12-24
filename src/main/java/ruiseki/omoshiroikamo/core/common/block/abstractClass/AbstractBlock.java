package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaBlockInfoProvider;

public abstract class AbstractBlock<T extends AbstractTE> extends BlockOK implements IWailaBlockInfoProvider {

    protected AbstractBlock(String name, Class<T> teClass, Material mat) {
        super(name, teClass, mat);
        setHardness(2.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
    }

    protected AbstractBlock(String name, Class<T> teClass) {
        this(name, teClass, Material.iron);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return false;
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {
        if (te != null) {
            ((AbstractTE) te).processDrop(world, x, y, z, te, stack);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof AbstractTE) {
            return ((AbstractTE) tile)
                .onBlockActivated(world, player, ForgeDirection.getOrientation(side), hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        AbstractTE te = (AbstractTE) world.getTileEntity(x, y, z);
        te.readFromItemStack(stack);
        te.setFacing(getFacingForHeading(heading));
        if (world.isRemote) {
            return;
        }
        world.markBlockForUpdate(x, y, z);
    }

    protected int getFacingForHeading(int heading) {
        int[] map = { 2, 0, 3, 1 };
        return map[heading];
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        TileEntity ent = world.getTileEntity(x, y, z);
        if (ent instanceof AbstractTE te) {
            te.onNeighborBlockChange(world, x, y, z, blockId);
        }
    }

    public boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (te instanceof AbstractTE) {
            return ((AbstractTE) te).isActive();
        }
        return false;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {

    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }
}
