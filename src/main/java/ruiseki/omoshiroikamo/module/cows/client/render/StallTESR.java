package ruiseki.omoshiroikamo.module.cows.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

@SideOnly(Side.CLIENT)
public class StallTESR extends TileEntitySpecialRenderer {

    @SideOnly(Side.CLIENT)
    private EntityCowsCow cachedCow;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TEStall tile)) {
            return;
        }

        if (tile.hasCow() && tile.getMaxProgress() != 0) {

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.1, z + 0.5);
            GL11.glRotatef(getRotationAngle(tile.getFacing()), 0F, 1F, 0F);
            GL11.glRotatef(180, 0F, 1F, 0F);
            GL11.glScalef(0.5f, 0.5f, 0.5f);

            RenderManager.instance.renderEntityWithPosYaw(tile.getCow(te.getWorldObj()), 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
        }

    }

    private float getRotationAngle(int facing) {
        switch (facing) {
            case 0:
                return 270;
            case 1:
                return 90f;
            case 2:
                return 0f;
            case 3:
                return 180f;
        }
        return 0f;
    }
}
