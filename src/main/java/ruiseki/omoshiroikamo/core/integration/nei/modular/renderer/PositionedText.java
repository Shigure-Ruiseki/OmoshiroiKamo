package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import codechicken.lib.gui.GuiDraw;

public class PositionedText implements INEIPositionedRenderer {

    private static final int LINE_HEIGHT = 10;

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
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int textWidth = fr.getStringWidth(text);

        if (textWidth > position.width) {
            // Wrap text into multiple lines
            List<String> lines = fr.listFormattedStringToWidth(text, position.width);
            int y = position.y;
            for (String line : lines) {
                int lineWidth = fr.getStringWidth(line);
                int x = center ? position.x + (position.width - lineWidth) / 2 : position.x;
                GuiDraw.drawString(line, x, y, color, false);
                y += LINE_HEIGHT;
            }
        } else {
            int y = position.y + (position.height - 8) / 2;
            int x = center ? position.x + (position.width - textWidth) / 2 : position.x;
            GuiDraw.drawString(text, x, y, color, false);
        }
    }

    /**
     * Returns the actual rendered height, accounting for word wrap.
     */
    public int getRenderedHeight() {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int textWidth = fr.getStringWidth(text);
        if (textWidth > position.width) {
            List<String> lines = fr.listFormattedStringToWidth(text, position.width);
            return lines.size() * LINE_HEIGHT;
        }
        return position.height;
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
