package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;

public class PositionedManaBar implements INEIPositionedRenderer {

    public int amount;
    public boolean perTick;
    public Rectangle position;

    public PositionedManaBar(int amount, boolean perTick, Rectangle position) {
        this.amount = amount;
        this.perTick = perTick;
        this.position = position;
    }

    @Override
    public void draw() {
        if (amount <= 0) {
            return;
        }

        // Use Botania's mana HUD texture
        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(new ResourceLocation("botania", "textures/gui/manaHud.png"));

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        GuiDraw.drawGradientRect(position.x, position.y + 20, 100, 8, 0xFF0000AA, 0xFF0000FF); // Example blue bar

        int barHeight = 8;
        int barWidth = position.width; // Use full width of slot
        int x = position.x;
        int y = position.y + (position.height - barHeight) / 2;

        GuiDraw.drawGradientRect(x, y, barWidth, barHeight, 0xFF00FFFF, 0xFF0000AA); // Light blue to dark blue

        // Draw amount text
        String amountStr = amount + (perTick ? " Mana/t" : " Mana");
        float scale = 0.5f;
        int realX = position.x + position.width / 2;
        int realY = position.y + position.height - 5;

        GL11.glPushMatrix();
        GL11.glTranslatef(realX, realY, 0.0f);
        GL11.glScalef(scale, scale, 1.0f);
        GuiDraw.drawStringC(amountStr, 0, 0, 0x0000FF, true);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public Rectangle getPosition() {
        return position;
    }

    @Override
    public void handleTooltip(List<String> currenttip) {
        currenttip.add(EnumChatFormatting.BLUE + "Mana");
        currenttip.add(EnumChatFormatting.GRAY + Integer.toString(amount) + (perTick ? " Mana/t" : " Mana"));
    }
}
