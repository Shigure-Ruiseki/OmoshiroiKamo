package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaBlockInfoProvider;

public abstract class AbstractBlock<T extends AbstractTE> extends BlockOK implements IWailaBlockInfoProvider {
    // TODO: Change block meta to extendedFacing for all the tileentities

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
    public void init() {
        BlockStateUtils.registerFacingProp(this.getClass());
        super.init();
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
        AbstractTE te = (AbstractTE) world.getTileEntity(x, y, z);
        te.readFromItemStack(stack);
        world.markBlockForUpdate(x, y, z);

        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        ForgeDirection facing = getDirectionForHeading(heading);
        BlockStateUtils.setFacingProp(world, x, y, z, facing);
    }

    private ForgeDirection getDirectionForHeading(int heading) {
        return switch (heading) {
            case 0 -> NORTH;
            case 1 -> SOUTH;
            case 2 -> WEST;
            case 3 -> EAST;
            default -> NORTH;
        };
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
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

    // Util Method
    public static void dropStacks(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof AbstractStorageTE te) {
            for (int i = 0; i < te.getSizeInventory(); i++) {
                ItemStack stack = te.getStackInSlot(i);
                if (stack != null) {
                    dropStack(world, x, y, z, stack);
                }
            }
        }
    }

    // Util Method
    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        float dx = world.rand.nextFloat() * 0.8F + 0.1F;
        float dy = world.rand.nextFloat() * 0.8F + 0.1F;
        float dz = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, stack.copy());

        float motion = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * motion;
        entityItem.motionY = world.rand.nextGaussian() * motion + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * motion;

        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

    }
}
