package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;

import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;

public class LayoutPartRenderer extends RecipeLayoutPart<INEIPositionedRenderer> {

    private final INEIPositionedRenderer renderer;

    public LayoutPartRenderer(INEIPositionedRenderer renderer) {
        super(0, 0);
        this.renderer = renderer;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        Rectangle r = renderer.getPosition();
        if (r != null) {
            r.setLocation(x, y);
        }
    }

    @Override
    public int getWidth() {
        return renderer.getPosition().width;
    }

    @Override
    public int getHeight() {
        return renderer.getPosition().height;
    }

    @Override
    public int getMaxHorizontalCount() {
        return 1; // Default fallback
    }

    @Override
    public int getSortOrder() {
        return 0; // Default fallback
    }

    @Override
    public void draw(Minecraft mc) {
        renderer.draw();
    }

    @Override
    public void handleTooltip(Point mousePos, List<String> currenttip) {
        if (renderer.getPosition()
            .contains(mousePos)) {
            renderer.handleTooltip(currenttip);
        }
    }

    public INEIPositionedRenderer getRenderer() {
        return renderer;
    }
}
