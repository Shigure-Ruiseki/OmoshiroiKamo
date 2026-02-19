package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;

public class PositionedGasTank implements INEIPositionedRenderer {

    public GasStack gasStack;
    public Rectangle position;

    public PositionedGasTank(GasStack gasStack, Rectangle position) {
        this.gasStack = gasStack;
        this.position = position;
    }

    @Override
    public void draw() {
        if (gasStack == null || gasStack.getGas() == null) {
            return;
        }
        Gas gas = gasStack.getGas();
        IIcon icon = gas.getIcon();
        if (icon == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        Gui gui = new Gui();
        gui.drawTexturedModelRectFromIcon(position.x, position.y, icon, 16, 16);

        GL11.glDisable(GL11.GL_BLEND);

        // Amount
        if (gasStack.amount > 0) {
            String amountStr = gasStack.amount + "mB";
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
        if (gasStack == null || gasStack.getGas() == null) {
            return;
        }
        currenttip.add(
            gasStack.getGas()
                .getLocalizedName());
        currenttip.add(EnumChatFormatting.GRAY + Integer.toString(gasStack.amount) + " mB");
    }
}
