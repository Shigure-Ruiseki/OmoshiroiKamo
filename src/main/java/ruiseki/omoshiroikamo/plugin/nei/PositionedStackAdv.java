/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package ruiseki.omoshiroikamo.plugin.nei;

import java.awt.Rectangle;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public class PositionedStackAdv extends PositionedStack {

    private final List<String> tooltip = new ArrayList<>();
    public float chance;
    public int textYOffset = 0;
    public int textColor = 0xFFFFFF;

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
        if (chance <= 0.0f || chance >= 1.0f) {
            return;
        }
        float scale = 0.8f;
        String text = String.format("%.1f%%", chance * 100f);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = font.getStringWidth(text);

        float inverse = 1f / scale;

        int x = this.relx + 1;
        int y = this.rely + 1;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        font.drawStringWithShadow(
            text,
            (int) ((x + 8 - stringWidth * scale / 2) * inverse),
            (int) ((y + 16 - font.FONT_HEIGHT * scale + textYOffset) * inverse),
            textColor);
        GL11.glPopMatrix();
    }

    public PositionedStackAdv setChance(float chance) {
        this.chance = Math.max(0.0f, Math.min(1.0f, chance));
        if (chance <= 0.0F) {
            this.tooltip.add(
                EnumChatFormatting.GRAY
                    + String.format(NEIUtils.translate("chance"), NEIUtils.translate("chance.never")));
        } else if (chance < 0.01F) {
            this.tooltip.add(
                EnumChatFormatting.GRAY
                    + String.format(NEIUtils.translate("chance"), NEIUtils.translate("chance.lessThan1")));
        } else if (chance != 1.0F) {
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(2);
            this.tooltip.add(
                EnumChatFormatting.GRAY
                    + String.format(NEIUtils.translate("chance"), String.valueOf(percentFormat.format(chance))));
        }
        return this;
    }
}
