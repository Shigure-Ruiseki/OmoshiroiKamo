package ruiseki.omoshiroikamo.core.client.util.model;

import net.minecraft.util.Vec3;

public class PositionTextureVertex {

    public Vec3 vector3D;
    public float texturePositionX;
    public float texturePositionY;

    public PositionTextureVertex(float x, float y, float z, float u, float v) {
        this(Vec3.createVectorHelper((double) x, (double) y, (double) z), u, v);
    }

    public PositionTextureVertex setTexturePosition(float u, float v) {
        return new PositionTextureVertex(this, u, v);
    }

    public PositionTextureVertex(PositionTextureVertex original, float u, float v) {
        this.vector3D = original.vector3D;
        this.texturePositionX = u;
        this.texturePositionY = v;
    }

    public PositionTextureVertex(Vec3 position, float u, float v) {
        this.vector3D = position;
        this.texturePositionX = u;
        this.texturePositionY = v;
    }
}
