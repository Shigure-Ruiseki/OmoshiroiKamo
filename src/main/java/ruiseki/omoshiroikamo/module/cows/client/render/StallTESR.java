package ruiseki.omoshiroikamo.module.cows.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

@SideOnly(Side.CLIENT)
public class StallTESR extends TileEntitySpecialRenderer {

    /**
     * Cache for cow entity rendering per TileEntity.
     * Uses a simple approach: cache is invalidated when cow presence changes.
     */
    private static class CowRenderCache {

        EntityCowsCow cachedCow;
        boolean hadCow;
        int cachedHashCode;

        void invalidate() {
            cachedCow = null;
            hadCow = false;
            cachedHashCode = 0;
        }
    }

    // Single cache per TESR instance (one TESR per TE type)
    // For multiple stalls, we use TE's worldObj to detect changes
    private CowRenderCache renderCache = new CowRenderCache();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TEStall tile)) {
            return;
        }

        boolean hasCow = tile.hasCow();
        if (!hasCow || tile.getMaxProgress() == 0) {
            // Clear cache if cow was removed
            if (renderCache.hadCow && !hasCow) {
                renderCache.invalidate();
            }
            return;
        }

        // Get or update cached cow entity
        EntityCowsCow cow = getCachedCow(tile);
        if (cow == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.1, z + 0.5);
        ForgeDirection facing = tile.getFacing();
        GL11.glRotatef(BlockStateUtils.getFacingAngle(facing), 0F, 1F, 0F);
        GL11.glRotatef(180, 0F, 1F, 0F);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        RenderManager.instance.renderEntityWithPosYaw(cow, 0, 0, 0, 0, 0);
        GL11.glPopMatrix();
    }

    /**
     * Get cached cow entity, creating a new one only when necessary.
     * The cache is invalidated when the TE position changes (different stall)
     * or when cow data changes.
     */
    private EntityCowsCow getCachedCow(TEStall tile) {
        // Simple cache key based on TE position
        int tileHashCode = tile.xCoord ^ (tile.yCoord << 8) ^ (tile.zCoord << 16);

        // Check if we need to refresh the cache
        if (renderCache.cachedCow == null || renderCache.cachedHashCode != tileHashCode) {
            renderCache.cachedCow = tile.getCow(tile.getWorldObj());
            renderCache.cachedHashCode = tileHashCode;
            renderCache.hadCow = true;
        }

        return renderCache.cachedCow;
    }

    /**
     * Force cache invalidation when cow data changes.
     * Should be called from TileEntity when cow is set/removed.
     */
    public void invalidateCache() {
        renderCache.invalidate();
    }
}
