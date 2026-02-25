package ruiseki.omoshiroikamo.core.datastructure;

import java.lang.ref.WeakReference;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.jetbrains.annotations.Nullable;

import lombok.Data;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;

/**
 * A simple data class for a block position inside a world.
 *
 * @author rubensworks
 */
@Data
public class DimPos implements Comparable<DimPos> {

    private final int dimensionId;
    private final BlockPos blockPos;
    private WeakReference<World> worldReference;

    private DimPos(int dimensionId, BlockPos blockPos, @Nullable World world) {
        this.dimensionId = dimensionId;
        this.blockPos = blockPos;
        this.worldReference = world != null ? new WeakReference<>(world) : null;
    }

    public static DimPos of(int dimensionId, int x, int y, int z) {
        return new DimPos(dimensionId, new BlockPos(x, y, z), null);
    }

    public static DimPos of(int dimensionId, BlockPos pos) {
        return new DimPos(dimensionId, pos, null);
    }

    public static DimPos of(World world, int x, int y, int z) {
        return new DimPos(world.provider.dimensionId, new BlockPos(x, y, z), world);
    }

    public static DimPos of(World world, BlockPos pos) {
        return new DimPos(world.provider.dimensionId, pos, world);
    }

    @Nullable
    public World getWorld() {
        if (worldReference != null) {
            World world = worldReference.get();
            if (world != null) {
                return world;
            }
        }

        World world = DimensionManager.getWorld(dimensionId);
        if (world != null) {
            worldReference = new WeakReference<>(world);
        }
        return world;
    }

    public boolean isLoaded() {
        World world = getWorld();
        return world != null && blockPos.isLoaded(world);
    }

    @Override
    public int compareTo(DimPos o) {
        int dimCompare = Integer.compare(dimensionId, o.dimensionId);
        if (dimCompare != 0) return dimCompare;

        return MinecraftHelpers.compareBlockPos(blockPos, o.blockPos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DimPos)) return false;
        return compareTo((DimPos) obj) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * dimensionId + blockPos.hashCode();
    }

    public int getX() {
        return blockPos.getX();
    }

    public int getY() {
        return blockPos.getY();
    }

    public int getZ() {
        return blockPos.getZ();
    }
}
