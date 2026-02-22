/*
 * Primal Mod
 * Copyright (c) PufferTeam
 * This file is part of Primal.
 * Primal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 2.1.
 * Primal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Primal. If not, see <https://www.gnu.org/licenses/>.
 */

package ruiseki.omoshiroikamo.core.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = true)
public abstract class ISBRHOK implements ISimpleBlockRenderingHandler {

    private static float lastBrightnessX = 0;
    private static float lastBrightnessY = 0;

    public void renderStandardInvBlockColor(RenderBlocks renderblocks, Block block, int meta, float scale) {
        int j;
        float f1;
        float f2;
        float f3;

        j = block.getRenderColor(meta);

        f1 = (float) (j >> 16 & 255) / 255.0F;
        f2 = (float) (j >> 8 & 255) / 255.0F;
        f3 = (float) (j & 255) / 255.0F;
        GL11.glColor4f(f1 * scale, f2 * scale, f3 * scale, 1.0F);
        renderStandardInvBlock(renderblocks, block, meta);
    }

    public void renderStandardInvBlockColorMaxBrightness(RenderBlocks renderblocks, Block block, int meta,
        float scale) {
        int j;
        float f1;
        float f2;
        float f3;

        j = block.getRenderColor(meta);

        f1 = (float) (j >> 16 & 255) / 255.0F;
        f2 = (float) (j >> 8 & 255) / 255.0F;
        f3 = (float) (j & 255) / 255.0F;
        GL11.glColor4f(f1 * scale, f2 * scale, f3 * scale, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        setLighting(floorMixedBrightness(15, 15));
        renderStandardInvBlock(renderblocks, block, meta);
        resetLighting();
    }

    public static void setLighting(int lighting) {
        storeLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lighting % 65536.0F, lighting / 65536.0F);
    }

    public static void storeLighting() {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
    }

    public static void resetLighting() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }

    private int floorMixedBrightness(int minBrightness, int brightness) {
        // Max value is 240 because 15 (max light value) * 16 = 240
        int sky = MathHelper.clamp_int(((brightness >>> 16) & 0xFF), minBrightness * 16, 240);
        int block = MathHelper.clamp_int(brightness & 0xFF, minBrightness * 16, 240);

        return ((sky & 0xFF) << 16) | (block & 0xFF);
    }

    public void renderStandardInvBlock(RenderBlocks renderblocks, Block block, int meta) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    public boolean renderStandardBlockNoColor(RenderBlocks renderer, Block blockType, int blockX, int blockY,
        int blockZ) {
        int l = 16777215;
        float f = (float) (l >> 16 & 255) / 255.0F;
        float f1 = (float) (l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return Minecraft.isAmbientOcclusionEnabled() && blockType.getLightValue() == 0
            ? (renderer.partialRenderBounds
                ? renderer.renderStandardBlockWithAmbientOcclusionPartial(blockType, blockX, blockY, blockZ, f, f1, f2)
                : renderer.renderStandardBlockWithAmbientOcclusion(blockType, blockX, blockY, blockZ, f, f1, f2))
            : renderer.renderStandardBlockWithColorMultiplier(blockType, blockX, blockY, blockZ, f, f1, f2);
    }

    public boolean renderStandardBlockMaxBrightness(RenderBlocks renderblocks, Block block, int x, int y, int z) {
        int l = block.colorMultiplier(renderblocks.blockAccess, x, y, z);
        float f = (float) (l >> 16 & 255) / 255.0F;
        float f1 = (float) (l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }
        return renderStandardBlockMaxBrightness(renderblocks, block, x, y, z, f, f1, f2);
    }

    public boolean renderStandardBlockMaxBrightness(RenderBlocks renderblocks, Block block, int x, int y, int z,
        float p_147736_5_, float p_147736_6_, float p_147736_7_) {
        renderblocks.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Blocks.grass) {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z);

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x, y - 1, z, 0)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMinY > 0.0D ? l
                        : block.getMixedBrightnessForBlock(
                            renderblocks.blockAccess,
                            x,
                            MathHelper.floor_double(y - 1),
                            z)));
            tessellator.setColorOpaque_F(f10, f13, f16);
            renderblocks.renderFaceYNeg(
                block,
                (double) x,
                (double) y,
                (double) z,
                renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 0));
            flag = true;
        }

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x, y + 1, z, 1)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMaxY < 1.0D ? l
                        : block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y + 1, z)));
            tessellator.setColorOpaque_F(f7, f8, f9);
            renderblocks.renderFaceYPos(
                block,
                (double) x,
                (double) y,
                (double) z,
                renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 1));
            flag = true;
        }

        IIcon iicon;

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x, y, z - 1, 2)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMinZ > 0.0D ? l
                        : block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z - 1)));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 2);
            renderblocks.renderFaceZNeg(block, (double) x, (double) y, (double) z, iicon);
            flag = true;
        }

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x, y, z + 1, 3)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMaxZ < 1.0D ? l
                        : block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z + 1)));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 3);
            renderblocks.renderFaceZPos(block, (double) x, (double) y, (double) z, iicon);
            flag = true;
        }

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x - 1, y, z, 4)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMinX > 0.0D ? l
                        : block.getMixedBrightnessForBlock(renderblocks.blockAccess, x - 1, y, z)));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 4);
            renderblocks.renderFaceXNeg(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        if (renderblocks.renderAllFaces || block.shouldSideBeRendered(renderblocks.blockAccess, x + 1, y, z, 5)) {
            tessellator.setBrightness(
                floorMixedBrightness(
                    15,
                    renderblocks.renderMaxX < 1.0D ? l
                        : block.getMixedBrightnessForBlock(renderblocks.blockAccess, x + 1, y, z)));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = renderblocks.getBlockIcon(block, renderblocks.blockAccess, x, y, z, 5);
            renderblocks.renderFaceXPos(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        return flag;
    }

    /*
     * --- CRASH FIX ----
     * Fix Vertex Crash (When using two different render pass);
     * This code is only used for that.
     * https://forums.minecraftforge.net/topic/22139-isbrh-alpha-blending-inconsistencies/
     */
    public void dumpVertices(Tessellator tess, int x, int y, int z) {
        for (int i = 0; i < 4; i++) {
            tess.addVertex(x, y, z);
        }
    }
}
