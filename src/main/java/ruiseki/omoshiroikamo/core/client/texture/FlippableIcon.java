package ruiseki.omoshiroikamo.core.client.texture;

import net.minecraft.util.IIcon;

import org.jetbrains.annotations.NotNull;

import ruiseki.omoshiroikamo.core.helper.TextureHelpers;

public class FlippableIcon implements IIcon {

    private IIcon original;
    private boolean flip_u;
    private boolean flip_v;

    public FlippableIcon(IIcon o) {
        o = TextureHelpers.checkTexture(o);
        if (o == null) {
            throw new IllegalArgumentException("Cannot create a wrapper icon with a null icon.");
        }

        this.setOriginal(o);
        this.setFlipU(false);
        this.setFlipV(false);
    }

    @Override
    public int getIconWidth() {
        return this.getOriginal()
            .getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return this.getOriginal()
            .getIconHeight();
    }

    @Override
    public float getMinU() {
        if (this.isFlipU()) {
            return this.getOriginal()
                .getMaxU();
        }
        return this.getOriginal()
            .getMinU();
    }

    @Override
    public float getMaxU() {
        if (this.isFlipU()) {
            return this.getOriginal()
                .getMinU();
        }
        return this.getOriginal()
            .getMaxU();
    }

    @Override
    public float getInterpolatedU(final double px) {
        if (this.isFlipU()) {
            return this.getOriginal()
                .getInterpolatedU(16 - px);
        }
        return this.getOriginal()
            .getInterpolatedU(px);
    }

    @Override
    public float getMinV() {
        if (this.isFlipV()) {
            return this.getOriginal()
                .getMaxV();
        }
        return this.getOriginal()
            .getMinV();
    }

    @Override
    public float getMaxV() {
        if (this.isFlipV()) {
            return this.getOriginal()
                .getMinV();
        }
        return this.getOriginal()
            .getMaxV();
    }

    @Override
    public float getInterpolatedV(final double px) {
        if (this.isFlipV()) {
            return this.getOriginal()
                .getInterpolatedV(16 - px);
        }
        return this.getOriginal()
            .getInterpolatedV(px);
    }

    @Override
    public String getIconName() {
        return this.getOriginal()
            .getIconName();
    }

    public IIcon getOriginal() {
        return this.original;
    }

    public void setFlip(final boolean u, final boolean v) {
        this.setFlipU(u);
        this.setFlipV(v);
    }

    public int setFlip(final int orientation) {
        this.setFlipU((orientation & 8) == 8);
        this.setFlipV((orientation & 16) == 16);
        return orientation & 7;
    }

    boolean isFlipU() {
        return this.flip_u;
    }

    void setFlipU(final boolean flipU) {
        this.flip_u = flipU;
    }

    boolean isFlipV() {
        return this.flip_v;
    }

    void setFlipV(final boolean flipV) {
        this.flip_v = flipV;
    }

    public void setOriginal(@NotNull final IIcon original) {
        this.original = original;
    }
}
