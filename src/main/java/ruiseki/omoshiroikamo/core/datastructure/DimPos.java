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
@Data(staticConstructor = "of")
public class DimPos implements Comparable<DimPos> {

    private final int dimensionId;
    private final BlockPos blockPos;
    private WeakReference<World> worldReference;

    private DimPos(int dimensionId, BlockPos blockPos, World world) {
        this.dimensionId = dimensionId;
        this.blockPos = blockPos;
        this.worldReference = world != null && world.isRemote ? new WeakReference<>(world) : null;
    }

    private DimPos(int dimensionId, BlockPos blockPos) {
        this(dimensionId, blockPos, null);
    }

    public @Nullable World getWorld() {
        if (worldReference == null) {
            return DimensionManager.getWorld(dimensionId);
        }
        World world = worldReference.get();
        if (world == null) {
            world = DimensionManager.getWorld(dimensionId);
            worldReference = new WeakReference<>(world);
        }
        return world;
    }

    public boolean isLoaded() {
        World world = getWorld();
        return world != null && getBlockPos().isLoaded(world);
    }

    @Override
    public int compareTo(DimPos o) {
        int compareDim = Integer.compare(getDimensionId(), o.getDimensionId());
        if (compareDim == 0) {
            return MinecraftHelpers.compareBlockPos(getBlockPos(), o.getBlockPos());
        }
        return compareDim;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DimPos && compareTo((DimPos) o) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * getDimensionId() + getBlockPos().hashCode();
    }

}
