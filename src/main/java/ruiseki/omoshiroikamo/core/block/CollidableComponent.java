package ruiseki.omoshiroikamo.core.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;

/**
 * Component for blocks that require complex collision detection.
 *
 * @author rubensworks
 * @param <P> The type of positions this component type can provide.
 * @param <B> The type of block this component is part of.
 */
@Data
public class CollidableComponent<P, B extends BlockOK & ICollidableParent> implements ICollidable<P> {

    private final B block;
    private final List<IComponent<P, B>> components;
    private final int totalComponents;

    public CollidableComponent(B block, List<IComponent<P, B>> components) {
        this.block = block;
        this.components = components;

        int count = 0;
        for (IComponent<P, B> component : components) {
            for (P position : component.getPossiblePositions()) {
                count += component.getBoundsCount(position);
            }
        }
        this.totalComponents = count;
    }

    private void addComponentCollisionBoxesToList(IComponent<P, B> component, World world, int x, int y, int z,
        AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity collidingEntity) {
        for (P position : component.getPossiblePositions()) {
            if (component.isActive(getBlock(), world, x, y, z, position)) {
                for (AxisAlignedBB bb : component.getBounds(getBlock(), world, x, y, z, position)) {
                    setBlockBounds(bb);
                    getBlock().addCollisionBoxesToListParent(world, x, y, z, axisalignedbb, list, collidingEntity);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb,
        List<AxisAlignedBB> list, Entity collidingEntity) {
        // Add bounding boxes for all active components.
        for (IComponent<P, B> component : components) {
            addComponentCollisionBoxesToList(component, world, x, y, z, axisalignedbb, list, collidingEntity);
        }

        // Reset the bounding box to prevent any entity glitches.
        getBlock().setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        RayTraceResult<P> rayTraceResult = doRayTrace(world, x, y, z, Minecraft.getMinecraft().thePlayer);
        if (rayTraceResult != null && rayTraceResult.getBoundingBox() != null) {
            AxisAlignedBB box = rayTraceResult.getBoundingBox();
            return box.offset(x, y, z);
        }
        // Happens when client hovers away from a block.
        return ((ICollidableParent) getBlock()).getSelectedBoundingBoxFromPoolParent(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        RayTraceResult<P> raytraceResult = doRayTrace(world, x, y, z, origin, direction);
        if (raytraceResult == null) {
            return null;
        } else {
            return raytraceResult.getMovingObjectPosition();
        }
    }

    /**
     * Do a ray trace for the current look direction of the player.
     *
     * @param world  The world.
     * @param x,     y, z The block position to perform a ray trace for.
     * @param player The player.
     * @return A holder object with information on the ray tracing.
     */
    public RayTraceResult<P> doRayTrace(World world, int x, int y, int z, EntityPlayer player) {
        double reachDistance;
        if (player instanceof EntityPlayerMP) {
            reachDistance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        } else {
            reachDistance = 5;
        }

        double eyeHeight = player.getEyeHeight();

        Vec3 lookVec = player.getLookVec();
        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
        Vec3 direction = origin
            .addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);

        return doRayTrace(world, x, y, z, origin, direction);
    }

    private int doRayTraceComponent(IComponent<P, B> component, int countStart, World world, int x, int y, int z,
        Vec3 origin, Vec3 direction, MovingObjectPosition[] hits, AxisAlignedBB[] boxes, P[] sideHit,
        IComponent<P, B>[] components) {
        int i = countStart;
        for (P position : component.getPossiblePositions()) {
            if (component.isActive(getBlock(), world, x, y, z, position)) {
                int offset = 0;
                for (AxisAlignedBB bb : component.getBounds(getBlock(), world, x, y, z, position)) {
                    setBlockBounds(bb);
                    boxes[i + offset] = bb;
                    hits[i + offset] = getBlock().collisionRayTraceParent(world, x, y, z, origin, direction);
                    sideHit[i + offset] = position;
                    components[i + offset] = component;
                    offset++;
                }
            }
            i += component.getBoundsCount(position);
        }
        return i;
    }

    private RayTraceResult<P> doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        // Perform a ray trace for all six sides.
        MovingObjectPosition[] hits = new MovingObjectPosition[totalComponents];
        AxisAlignedBB[] boxes = new AxisAlignedBB[totalComponents];
        @SuppressWarnings("unchecked")
        P[] sideHit = (P[]) new Object[totalComponents];
        @SuppressWarnings("unchecked")
        IComponent<P, B>[] componentsOutput = new IComponent[totalComponents];
        Arrays.fill(sideHit, null);

        // Ray trace for all active components.
        int count = 0;
        for (IComponent<P, B> component : components) {
            count = doRayTraceComponent(
                component,
                count,
                world,
                x,
                y,
                z,
                origin,
                direction,
                hits,
                boxes,
                sideHit,
                componentsOutput);
        }

        // Find the closest hit
        double minDistance = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 0; i < hits.length; i++) {
            if (hits[i] != null) {
                double d = hits[i].hitVec.squareDistanceTo(origin);
                if (d < minDistance) {
                    minDistance = d;
                    minIndex = i;
                }
            }
        }

        // Reset bounds
        getBlock().setBlockBounds(0, 0, 0, 1, 1, 1);

        if (minIndex != -1) {
            return new RayTraceResult<P>(
                hits[minIndex],
                boxes[minIndex],
                sideHit[minIndex],
                componentsOutput[minIndex]);
        }
        return null;
    }

    private void setBlockBounds(AxisAlignedBB bounds) {
        getBlock().setBlockBounds(
            (float) bounds.minX,
            (float) bounds.minY,
            (float) bounds.minZ,
            (float) bounds.maxX,
            (float) bounds.maxY,
            (float) bounds.maxZ);
    }

}
