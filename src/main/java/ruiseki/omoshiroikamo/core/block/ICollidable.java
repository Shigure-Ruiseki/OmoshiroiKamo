package ruiseki.omoshiroikamo.core.block;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;

/**
 * Interface for blocks that have a collidable component.
 * Delegate calls to {@link CollidableComponent}.
 *
 * @param <P> The type of positions this component type can provide.
 * @author rubensworks
 */
public interface ICollidable<P> {

    /**
     * @return The colliding block instance
     */
    public Block getBlock();

    /**
     * Add the current block bounding box to the given list.
     *
     * @param world           The world
     * @param x,              y, z The position
     * @param mask            The bounding boxes mask
     * @param list            The list to add to
     * @param collidingEntity The entity that is colliding
     */
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collidingEntity);

    /**
     * The the selected bounding box.
     *
     * @param world The world
     * @param x,    y, z The position
     * @return The selected bounding box
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z);

    /**
     * Do a ray trace for the current look direction of the player.
     *
     * @param world  The world.
     * @param x,     y, z The block position to perform a ray trace for.
     * @param player The player.
     * @return A holder object with information on the ray tracing.
     */
    public RayTraceResult<P> doRayTrace(World world, int x, int y, int z, EntityPlayer player);

    /**
     * Ray trace the given direction.
     *
     * @param world     The world
     * @param x,        y, z The position
     * @param origin    The origin vector
     * @param direction The direction vector
     * @return The position object holder
     */
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction);

    /**
     * Result from ray tracing
     *
     * @param <P> The type of position that can be hit.
     */
    @Data
    public static class RayTraceResult<P> {

        private final MovingObjectPosition movingObjectPosition;
        private final AxisAlignedBB boundingBox;
        private final P positionHit;
        private final IComponent<P, ?> collisionType;

        @Override
        public String toString() {
            return String.format("RayTraceResult: %s %s", boundingBox, collisionType);
        }
    }

    /**
     * A component that can be part of the collision detection for a block.
     *
     * @param <P> The type of positions this component type can provide.
     * @param <B> The type of block this component is part of.
     */
    public static interface IComponent<P, B> {

        public Collection<P> getPossiblePositions();

        public int getBoundsCount(P position);

        public boolean isActive(B block, World world, int x, int y, int z, P position);

        public List<AxisAlignedBB> getBounds(B block, World world, int x, int y, int z, P position);

        public ItemStack getPickBlock(World world, int x, int y, int z, P position);

        /**
         * Destroy this component
         *
         * @param world    The world
         * @param x,       y, z The position
         * @param position The component position
         * @param player   The player destroying the component.
         * @return If the complete block was destroyed
         */
        public boolean destroy(World world, int x, int y, int z, P position, EntityPlayer player);
    }

}
