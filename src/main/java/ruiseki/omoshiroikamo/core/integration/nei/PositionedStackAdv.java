/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package ruiseki.omoshiroikamo.core.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public class PositionedStackAdv extends PositionedStack {

    private final List<String> tooltip = new ArrayList<>();
    public float chance;
    public int textYOffset = 0;
    public int textColor = 0xFFFFFF;
    public String label = null;
    public int labelColor = 0x000000;

    public PositionedStackAdv(Object object, int x, int y) {
        super(object, x, y);
    }

    public PositionedStackAdv setTextYOffset(int offset) {
        this.textYOffset = offset;
        return this;
    }

    public PositionedStackAdv setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public PositionedStackAdv(Object object, int x, int y, List<String> tooltip) {
        super(object, x, y);
        this.addToTooltip(tooltip);
    }

    public Rectangle getRect() {
        return new Rectangle(this.relx - 1, this.rely - 1, 18, 18);
    }

    public List<String> handleTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip) {
        if (!this.tooltip.isEmpty()) {
            for (String tip : this.tooltip) {
                currenttip.add(tip);
            }
        }
        return currenttip;
    }

    public PositionedStackAdv addToTooltip(List<String> lines) {
        for (String tip : lines) {
            this.tooltip.add(tip);
        }
        return this;
    }

    public PositionedStackAdv addToTooltip(String line) {
        this.tooltip.add(line);
        return this;
    }

    public void drawChance() {
        if (chance > 1.0f) {
            return;
        }
        float scale = 0.8f;
        double percent = chance * 100.0;

        String text;
        if (chance <= 0.0f) {
            // 0%: show as "-"
            text = "-";
        } else if (percent >= 10.0) {
            // 10% - 99%: show 1 decimal place (3 sig figs)
            text = String.format("%.1f%%", percent);
        } else if (percent >= 1.0) {
            // 1% - 10%: show 2 decimal places (3 sig figs)
            text = String.format("%.2f%%", percent);
        } else if (percent >= 0.1) {
            // 0.1% - 1%: show 3 decimal places (3 sig figs)
            text = String.format("%.3f%%", percent);
        } else {
            // Less than 0.1%: show 4 decimal places
            text = String.format("%.4f%%", percent);
        }

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = font.getStringWidth(text);

        float inverse = 1f / scale;

        int x = this.relx + 1;
        int y = this.rely + 1;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        font.drawString(
            text,
            (int) ((x + 8 - stringWidth * scale / 2) * inverse),
            (int) ((y + 16 - font.FONT_HEIGHT * scale + textYOffset) * inverse),
            textColor);
        GL11.glPopMatrix();
    }

    public void drawLabel() {
        if (label == null || label.isEmpty()) {
            return;
        }
        float scale = 0.8f;

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = font.getStringWidth(label);

        float inverse = 1f / scale;

        int x = this.relx + 1;
        int y = this.rely + 1;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        font.drawString(
            label,
            (int) ((x + 8 - stringWidth * scale / 2) * inverse),
            (int) ((y - 6) * inverse),
            labelColor);
        GL11.glPopMatrix();
    }

    public PositionedStackAdv setLabel(String label) {
        this.label = label;
        return this;
    }

    public PositionedStackAdv setLabelColor(int color) {
        this.labelColor = color;
        return this;
    }

    public PositionedStackAdv setChance(float chance) {
        this.chance = Math.max(0.0f, Math.min(1.0f, chance));
        return this;
    }
}
