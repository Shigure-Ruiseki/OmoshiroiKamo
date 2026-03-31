package ruiseki.omoshiroikamo.core.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

/**
 * Base block for all fluids in the mod.
 * Extends Forge's BlockFluidClassic and adds:
 * - drag: velocity reduction per tick while an entity is inside
 * - applyFluidEffect: hook for toxic/magical effects (subclass or override)
 */
public abstract class BlockFluidBase extends BlockFluidClassic {

    /** Velocity multiplied by (1 - drag) each tick. 0 = no resistance, 1 = instant stop. */
    private float drag = 0.0f;

    public BlockFluidBase(Fluid fluid, Material material) {
        super(fluid, material);
    }

    public BlockFluidBase setDrag(float drag) {
        this.drag = drag;
        return this;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
        // Apply drag on server for mobs; on both sides for players (client-predicted movement)
        if (drag > 0.0f && (!world.isRemote || entity instanceof EntityPlayer)) {
            // Cap horizontal speed to (1 - drag) * base walking speed.
            // Applied after movement so it overrides re-acceleration from player input.
            double maxHorizSpeed = 0.2 * (1.0 - drag);
            double horizSpeed = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
            if (horizSpeed > maxHorizSpeed) {
                double scale = maxHorizSpeed / horizSpeed;
                entity.motionX *= scale;
                entity.motionZ *= scale;
            }
            // Vertical: drag multiplication (slows falling / upward movement)
            entity.motionY *= (1.0 - drag);
        }
        applyFluidEffect(world, x, y, z, entity);
    }

    /**
     * Hook for applying custom effects to entities inside the fluid.
     * Override this in subclasses to add toxicity, buffs, etc.
     */
    protected void applyFluidEffect(World world, int x, int y, int z, Entity entity) {
        // To be implemented in subclasses or specific materials
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);
    }
}
