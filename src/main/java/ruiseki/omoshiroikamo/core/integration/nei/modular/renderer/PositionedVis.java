package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public class PositionedVis implements INEIPositionedRenderer {

    public Aspect aspect;
    public int amountCentiVis;
    public Rectangle position;

    public PositionedVis(Aspect aspect, int amountCentiVis, Rectangle position) {
        this.aspect = aspect;
        this.amountCentiVis = amountCentiVis;
        this.position = position;
    }

    @Override
    public void draw() {
        if (aspect == null) {
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);
        try {
            UtilsFX.drawTag(position.x, position.y, aspect, amountCentiVis, 0, 0.0D);
        } catch (Throwable e) {
            ResourceLocation icon = aspect.getImage();
            if (icon == null) return;
            Minecraft.getMinecraft().renderEngine.bindTexture(icon);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiDraw.drawTexturedModalRect(position.x, position.y, 0, 0, 16, 16);
        }
        GL11.glDisable(GL11.GL_BLEND);

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
        if (amountCentiVis > 0) {
            currenttip.add(EnumChatFormatting.GRAY + "Vis: " + amountCentiVis + " cV");
        }
    }
}
