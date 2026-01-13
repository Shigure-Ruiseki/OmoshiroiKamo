package ruiseki.omoshiroikamo.module.multiblock.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BeamSegment {

    public final int startY;
    public final int height;
    public final float[] color;

    public BeamSegment(int startY, int height, float[] color) {
        this.startY = startY;
        this.height = height;
        this.color = new float[] { color[0], color[1], color[2] };
    }

    public float getRed() {
        return color[0];
    }

    public float getGreen() {
        return color[1];
    }

    public float getBlue() {
        return color[2];
    }
}
