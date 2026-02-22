package ruiseki.omoshiroikamo.core.datastructure;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Strings;

public class BlockPos extends com.gtnewhorizon.gtnhlib.blockpos.BlockPos {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    private static final int NUM_X_BITS = 26;
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos() {}

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public BlockPos(ChunkPosition chunkPosition) {
        super(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ);
    }

    public BlockPos(TileEntity tile) {
        this(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public BlockPos(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public BlockPos(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos(String x, String y, String z) {
        super(
            Strings.isNullOrEmpty(x) ? 0 : Integer.parseInt(x),
            Strings.isNullOrEmpty(y) ? 0 : Integer.parseInt(y),
            Strings.isNullOrEmpty(z) ? 0 : Integer.parseInt(z));
    }

    public BlockPos(String x, String y, String z, World world) {
        this(x, y, z);
    }

    public BiomeGenBase getBiomeGen(World world) {
        return world.getBiomeGenForCoords(getX(), getZ());
    }

    public void markBlockForUpdate(World world) {
        world.markBlockForUpdate(getX(), getY(), getZ());
    }

    public TileEntity getTileEntity(IBlockAccess world) {
        return world.getTileEntity(getX(), getY(), getZ());
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(getX(), getY(), getZ());
    }

    public int getBlockMetadata(IBlockAccess world) {
        return world.getBlockMetadata(getX(), getY(), getZ());
    }

    public BlockPos offset(ForgeDirection side) {
        return new BlockPos(getX() + side.offsetX, getY() + side.offsetY, getZ() + side.offsetZ);
    }

    public boolean equals(int x, int y, int z) {
        return x == getX() && y == getY() && z == getZ();
    }

    public boolean equals(TileEntity tile) {
        return tile.xCoord == getX() && tile.yCoord == getY() && tile.zCoord == getZ();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BlockPos other)) return false;
        if (!other.canEqual(this)) return false;
        return getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BlockPos;
    }

    public static int compareBlockPos(BlockPos pos1, BlockPos pos2) {
        int compX = Integer.compare(pos1.getX(), pos2.getX());
        if (compX == 0) {
            int compY = Integer.compare(pos1.getY(), pos2.getY());
            if (compY == 0) {
                return Integer.compare(pos1.getZ(), pos2.getZ());
            }
            return compY;
        }
        return compX;
    }

    public boolean isLoaded(World world) {
        if (world == null) return false;

        int x = getX();
        int y = getY();
        int z = getZ();

        return world.blockExists(x, y, z);
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

    public BlockPos add(ForgeDirection d) {
        add(d.offsetX, d.offsetY, d.offsetZ);
        return this;
    }

    public BlockPos sub(ForgeDirection d) {
        sub(d.offsetX, d.offsetY, d.offsetZ);
        return this;
    }

    public long toLong() {
        return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | (getZ() & Z_MASK);
    }

    public static BlockPos fromLong(long serialized) {
        int j = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int k = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int l = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(j, k, l);
    }

    public BlockPos readFromNBT(NBTTagCompound compound) {
        return new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("X", getZ());
        tag.setInteger("Y", getY());
        tag.setInteger("Z", getZ());
        return tag;
    }
}
