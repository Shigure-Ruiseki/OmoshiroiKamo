package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Point;
import java.util.List;

import net.minecraft.client.Minecraft;

public abstract class RecipeLayoutPart<T> {

    protected Point offset;

    public RecipeLayoutPart(int x, int y) {
        this.offset = new Point(x, y);
    }

    public void setPosition(int x, int y) {
        this.offset.setLocation(x, y);
    }

    public Point getPosition() {
        return this.offset;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getMaxHorizontalCount();

    // Higher number = sorted first (left/top)
    public abstract int getSortOrder();

    public abstract void draw(Minecraft mc);

    public abstract void handleTooltip(Point mousePos, List<String> currenttip);
}
