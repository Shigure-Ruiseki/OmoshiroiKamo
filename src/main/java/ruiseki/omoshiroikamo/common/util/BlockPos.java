package ruiseki.omoshiroikamo.common.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.base.Strings;
import com.gtnewhorizon.gtnhlib.blockpos.IWorldReferent;

public class BlockPos extends com.gtnewhorizon.gtnhlib.blockpos.BlockPos implements IWorldReferent {

    public World world = null;

    public BlockPos() {}

    public BlockPos(int x, int y, int z, World world) {
        super(x, y, z);
        this.world = world;
    }

    public BlockPos(double x, double y, double z) {
        super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public BlockPos(double x, double y, double z, World world) {
        super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
        this.world = world;

    }

    public BlockPos(ChunkPosition chunkPosition, World world) {
        super(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ);
        this.world = world;
    }

    public BlockPos(TileEntity tile) {
        this(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj());
    }

    public BlockPos(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ, entity.worldObj);
    }

    public BlockPos(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getWorld());
    }

    public BlockPos(String x, String y, String z) {
        super(
            Strings.isNullOrEmpty(x) ? 0 : Integer.parseInt(x),
            Strings.isNullOrEmpty(y) ? 0 : Integer.parseInt(y),
            Strings.isNullOrEmpty(z) ? 0 : Integer.parseInt(z));
    }

    public BlockPos(String x, String y, String z, World world) {
        this(x, y, z);
        this.world = world;
    }

    public BlockPos setWorld(World world) {
        this.world = world;
        return this;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    public Block getBlock() {
        return getWorld().getBlock(getX(), getY(), getZ());
    }

    public int getMetadata() {
        return getWorld().getBlockMetadata(getX(), getY(), getZ());
    }

    public TileEntity getTileEntity() {
        return getWorld().getTileEntity(getX(), getY(), getZ());
    }

    public BiomeGenBase getBiomeGen() {
        return getWorld().getBiomeGenForCoords(getX(), getZ());
    }

    public void markBlockForUpdate() {
        getWorld().markBlockForUpdate(getX(), getY(), getZ());
    }

    public boolean equals(int x, int y, int z) {
        return x == getX() && y == getY() && z == getZ();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BlockPos)) {
            return false;
        }
        final BlockPos other = (BlockPos) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        if (getX() != other.getX()) {
            return false;
        }
        if (getY() != other.getY()) {
            return false;
        }
        if (getZ() != other.getZ()) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BlockPos;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + getX();
        result = result * PRIME + getY();
        result = result * PRIME + getZ();
        return result;
    }

    public BlockPos withX(final int x) {
        return getX() == x ? this : new BlockPos(x, getY(), getZ());
    }

    public BlockPos withY(final int y) {
        return getY() == y ? this : new BlockPos(getX(), y, getZ());
    }

    public BlockPos withZ(final int z) {
        return getZ() == z ? this : new BlockPos(getX(), getY(), z);
    }
}
