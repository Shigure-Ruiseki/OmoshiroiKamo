package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import thaumcraft.api.aspects.Aspect;

public class PositionedEssentia implements INEIPositionedRenderer {

    public Aspect aspect;
    public int amount;
    public Rectangle position;

    public PositionedEssentia(Aspect aspect, int amount, Rectangle position) {
        this.aspect = aspect;
        this.amount = amount;
        this.position = position;
    }

    @Override
    public void draw() {
        if (aspect == null) {
            return;
        }

        ResourceLocation icon = aspect.getImage();
        if (icon == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(icon);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GuiDraw.drawTexturedModalRect(position.x, position.y, 0, 0, 16, 16);

        GL11.glDisable(GL11.GL_BLEND);

        // Amount
        if (amount > 0) {
            String amountStr = Integer.toString(amount);
            float scale = 0.5f;
            int realX = position.x + position.width / 2;
            int realY = position.y + position.height - 5;

            GL11.glPushMatrix();
            GL11.glTranslatef(realX, realY, 0.0f);
            GL11.glScalef(scale, scale, 1.0f);
            GuiDraw.drawStringC(amountStr, 0, 0, 0xFFFFFF, true);
            GL11.glPopMatrix();
        }
    }

    @Override
    public Rectangle getPosition() {
        return position;
    }

    @Override
    public void handleTooltip(List<String> currenttip) {
        if (aspect == null) {
            return;
        }
        currenttip.add(aspect.getName());
        currenttip.add(EnumChatFormatting.GRAY + aspect.getLocalizedDescription());
        if (amount > 0) {
            currenttip.add(EnumChatFormatting.GRAY + "Essentia: " + amount);
        }
    }
}
