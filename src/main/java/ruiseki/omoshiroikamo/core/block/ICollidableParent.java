package ruiseki.omoshiroikamo.core.block;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Interface used to access the parent methods from a {@link ICollidable}.
 *
 * @author rubensworks
 */
public interface ICollidableParent {

    /**
     * Simply forward this call to the super.
     *
     * @param worldIn         The world
     * @param x,              y, z The position
     * @param mask            The bounding boxes mask
     * @param list            The list to add to
     * @param collidingEntity The entity that is colliding
     */
    public void addCollisionBoxesToListParent(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collidingEntity);

    /**
     * Simply forward this call to the super.
     *
     * @param worldIn The world
     * @param x,      y, z The position
     * @return The selected bounding box
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPoolParent(World worldIn, int x, int y, int z);

    /**
     * Simply forward this call to the super.
     *
     * @param world     The world
     * @param x,        y, z The position
     * @param origin    The origin vector
     * @param direction The direction vector
     * @return The position object holder
     */
    public MovingObjectPosition collisionRayTraceParent(World world, int x, int y, int z, Vec3 origin, Vec3 direction);

}
