package ruiseki.omoshiroikamo.core.block;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

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

import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty;

import ruiseki.omoshiroikamo.core.block.property.AutoBlockProperty;
import ruiseki.omoshiroikamo.core.helper.BlockStateHelpers;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaBlockInfoProvider;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;

public abstract class AbstractBlock<T extends AbstractTE> extends BlockOK implements IWailaBlockInfoProvider {
    // TODO: Change block meta to extendedFacing for all the tileentities

    @AutoBlockProperty
    public static final DirectionBlockProperty FACING = DirectionBlockProperty.facing(0b0011, dir -> switch (dir) {
        case SOUTH -> 0;
        case EAST -> 1;
        case NORTH -> 2;
        case WEST -> 3;
        default -> 0;
    }, meta -> switch (meta & 0b0011) {
        case 0 -> SOUTH;
        case 1 -> EAST;
        case 2 -> NORTH;
        case 3 -> WEST;
        default -> NORTH;
    });

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
        ForgeDirection facing = getDirectionForHeading(heading);
        BlockStateHelpers.setFacingProp(world, x, y, z, facing);
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
}
