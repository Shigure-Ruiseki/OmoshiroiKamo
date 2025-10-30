package ruiseki.omoshiroikamo.client.render.block.quantumExtractor;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class QuantumExtractorTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "void_miner.obj";

    private static final ResourceLocation BOTTOM = new ResourceLocation(LibResources.PREFIX_BLOCK + "basalt.png");
    private static final ResourceLocation LASER = new ResourceLocation(LibResources.PREFIX_BLOCK + "laser_core.png");
    private static final ResourceLocation PANEL = new ResourceLocation(LibResources.PREFIX_BLOCK + "cont_tier.png");
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    public QuantumExtractorTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        TEQuantumExtractor voidOreMiner = (TEQuantumExtractor) te;
        if (voidOreMiner.isFormed()) {
            renderBeam(voidOreMiner, x, y, z, partialTicks);
        }

        int tier = voidOreMiner.getTier();

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

        if (te.getBlockType() == ModBlocks.QUANTUM_RES_EXTRACTOR.get()) {
            GL11.glColor3f(0.31f, 0.78f, 0.47f);
        } else {
            GL11.glColor3f(0.40f, 0.90f, 1.00f);
        }
        RenderUtil.bindTexture(PANEL);
        model.renderOnly("TopPanelW", "TopPanelN", "TopPanelEW", "PanelNorth", "PanelSouth", "PanelEast", "PanelWest");
        render(tier);
        GL11.glPopMatrix();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int tier = item.getItemDamage() + 1;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, -0.1f, 0.5f);

        if (item.getItem() == ModBlocks.QUANTUM_RES_EXTRACTOR.getItem()) {
            GL11.glColor3f(0.31f, 0.78f, 0.47f);
        } else {
            GL11.glColor3f(0.40f, 0.90f, 1.00f);
        }
        RenderUtil.bindTexture(PANEL);
        model.renderOnly("TopPanelW", "TopPanelN", "TopPanelEW", "PanelNorth", "PanelSouth", "PanelEast", "PanelWest");
        render(tier);
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    private void renderBeam(TEQuantumExtractor miner, double x, double y, double z, float partialTicks) {
        float f1 = miner.getBeamProgress();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        if (f1 > 0.0F) {
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

            double beamHeight = 256.0 * f1; // chiếu xuống
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
            double beamHeight2 = 256.0 * f1;
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

    public void render(int tier) {

        RenderUtil.bindTexture(BOTTOM);
        model.renderOnly("Bottom");
        RenderUtil.bindTexture(LASER);
        model.renderOnly("LaserHead");

        int color = DyeColor.WHITE.getColor();
        switch (tier) {
            case 1:
                color = DyeColor.YELLOW.getColor();
                break;
            case 2:
                color = DyeColor.LIGHT_BLUE.getColor();
                break;
            case 3:
                color = DyeColor.CYAN.getColor();
                break;
            case 4:
                color = DyeColor.WHITE.getColor();
                break;
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float brightnessFactor = 1.18f;
        r = Math.min(1.0f, r * brightnessFactor);
        g = Math.min(1.0f, g * brightnessFactor);
        b = Math.min(1.0f, b * brightnessFactor);

        GL11.glColor3f(r, g, b);
        RenderUtil.bindTexture(PANEL);
        model.renderOnly(
            "TopEast",
            "TopWest",
            "TopSouth",
            "TopNorth",
            "NorthEast",
            "SouthEast",
            "SouthWest",
            "NorthWest");
    }

}
