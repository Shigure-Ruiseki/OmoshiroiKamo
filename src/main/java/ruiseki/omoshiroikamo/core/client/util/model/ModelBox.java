package ruiseki.omoshiroikamo.core.client.util.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.joml.Matrix4f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelBox {

    /** The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube */
    public PositionTextureVertex[] vertexPositions;
    /** An array of 6 TexturedQuads, one for each face of a cube */
    public TexturedQuad[] quadList;
    /** X vertex coordinate of lower box corner */
    public float posX1;
    /** Y vertex coordinate of lower box corner */
    public float posY1;
    /** Z vertex coordinate of lower box corner */
    public float posZ1;
    /** X vertex coordinate of upper box corner */
    public float posX2;
    /** Y vertex coordinate of upper box corner */
    public float posY2;
    /** Z vertex coordinate of upper box corner */
    public float posZ2;
    public String field_78247_g;

    public boolean[] quadsEnabled;

    public ModelBox() {}

    public ModelBox(ModelRenderer body, int U, int V, float x, float y, float z, int xWidth, int yHeight, int zDepth,
        float scaleFactor) {
        this(body, U, V, x, y, z, xWidth, yHeight, zDepth, scaleFactor, null);
    }

    public ModelBox(ModelRenderer body, int U, int V, float x, float y, float z, int xWidth, int yHeight, int zDepth,
        float scaleFactor, ModelQuad config) {
        if (config != null) {
            this.quadsEnabled = config.quadsEnabled;
        } else {
            this.quadsEnabled = new boolean[] { true, true, true, true, true, true };
        }
        this.posX1 = x;
        this.posY1 = y;
        this.posZ1 = z;
        this.posX2 = x + (float) xWidth;
        this.posY2 = y + (float) yHeight;
        this.posZ2 = z + (float) zDepth;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f4 = x + (float) xWidth;
        float f5 = y + (float) yHeight;
        float f6 = z + (float) zDepth;
        x -= scaleFactor;
        y -= scaleFactor;
        z -= scaleFactor;
        f4 += scaleFactor;
        f5 += scaleFactor;
        f6 += scaleFactor;

        if (body.mirror) {
            float f7 = f4;
            f4 = x;
            x = f7;
        }

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f4, y, z, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f5, z, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, f5, z, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f4, y, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, f5, f6, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        if (this.quadsEnabled[0]) {
            this.quadList[0] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex, positiontexturevertex1,
                    positiontexturevertex5 },
                U + zDepth + xWidth,
                V + zDepth,
                U + zDepth + xWidth + zDepth,
                V + zDepth + yHeight,
                body.textureWidth,
                body.textureHeight);
        }
        if (this.quadsEnabled[1]) {
            this.quadList[1] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex7, positiontexturevertex3, positiontexturevertex6,
                    positiontexturevertex2 },
                U,
                V + zDepth,
                U + zDepth,
                V + zDepth + yHeight,
                body.textureWidth,
                body.textureHeight);
        }
        if (this.quadsEnabled[2]) {
            this.quadList[2] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex3, positiontexturevertex7,
                    positiontexturevertex },
                U + zDepth,
                V,
                U + zDepth + xWidth,
                V + zDepth,
                body.textureWidth,
                body.textureHeight);
        }
        if (this.quadsEnabled[3]) {
            this.quadList[3] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex2, positiontexturevertex6,
                    positiontexturevertex5 },
                U + zDepth + xWidth,
                V + zDepth,
                U + zDepth + xWidth + xWidth,
                V,
                body.textureWidth,
                body.textureHeight);
        }
        if (this.quadsEnabled[4]) {
            this.quadList[4] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex7, positiontexturevertex2,
                    positiontexturevertex1 },
                U + zDepth,
                V + zDepth,
                U + zDepth + xWidth,
                V + zDepth + yHeight,
                body.textureWidth,
                body.textureHeight);
        }
        if (this.quadsEnabled[5]) {
            this.quadList[5] = new TexturedQuad(
                new PositionTextureVertex[] { positiontexturevertex3, positiontexturevertex4, positiontexturevertex5,
                    positiontexturevertex6 },
                U + zDepth + xWidth + zDepth,
                V + zDepth,
                U + zDepth + xWidth + zDepth + xWidth,
                V + zDepth + yHeight,
                body.textureWidth,
                body.textureHeight);
        }

        if (body.mirror) {
            for (int j1 = 0; j1 < this.quadList.length; ++j1) {
                this.quadList[j1].flipFace();
            }
        }
    }

    public ModelBox(ModelRenderer body, int U, int V, float x, float y, float z, int xWidth, int yHeight, int zDepth,
        float scaleFactor, boolean mirror) {
        this(setMirror(body, mirror), U, V, x, y, z, xWidth, yHeight, zDepth, scaleFactor);
    }

    static ModelRenderer setMirror(ModelRenderer in, boolean mirrorToBe) {
        in.mirror = mirrorToBe;
        return in;
    }

    /**
     * Draw the six sided box defined by this ModelBox
     */
    @SideOnly(Side.CLIENT)
    public void render(Tessellator tessellator, float scale) {
        for (int i = 0; i < this.quadList.length; ++i) {
            if (this.quadList[i] == null) continue;
            this.quadList[i].draw(tessellator, scale);
        }
    }

    public void renderJOML(Tessellator tessellator, float scale, Matrix4f matrix, int color, int x, int y, int z,
        double offsetX, double offsetY, double offsetZ, IIcon icon) {
        for (int i = 0; i < this.quadList.length; ++i) {
            if (this.quadList[i] == null) continue;
            this.quadList[i].drawJOML(tessellator, scale, matrix, color, x, y, z, offsetX, offsetY, offsetZ, icon);
        }
    }

    public ModelBox func_78244_a(String boxName) {
        this.field_78247_g = boxName;
        return this;
    }
}
