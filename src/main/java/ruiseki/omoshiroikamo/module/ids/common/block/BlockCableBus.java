package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.part.ICableBusContainer;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.helper.TileHelpers;
import ruiseki.omoshiroikamo.module.ids.common.part.NullCableBusContainer;
import ruiseki.omoshiroikamo.module.ids.common.tileentity.TileCableBus;

public class BlockCableBus extends BlockOK {

    static private final ICableBusContainer nullCB = new NullCableBusContainer();

    public BlockCableBus() {
        super("cable_bus", TileCableBus.class, Material.glass);
        setLightOpacity(0);
    }

    @Override
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random r) {
        this.cb(world, x, y, z)
            .randomDisplayTick(world, x, y, z, r);
    }

    @Override
    public void onNeighborBlockChange(final World w, final int x, final int y, final int z, final Block meh) {
        this.cb(w, x, y, z)
            .onNeighborChanged();
    }

    @Override
    public int isProvidingWeakPower(final IBlockAccess w, final int x, final int y, final int z, final int side) {
        return this.cb(w, x, y, z)
            .isProvidingWeakPower(
                ForgeDirection.getOrientation(side)
                    .getOpposite());
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(final World w, final int x, final int y, final int z, final Entity e) {
        this.cb(w, x, y, z)
            .onEntityCollision(e);
    }

    @Override
    public int isProvidingStrongPower(final IBlockAccess w, final int x, final int y, final int z, final int side) {
        return this.cb(w, x, y, z)
            .isProvidingStrongPower(
                ForgeDirection.getOrientation(side)
                    .getOpposite());
    }

    @Override
    public int getLightValue(final IBlockAccess world, final int x, final int y, final int z) {
        final Block block = world.getBlock(x, y, z);
        if (block != null && block != this) {
            return block.getLightValue(world, x, y, z);
        }
        if (block == null) {
            return 0;
        }
        return this.cb(world, x, y, z)
            .getLightValue();
    }

    @Override
    public boolean isLadder(final IBlockAccess world, final int x, final int y, final int z,
        final EntityLivingBase entity) {
        return this.cb(world, x, y, z)
            .isLadder(entity);
    }

    @Override
    public boolean isSideSolid(final IBlockAccess w, final int x, final int y, final int z, final ForgeDirection side) {
        return this.cb(w, x, y, z)
            .isSolidOnSide(side);
    }

    @Override
    public boolean isReplaceable(final IBlockAccess world, final int x, final int y, final int z) {
        return this.cb(world, x, y, z)
            .isEmpty();
    }

    @Override
    public boolean canConnectRedstone(final IBlockAccess w, final int x, final int y, final int z, final int side) {
        return switch (side) {
            case -1, 4 -> this.cb(w, x, y, z)
                .canConnectRedstone(EnumSet.of(ForgeDirection.UP, ForgeDirection.DOWN));
            case 0 -> this.cb(w, x, y, z)
                .canConnectRedstone(EnumSet.of(ForgeDirection.NORTH));
            case 1 -> this.cb(w, x, y, z)
                .canConnectRedstone(EnumSet.of(ForgeDirection.EAST));
            case 2 -> this.cb(w, x, y, z)
                .canConnectRedstone(EnumSet.of(ForgeDirection.SOUTH));
            case 3 -> this.cb(w, x, y, z)
                .canConnectRedstone(EnumSet.of(ForgeDirection.WEST));
            default -> false;
        };
    }

    @Override
    public void onNeighborChange(final IBlockAccess w, final int x, final int y, final int z, final int tileX,
        final int tileY, final int tileZ) {
        if (MinecraftHelpers.isServerSide()) {
            this.cb(w, x, y, z)
                .onNeighborChanged();
        }
    }

    private ICableBusContainer cb(IBlockAccess world, int x, int y, int z) {
        TileCableBus te = TileHelpers.getSafeTile(world, x, y, z, TileCableBus.class);
        ICableBusContainer out = null;

        if (te != null) {
            out = te.cb;
        }

        return out == null ? nullCB : out;
    }
}
