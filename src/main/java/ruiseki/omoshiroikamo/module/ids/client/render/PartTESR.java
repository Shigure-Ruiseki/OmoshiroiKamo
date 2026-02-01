package ruiseki.omoshiroikamo.module.ids.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cable.TECable;

public class PartTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float pt) {
        if (!(tile instanceof TECable cable)) return;
        if (tile.getWorldObj() == null) return;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        Tessellator tess = TessellatorManager.get();

        for (ICablePart part : cable.getParts()) {
            part.renderPart(tess, pt);
        }

        GL11.glPopMatrix();
    }
}
