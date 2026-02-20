package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import codechicken.lib.gui.GuiDraw;

public class PositionedText implements INEIPositionedRenderer {

    private final String text;
    private final int color;
    private final Rectangle position;
    private final boolean center;

    public PositionedText(String text, int color, Rectangle position) {
        this(text, color, position, true);
    }

    public PositionedText(String text, int color, Rectangle position, boolean center) {
        this.text = text;
        this.color = color;
        this.position = position;
        this.center = center;
    }

    @Override
    public void draw() {
        // Center the text in the rectangle
        int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        int x = center ? position.x + (position.width - textWidth) / 2 : position.x;
        int y = position.y + (position.height - 8) / 2;

        GuiDraw.drawString(text, x, y, color, false);
    }

    @Override
    public void handleTooltip(List<String> currenttip) {
        currenttip.add(EnumChatFormatting.GRAY + text);
    }

    @Override
    public Rectangle getPosition() {
        return position;
    }
}
