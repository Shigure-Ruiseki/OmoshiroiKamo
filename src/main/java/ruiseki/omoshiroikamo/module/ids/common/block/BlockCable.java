package ruiseki.omoshiroikamo.module.ids.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;

import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.property.AutoBlockProperty;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.module.ids.common.tileentity.TileMultipartTicking;

public class BlockCable extends BlockOK implements ICableConnectable {

    @AutoBlockProperty
    public static final BlockProperty<Boolean>[] CONNECTED = new BooleanBlockProperty[6];
    static {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            CONNECTED[side.ordinal()] = new BooleanBlockProperty() {

                @Override
                public String getName() {
                    return "connected_" + side.name();
                }

                @Override
                public boolean hasTrait(BlockPropertyTrait trait) {
                    return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.WorldMutable;
                }
            };
        }
    }

    public BlockCable() {
        super("cable_1", TileMultipartTicking.class, Material.glass);
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public BlockState getBlockState(IBlockAccess world, int x, int y, int z) {
        return ((TileMultipartTicking) world.getTileEntity(x, y, z)).getConnectionState();
    }

    @Override
    public BlockState updateConnections(World world, BlockPos pos) {
        TileMultipartTicking tile = (TileMultipartTicking) pos.getTileEntity(world);
        if (tile != null) {
            BlockState extendedState = getBlockState(world, pos).clone();
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                BlockPos neighbourPos = pos.offset(side);
                Block neighbourBlock = pos.getBlock(world);
                if (neighbourBlock instanceof ICableConnectable
                    && ((ICableConnectable) neighbourBlock).canConnect(world, neighbourPos, this, pos)) {
                    extendedState.setPropertyValue(CONNECTED[side.ordinal()], true);
                }
            }
            tile.setConnectionState(extendedState);
            world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
            return extendedState;
        }
        return null;
    }

    protected void requestConnectionsUpdate(World world, BlockPos pos) {
        TileMultipartTicking tile = (TileMultipartTicking) pos.getTileEntity(world);
        if (tile != null) {
            tile.setConnectionState(null);
        }
        world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        world.notifyBlockOfNeighborChange(x, y, z, this);
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            requestConnectionsUpdate(world, new BlockPos(x, y, z).offset(side));
        }
        super.onBlockPlacedBy(world, x, y, z, placer, itemIn);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        if (neighbor instanceof ICableConnectable) {
            requestConnectionsUpdate(world, new BlockPos(x, y, z));
        }
    }

    @Override
    public boolean canConnect(World world, BlockPos selfPosition, ICableConnectable connector, BlockPos otherPosition) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }
}
