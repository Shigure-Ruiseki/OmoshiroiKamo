package ruiseki.omoshiroikamo.client.render.block.quantumExtractor;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;

@SideOnly(Side.CLIENT)
public class QuantumExtractorTESR extends TileEntitySpecialRenderer {

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        TEQuantumExtractor voidOreMiner = (TEQuantumExtractor) te;
        if (voidOreMiner.isFormed()) {
            renderBeam(voidOreMiner, x, y, z, partialTicks);
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

            float f2 = (float) miner.getWorldObj()
                .getTotalWorldTime() + partialTicks;
            float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
            byte b0 = 1;
            double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(255, 255, 255, 32);
            double d5 = (double) b0 * 0.2D;

            double[][] corners = {
                { 0.5 + Math.cos(d3 + 2.356194490192345D) * d5, 0.5 + Math.sin(d3 + 2.356194490192345D) * d5 },
                { 0.5 + Math.cos(d3 + Math.PI / 4D) * d5, 0.5 + Math.sin(d3 + Math.PI / 4D) * d5 },
                { 0.5 + Math.cos(d3 + 3.9269908169872414D) * d5, 0.5 + Math.sin(d3 + 3.9269908169872414D) * d5 },
                { 0.5 + Math.cos(d3 + 5.497787143782138D) * d5, 0.5 + Math.sin(d3 + 5.497787143782138D) * d5 } };

            double beamHeight = 256.0 * beamProgress;
            float u1 = 0, u2 = 1;
            float v1 = 0, v2 = (float) beamHeight;
            double d28 = -1.0F + f3;
            double d29 = beamHeight * (0.5 / d5) + d28;

            for (int i = 0; i < 4; i++) {
                int next = (i + 1) % 4;
                tessellator.addVertexWithUV(x + corners[i][0], y - beamHeight, z + corners[i][1], u2, v2);
                tessellator.addVertexWithUV(x + corners[i][0], y, z + corners[i][1], u2, v1);
                tessellator.addVertexWithUV(x + corners[next][0], y, z + corners[next][1], u1, v1);
                tessellator.addVertexWithUV(x + corners[next][0], y - beamHeight, z + corners[next][1], u1, v2);
            }
            tessellator.draw();

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDepthMask(false);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(255, 255, 255, 32);

            double[][] innerCorners = { { 0.2, 0.2 }, { 0.8, 0.2 }, { 0.8, 0.8 }, { 0.2, 0.8 } };
            double beamHeight2 = 256.0 * beamProgress;
            double d24 = -1.0F + f3;
            double d26 = beamHeight2 + d24;

            for (int i = 0; i < 4; i++) {
                int next = (i + 1) % 4;
                tessellator.addVertexWithUV(x + innerCorners[i][0], y - beamHeight2, z + innerCorners[i][1], u2, d26);
                tessellator.addVertexWithUV(x + innerCorners[i][0], y, z + innerCorners[i][1], u2, d24);
                tessellator.addVertexWithUV(x + innerCorners[next][0], y, z + innerCorners[next][1], u1, d24);
                tessellator
                    .addVertexWithUV(x + innerCorners[next][0], y - beamHeight2, z + innerCorners[next][1], u1, d26);
            }

            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

}
