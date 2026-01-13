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

    private CowRenderCache renderCache = new CowRenderCache();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TEStall tile)) {
            return;
        }

        boolean hasCow = tile.hasCow();
        if (!hasCow || tile.getMaxProgress() == 0) {

            if (renderCache.hadCow && !hasCow) {
                renderCache.invalidate();
            }
            return;
        }

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

    private EntityCowsCow getCachedCow(TEStall tile) {
        int tileHashCode = tile.xCoord ^ (tile.yCoord << 8) ^ (tile.zCoord << 16);

        if (renderCache.cachedCow == null || renderCache.cachedHashCode != tileHashCode) {
            renderCache.cachedCow = tile.getCow(tile.getWorldObj());
            renderCache.cachedHashCode = tileHashCode;
            renderCache.hadCow = true;
        }

        return renderCache.cachedCow;
    }

    public void invalidateCache() {
        renderCache.invalidate();
    }
}
