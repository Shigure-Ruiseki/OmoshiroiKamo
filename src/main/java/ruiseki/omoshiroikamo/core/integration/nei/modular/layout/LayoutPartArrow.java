package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Point;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import codechicken.lib.gui.GuiDraw;

public class LayoutPartArrow extends RecipeLayoutPart<Object> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

    public LayoutPartArrow() {
        super(0, 0);
    }

    @Override
    public int getWidth() {
        return 24;
    }

    @Override
    public int getHeight() {
        return 17;
    }

    @Override
    public int getMaxHorizontalCount() {
        return 1;
    }

    @Override
    public int getSortOrder() {
        return 0; // Irrelevant, manually placed
    }

    @Override
    public void draw(Minecraft mc) {
        mc.renderEngine.bindTexture(TEXTURE);
        GuiDraw.drawTexturedModalRect(offset.x, offset.y, 79, 17, 24, 17);
    }

    @Override
    public void handleTooltip(Point mousePos, List<String> currenttip) {
        // No tooltip for arrow
    }
}
