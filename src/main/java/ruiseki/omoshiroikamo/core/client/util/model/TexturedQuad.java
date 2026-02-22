package ruiseki.omoshiroikamo.core.client.util.model;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TexturedQuad {

    static double epsilon = 2e-5;

    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;
    public Vec3 normal;
    public IIcon lastIcon;

    public double[] cachedU;
    public double[] cachedV;

    public TexturedQuad(PositionTextureVertex[] p_i1152_1_) {
        this.vertexPositions = p_i1152_1_;
        this.nVertices = p_i1152_1_.length;
    }

    public TexturedQuad(PositionTextureVertex[] vertices, int u1, int v1, int u2, int v2, float textureWidth,
        float textureHeight) {
        this(vertices);
        float offsetU = 0.0F / textureWidth;
        float offsetV = 0.0F / textureHeight;
        vertices[0] = vertices[0].setTexturePosition(u2 / textureWidth - offsetU, v1 / textureHeight + offsetV);
        vertices[1] = vertices[1].setTexturePosition(u1 / textureWidth + offsetU, v1 / textureHeight + offsetV);
        vertices[2] = vertices[2].setTexturePosition(u1 / textureWidth + offsetU, v2 / textureHeight - offsetV);
        vertices[3] = vertices[3].setTexturePosition(u2 / textureWidth - offsetU, v2 / textureHeight - offsetV);
    }

    public void flipFace() {
        PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

        for (int i = 0; i < this.vertexPositions.length; ++i) {
            apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }

        this.vertexPositions = apositiontexturevertex;
    }

    public Vec3 getNormal() {
        if (this.normal == null) {
            Vec3 vec3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
            Vec3 vec31 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
            this.normal = vec31.crossProduct(vec3)
                .normalize();
        }
        return this.normal;
    }

    public void cacheUV(IIcon icon) {
        if (icon != lastIcon) {
            lastIcon = icon;
            PositionTextureVertex pos1 = this.vertexPositions[0];
            double u1 = icon.getMinU() + pos1.texturePositionX * (icon.getMaxU() - icon.getMinU());
            double v1 = icon.getMinV() + pos1.texturePositionY * (icon.getMaxV() - icon.getMinV());

            PositionTextureVertex pos2 = this.vertexPositions[1];
            double u2 = icon.getMinU() + pos2.texturePositionX * (icon.getMaxU() - icon.getMinU());
            double v2 = icon.getMinV() + pos2.texturePositionY * (icon.getMaxV() - icon.getMinV());

            PositionTextureVertex pos3 = this.vertexPositions[2];
            double u3 = icon.getMinU() + pos3.texturePositionX * (icon.getMaxU() - icon.getMinU());
            double v3 = icon.getMinV() + pos3.texturePositionY * (icon.getMaxV() - icon.getMinV());

            PositionTextureVertex pos4 = this.vertexPositions[3];
            double u4 = icon.getMinU() + pos4.texturePositionX * (icon.getMaxU() - icon.getMinU());
            double v4 = icon.getMinV() + pos4.texturePositionY * (icon.getMaxV() - icon.getMinV());

            double[] U = { u1, u2, u3, u4 };
            double[] V = { v1, v2, v3, v4 };

            cachedU = addEpsilonOffset(U);
            cachedV = addEpsilonOffset(V);
        }
    }

    public void draw(Tessellator tess, float scale) {
        Vec3 vec32 = this.getNormal();
        tess.startDrawingQuads();

        if (this.invertNormal) {
            tess.setNormal(-((float) vec32.xCoord), -((float) vec32.yCoord), -((float) vec32.zCoord));
        } else {
            tess.setNormal((float) vec32.xCoord, (float) vec32.yCoord, (float) vec32.zCoord);
        }

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            tess.addVertexWithUV(
                (double) ((float) positiontexturevertex.vector3D.xCoord * scale),
                (double) ((float) positiontexturevertex.vector3D.yCoord * scale),
                (double) ((float) positiontexturevertex.vector3D.zCoord * scale),
                (double) positiontexturevertex.texturePositionX,
                (double) positiontexturevertex.texturePositionY);
        }

        tess.draw();
    }

    public Vector3f temp = new Vector3f();
    public Vector3f normalTemp = new Vector3f();

    public void drawJOML(Tessellator tess, float scale, Matrix4f matrix, int color, int x, int y, int z, double offsetX,
        double offsetY, double offsetZ, IIcon icon) {
        normalTemp.set((float) getNormal().xCoord, (float) getNormal().yCoord, (float) getNormal().zCoord);

        matrix.transformDirection(normalTemp);
        normalTemp.normalize();

        if (this.invertNormal) {
            tess.setNormal(-((float) normalTemp.x), -((float) normalTemp.y), -((float) normalTemp.z));
        } else {
            tess.setNormal((float) normalTemp.x, (float) normalTemp.y, (float) normalTemp.z);
        }

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

        cacheUV(icon);

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            temp.set(
                (float) positiontexturevertex.vector3D.xCoord,
                (float) positiontexturevertex.vector3D.yCoord,
                (float) positiontexturevertex.vector3D.zCoord);

            matrix.transformPosition(temp);
            temp.mul(scale);

            tess.addVertexWithUV(
                temp.x + x + 0.5 + offsetX,
                temp.y + y + offsetY,
                temp.z + z + 0.5 + offsetZ,
                cachedU[i],
                cachedV[i]);
        }
    }

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
