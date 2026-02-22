package ruiseki.omoshiroikamo.core.client.util.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ModelTESS {

    static double epsilon = 2e-5;

    public void renderBlock(RenderBlocks renderblocks, Tessellator tess, Block block, ModelRenderer renderer,
        float scale, int x, int y, int z, double offsetX, double offsetY, double offsetZ, int index) {

        if (renderblocks.hasOverrideBlockTexture()) {
            renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            renderblocks.renderStandardBlock(block, x, y, z);
            return;
        }

        if (!renderer.isHidden && renderer.showModel) {

            // Render children first
            if (renderer.childModels != null) {
                for (int i = 0; i < renderer.childModels.size(); ++i) {
                    ModelRenderer child = (ModelRenderer) renderer.childModels.get(i);

                    // Backup child state
                    float oldRotateX = child.rotateAngleX;
                    float oldRotateY = child.rotateAngleY;
                    float oldRotateZ = child.rotateAngleZ;
                    float oldPivotX = child.rotationPointX;
                    float oldPivotY = child.rotationPointY;
                    float oldPivotZ = child.rotationPointZ;

                    // Apply parent rotation/pivot to child
                    child.rotateAngleX += renderer.rotateAngleX;
                    child.rotateAngleY += renderer.rotateAngleY;
                    child.rotateAngleZ += renderer.rotateAngleZ;

                    child.rotationPointX += renderer.rotationPointX;
                    child.rotationPointY += renderer.rotationPointY;
                    child.rotationPointZ += renderer.rotationPointZ;

                    child.rotateAngleYGlobal = renderer.rotateAngleYGlobal;

                    // Recurse
                    renderBlock(renderblocks, tess, block, child, scale, x, y, z, offsetX, offsetY, offsetZ, index);

                    // Restore child state
                    child.rotateAngleX = oldRotateX;
                    child.rotateAngleY = oldRotateY;
                    child.rotateAngleZ = oldRotateZ;
                    child.rotationPointX = oldPivotX;
                    child.rotationPointY = oldPivotY;
                    child.rotationPointZ = oldPivotZ;
                }
            }

            double r = renderer.rotateAngleYGlobal;

            double cosR = Math.cos(r);
            double sinR = Math.sin(r);

            IIcon icon = block.getIcon(renderblocks.blockAccess, x, y, z, index);
            if (index < 16) {
                icon = block.getIcon(0, index);
            }

            tess.setBrightness(block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z));
            int i1 = block.colorMultiplier(renderblocks.blockAccess, x, y, z);
            float f = (float) (i1 >> 16 & 255) / 255.0F;
            float f1 = (float) (i1 >> 8 & 255) / 255.0F;
            float f2 = (float) (i1 & 255) / 255.0F;

            if (EntityRenderer.anaglyphEnable) {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            if (renderer.cubeList != null) {
                for (int i = 0; i < renderer.cubeList.size(); ++i) {
                    ModelBox box = renderer.cubeList.get(i);

                    double cosX = Math.cos(renderer.rotateAngleX), sinX = Math.sin(renderer.rotateAngleX);
                    double cosY = Math.cos(renderer.rotateAngleY), sinY = Math.sin(renderer.rotateAngleY);
                    double cosZ = Math.cos(renderer.rotateAngleZ), sinZ = Math.sin(renderer.rotateAngleZ);

                    for (int j = 0; j < box.quadList.length; ++j) {
                        TexturedQuad quad = box.quadList[j];
                        if (quad == null) continue;

                        // --- Base normal (unrotated) ---
                        Vec3 normal = quad.getNormal();

                        // --- Rotate the normal ---
                        double nx = normal.xCoord, ny = normal.yCoord, nz = normal.zCoord;

                        double ny1 = ny * cosX - nz * sinX;
                        double nz1 = ny * sinX + nz * cosX;

                        double nx2 = nx * cosY + nz1 * sinY;
                        double nz2 = -nx * sinY + nz1 * cosY;

                        double nx3 = nx2 * cosZ - ny1 * sinZ;
                        double ny3 = nx2 * sinZ + ny1 * cosZ;

                        double vx20 = nx3 * cosR - nz2 * sinR;
                        double vz20 = nx3 * sinR + nz2 * cosR;

                        nx3 = vx20;
                        nz2 = vz20;

                        double len = Math.sqrt(nx3 * nx3 + ny3 * ny3 + nz2 * nz2);
                        if (len > 0) {
                            nx3 /= len;
                            ny3 /= len;
                            nz2 /= len;
                        }

                        tess.setNormal((float) nx3, (float) ny3, (float) nz2);

                        float shade = 1.0F;
                        if (ny3 > 0.5F) shade = 1.0F; // top
                        else if (ny3 < -0.5F) shade = 0.5F; // bottom
                        else if (nx3 > 0.5F || nx3 < -0.5F) shade = 0.6F; // east/west
                        else if (nz2 > 0.5F || nz2 < -0.5F) shade = 0.8F; // north/south

                        tess.setColorOpaque_F(f * shade, f1 * shade, f2 * shade);

                        PositionTextureVertex pos1 = quad.vertexPositions[0];
                        double u1 = icon.getMinU() + pos1.texturePositionX * (icon.getMaxU() - icon.getMinU());
                        double v1 = icon.getMinV() + pos1.texturePositionY * (icon.getMaxV() - icon.getMinV());

                        PositionTextureVertex pos2 = quad.vertexPositions[1];
                        double u2 = icon.getMinU() + pos2.texturePositionX * (icon.getMaxU() - icon.getMinU());
                        double v2 = icon.getMinV() + pos2.texturePositionY * (icon.getMaxV() - icon.getMinV());

                        PositionTextureVertex pos3 = quad.vertexPositions[2];
                        double u3 = icon.getMinU() + pos3.texturePositionX * (icon.getMaxU() - icon.getMinU());
                        double v3 = icon.getMinV() + pos3.texturePositionY * (icon.getMaxV() - icon.getMinV());

                        PositionTextureVertex pos4 = quad.vertexPositions[3];
                        double u4 = icon.getMinU() + pos4.texturePositionX * (icon.getMaxU() - icon.getMinU());
                        double v4 = icon.getMinV() + pos4.texturePositionY * (icon.getMaxV() - icon.getMinV());

                        double[] U = { u1, u2, u3, u4 };
                        double[] V = { v1, v2, v3, v4 };

                        U = addEpsilonOffset(U);
                        V = addEpsilonOffset(V);

                        // --- Add rotated vertices ---
                        for (int p = 0; p < 4; ++p) {
                            PositionTextureVertex pos = quad.vertexPositions[p];
                            double u = U[p];
                            double v = V[p];

                            double px = pos.vector3D.xCoord * scale;
                            double py = pos.vector3D.yCoord * scale;
                            double pz = pos.vector3D.zCoord * scale;

                            // --- Translate into pivot space ---
                            px -= renderer.rotationPointX * scale;
                            py -= renderer.rotationPointY * scale;
                            pz -= renderer.rotationPointZ * scale;

                            // --- Rotate vertex same as normal ---
                            double y1 = py * cosX - pz * sinX;
                            double z1 = py * sinX + pz * cosX;

                            double x2 = px * cosY + z1 * sinY;
                            double z2v = -px * sinY + z1 * cosY;

                            double x3 = x2 * cosZ - y1 * sinZ;
                            double y3 = x2 * sinZ + y1 * cosZ;

                            // --- Translate back from pivot ---
                            x3 += renderer.rotationPointX * scale;
                            y3 += renderer.rotationPointY * scale;
                            z2v += renderer.rotationPointZ * scale;

                            // --- Apply world offset ---

                            double vx2 = x3 * cosR - z2v * sinR;
                            double vz2 = x3 * sinR + z2v * cosR;

                            x3 = vx2;
                            z2v = vz2;

                            double vx = x3 + x + 0.5 + offsetX;
                            double vy = y3 + y + offsetY;
                            double vz = z2v + z + 0.5 + offsetZ;

                            tess.addVertexWithUV(vx, vy, vz, u, v);
                        }
                    }
                }
            }
        }
    }

    public Matrix4f matrix = new Matrix4f();

    public void renderBlockJOML(RenderBlocks renderblocks, Tessellator tess, Block block, ModelRenderer renderer,
        float scale, int x, int y, int z, double offsetX, double offsetY, double offsetZ, int index) {

        if (renderblocks.hasOverrideBlockTexture()) {
            return;
        }
        IIcon icon = block.getIcon(renderblocks.blockAccess, x, y, z, index);
        if (index < 16) {
            icon = block.getIcon(0, index);
        }

        matrix.identity();
        tess.setBrightness(block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z));
        int i1 = block.colorMultiplier(renderblocks.blockAccess, x, y, z);
        renderer.renderJOML(scale, matrix, i1, x, y, z, offsetX, offsetY, offsetZ, icon);
    }

    public void renderCrossed(RenderBlocks renderblocks, Block block, int x, int y, int z, int index) {
        IIcon icon = block.getIcon(renderblocks.blockAccess, x, y, z, index);
        if (index < 16) {
            icon = block.getIcon(0, index);
        }
        renderblocks.drawCrossedSquares(icon, x, y, z, 1.0F);
    }

    public void renderFluid(RenderBlocks renderblocks, Tessellator tess, int x, int y, int z, FluidStack fs,
        double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean renderAllSides) {
        renderFluid(renderblocks, tess, x, y, z, fs, minX, minY, minZ, maxX, maxY, maxZ, renderAllSides, false);
    }

    public void renderFluid(RenderBlocks renderblocks, Tessellator tess, int x, int y, int z, FluidStack fs,
        double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean renderAllSides,
        boolean isFlowing) {
        if (fs == null) return;
        Fluid fluid = fs.getFluid();
        if (fluid == null) return;

        Block block = fs.getFluid()
            .getBlock();
        if (block == null) return;

        if (renderblocks.hasOverrideBlockTexture()) {
            renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            renderblocks.renderStandardBlock(block, x, y, z);
            return;
        }

        IIcon icon = fluid.getStillIcon();
        if (isFlowing) {
            icon = fluid.getFlowingIcon();
        }
        if (icon == null) icon = fluid.getIcon();
        if (icon == null) icon = block.getIcon(0, 0);
        if (icon == null) return;

        int color = fluid.getColor(fs);
        tess.setBrightness(block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z));

        float r = (color >> 16 & 255) / 255f;
        float g = (color >> 8 & 255) / 255f;
        float b = (color & 255) / 255f;
        tess.setColorOpaque_F(r, g, b);
        renderblocks.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        renderblocks.renderFaceYPos(block, x, y, z, icon);
        if (renderAllSides) {
            renderblocks.renderFaceYNeg(block, x, y, z, icon);
            renderblocks.renderFaceXPos(block, x, y, z, icon);
            renderblocks.renderFaceXNeg(block, x, y, z, icon);
            renderblocks.renderFaceZPos(block, x, y, z, icon);
            renderblocks.renderFaceZNeg(block, x, y, z, icon);
        }
    }

    public void renderItem(RenderBlocks renderblocks, Tessellator tess, Block block, int x, int y, int z,
        double offsetX, double offsetY, double offsetZ, int index, ModelRenderer model, float scale) {
        if (renderblocks.hasOverrideBlockTexture()) {
            return;
        }
        IIcon icon = block.getIcon(renderblocks.blockAccess, x, y, z, index);
        if (index < 16) {
            icon = block.getIcon(0, index);
        }
        Matrix4f matrix2 = model.getLocalMatrix(scale);

        tess.setBrightness(block.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z));
        renderItem(
            tess,
            icon.getMaxU(),
            icon.getMinV(),
            icon.getMinU(),
            icon.getMaxV(),
            icon.getIconWidth(),
            icon.getIconHeight(),
            0.0625F,
            x,
            y,
            z,
            matrix2,
            scale,
            offsetX,
            offsetY,
            offsetZ);
    }

    public Vector3f temp = new Vector3f();
    public Vector3f normalTemp = new Vector3f();

    public void addVertexRotated(Tessellator tess, double x, double y, double z, double u, double v, Matrix4f matrix,
        int x2, int y2, int z2, float scale, double offsetX, double offsetY, double offsetZ) {
        temp.set(x, y, z);
        matrix.transformPosition(temp);
        temp.mul(scale);
        tess.addVertexWithUV(temp.x + x2 + offsetX + 0.5, temp.y + offsetY + y2, temp.z + offsetZ + z2 + 0.5, u, v);
    }

    public void addNormalRotated(Tessellator tess, double x, double y, double z, Matrix4f matrix, float scale) {
        normalTemp.set(x, y, z);

        matrix.transformDirection(normalTemp);
        normalTemp.normalize();
        int color = 16777215;
        // Color Recoloration from Block
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        // Shading (Not automatic in ISBRH)
        float shade = 1.0F;
        if (normalTemp.y > 0.5F) shade = 1.0F; // top
        else if (normalTemp.y < -0.5F) shade = 0.5F; // bottom
        else if (normalTemp.x > 0.5F || normalTemp.x < -0.5F) shade = 0.6F; // east/west
        else if (normalTemp.z > 0.5F || normalTemp.z < -0.5F) shade = 0.8F; // north/south

        tess.setColorOpaque_F(f * shade, f1 * shade, f2 * shade);

        tess.setNormal((float) normalTemp.x, (float) normalTemp.y, (float) normalTemp.z);
    }

    // spotless:off
    public void renderItem(Tessellator tess, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_, int x, int y, int z, Matrix4f matrix, float scale, double offsetX, double offsetY, double offsetZ)
    {

        addNormalRotated(tess, 0.0F, 0.0F, 1.0F, matrix, scale);
        addVertexRotated(tess, 0.0D, 0.0D, 0.0D, p_78439_1_, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 1.0D, 0.0D, 0.0D, p_78439_3_, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 1.0D, 1.0D, 0.0D, p_78439_3_, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 0.0D, 1.0D, 0.0D, p_78439_1_, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);

        addNormalRotated(tess, 0.0F, 0.0F, -1.0F, matrix, scale);
        addVertexRotated(tess, 0.0D, 1.0D, -p_78439_7_, p_78439_1_, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 1.0D, 1.0D, -p_78439_7_, p_78439_3_, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 1.0D, 0.0D, -p_78439_7_, p_78439_3_, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        addVertexRotated(tess, 0.0D, 0.0D, -p_78439_7_, p_78439_1_, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);

        float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float)p_78439_5_;
        float f6 = 0.5F * (p_78439_4_ - p_78439_2_) / (float)p_78439_6_;

        addNormalRotated(tess, -1.0F, 0.0F, 0.0F, matrix, scale);
        for (int k = 0; k < p_78439_5_; ++k) {
            float f7 = (float)k / (float)p_78439_5_;
            float f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;

            addVertexRotated(tess, f7, 0.0D, -p_78439_7_, f8, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f7, 0.0D, 0.0D,  f8, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f7, 1.0D, 0.0D,  f8, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f7, 1.0D, -p_78439_7_, f8, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        }

        addNormalRotated(tess, 1.0F, 0.0F, 0.0F, matrix, scale);
        for (int k = 0; k < p_78439_5_; ++k) {
            float f7 = (float)k / (float)p_78439_5_;
            float f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            float f9 = f7 + 1.0F / (float)p_78439_5_;

            addVertexRotated(tess, f9, 1.0D, -p_78439_7_, f8, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f9, 1.0D, 0.0D,  f8, p_78439_2_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f9, 0.0D, 0.0D,  f8, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, f9, 0.0D, -p_78439_7_, f8, p_78439_4_, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        }

        addNormalRotated(tess, 0.0F, 1.0F, 0.0F, matrix, scale);
        for (int k = 0; k < p_78439_6_; ++k) {
            float f7 = (float)k / (float)p_78439_6_;
            float f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            float f9 = f7 + 1.0F / (float)p_78439_6_;

            addVertexRotated(tess, 0.0D, f9, 0.0D, p_78439_1_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 1.0D, f9, 0.0D, p_78439_3_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 1.0D, f9, -p_78439_7_, p_78439_3_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 0.0D, f9, -p_78439_7_, p_78439_1_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        }

        addNormalRotated(tess, 0.0F, -1.0F, 0.0F, matrix, scale);
        for (int k = 0; k < p_78439_6_; ++k) {
            float f7 = (float)k / (float)p_78439_6_;
            float f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;

            addVertexRotated(tess, 1.0D, f7, 0.0D, p_78439_3_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 0.0D, f7, 0.0D, p_78439_1_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 0.0D, f7, -p_78439_7_, p_78439_1_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
            addVertexRotated(tess, 1.0D, f7, -p_78439_7_, p_78439_3_, f8, matrix, x, y, z, scale, offsetX, offsetY, offsetZ);
        }
    }
    // spotless:on

    public double[] addEpsilonOffset(double[] coords) {
        double min = coords[0];
        double max = coords[0];
        for (int i = 1; i < coords.length; i++) {
            if (coords[i] < min) min = coords[i];
            if (coords[i] > max) max = coords[i];
        }

        for (int i = 0; i < coords.length; i++) {
            if (Math.abs(coords[i] - min) < 1e-9) {
                coords[i] = min + epsilon;
            } else if (Math.abs(coords[i] - max) < 1e-9) {
                coords[i] = max - epsilon;
            }
        }

        return coords;
    }
}
