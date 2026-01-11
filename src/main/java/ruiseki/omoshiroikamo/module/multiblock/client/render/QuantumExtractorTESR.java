package ruiseki.omoshiroikamo.module.multiblock.client.render;

import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

@SideOnly(Side.CLIENT)
public class QuantumExtractorTESR extends TileEntitySpecialRenderer {

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        TEQuantumExtractor miner = (TEQuantumExtractor) te;
        if (miner.isFormed()) {
            renderBeam(miner, x, y, z, partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderBeam(TEQuantumExtractor miner, double x, double y, double z, float partialTicks) {
        float beamProgress = miner.getBeamProgress();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        if (beamProgress > 0.0F) {
            Tessellator tessellator = Tessellator.instance;
            this.bindTexture(BEAM_TEXTURE);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);

            float worldTime = (float) miner.getWorldObj()
                .getTotalWorldTime() + partialTicks;
            float textureOffset = -worldTime * 0.2F - (float) MathHelper.floor_float(-worldTime * 0.1F);

            // Use cached beam segments instead of scanning every frame
            List<BeamSegment> segments = miner.getBeamSegments();
            for (BeamSegment segment : segments) {
                renderBeamSegment(
                    tessellator,
                    x,
                    y,
                    z,
                    worldTime,
                    textureOffset,
                    beamProgress,
                    segment.startY,
                    segment.height,
                    segment.color);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

    private void renderBeamSegment(Tessellator tessellator, double x, double y, double z, float worldTime,
        float textureOffset, float beamProgress, int yOffset, int height, float[] color) {
        double d3 = (double) worldTime * 0.025D * -1.5D;
        double beamWidth = 0.2D;

        // Use float color directly (like ET Futurum)
        float r = color[0];
        float g = color[1];
        float b = color[2];

        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * beamWidth;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * beamWidth;
        double d6 = 0.5D + Math.cos(d3 + Math.PI / 4D) * beamWidth;
        double d7 = 0.5D + Math.sin(d3 + Math.PI / 4D) * beamWidth;
        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * beamWidth;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * beamWidth;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * beamWidth;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * beamWidth;

        double segmentHeight = height * beamProgress;
        double startY = y - yOffset;
        double endY = startY - segmentHeight;

        double d14 = -1.0D + textureOffset;
        double d15 = segmentHeight * (0.5D / beamWidth) + d14;

        // Inner beam (solid core) - Full opacity like ET Futurum
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, 1.0F);
        tessellator.addVertexWithUV(x + d4, endY, z + d5, 1.0D, d15);
        tessellator.addVertexWithUV(x + d4, startY, z + d5, 1.0D, d14);
        tessellator.addVertexWithUV(x + d6, startY, z + d7, 0.0D, d14);
        tessellator.addVertexWithUV(x + d6, endY, z + d7, 0.0D, d15);
        tessellator.addVertexWithUV(x + d10, endY, z + d11, 1.0D, d15);
        tessellator.addVertexWithUV(x + d10, startY, z + d11, 1.0D, d14);
        tessellator.addVertexWithUV(x + d8, startY, z + d9, 0.0D, d14);
        tessellator.addVertexWithUV(x + d8, endY, z + d9, 0.0D, d15);
        tessellator.addVertexWithUV(x + d6, endY, z + d7, 1.0D, d15);
        tessellator.addVertexWithUV(x + d6, startY, z + d7, 1.0D, d14);
        tessellator.addVertexWithUV(x + d10, startY, z + d11, 0.0D, d14);
        tessellator.addVertexWithUV(x + d10, endY, z + d11, 0.0D, d15);
        tessellator.addVertexWithUV(x + d8, endY, z + d9, 1.0D, d15);
        tessellator.addVertexWithUV(x + d8, startY, z + d9, 1.0D, d14);
        tessellator.addVertexWithUV(x + d4, startY, z + d5, 0.0D, d14);
        tessellator.addVertexWithUV(x + d4, endY, z + d5, 0.0D, d15);
        tessellator.draw();

        // Outer beam (transparent glow) - like ET Futurum
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);

        double outerWidth = 0.25D;
        double o3 = 0.5D - outerWidth;
        double o4 = 0.5D - outerWidth;
        double o5 = 0.5D + outerWidth;
        double o6 = 0.5D - outerWidth;
        double o7 = 0.5D - outerWidth;
        double o8 = 0.5D + outerWidth;
        double o9 = 0.5D + outerWidth;
        double o10 = 0.5D + outerWidth;

        double d24 = -1.0D + textureOffset;
        double d26 = segmentHeight + d24;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, 0.125F);
        tessellator.addVertexWithUV(x + o3, endY, z + o4, 1.0D, d26);
        tessellator.addVertexWithUV(x + o3, startY, z + o4, 1.0D, d24);
        tessellator.addVertexWithUV(x + o5, startY, z + o6, 0.0D, d24);
        tessellator.addVertexWithUV(x + o5, endY, z + o6, 0.0D, d26);
        tessellator.addVertexWithUV(x + o9, endY, z + o10, 1.0D, d26);
        tessellator.addVertexWithUV(x + o9, startY, z + o10, 1.0D, d24);
        tessellator.addVertexWithUV(x + o7, startY, z + o8, 0.0D, d24);
        tessellator.addVertexWithUV(x + o7, endY, z + o8, 0.0D, d26);
        tessellator.addVertexWithUV(x + o5, endY, z + o6, 1.0D, d26);
        tessellator.addVertexWithUV(x + o5, startY, z + o6, 1.0D, d24);
        tessellator.addVertexWithUV(x + o9, startY, z + o10, 0.0D, d24);
        tessellator.addVertexWithUV(x + o9, endY, z + o10, 0.0D, d26);
        tessellator.addVertexWithUV(x + o7, endY, z + o8, 1.0D, d26);
        tessellator.addVertexWithUV(x + o7, startY, z + o8, 1.0D, d24);
        tessellator.addVertexWithUV(x + o3, startY, z + o4, 0.0D, d24);
        tessellator.addVertexWithUV(x + o3, endY, z + o4, 0.0D, d26);
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

}
