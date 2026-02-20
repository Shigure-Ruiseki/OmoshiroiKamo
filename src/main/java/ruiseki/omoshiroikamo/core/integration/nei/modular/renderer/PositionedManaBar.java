package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import vazkii.botania.client.core.handler.HUDHandler;

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

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        int x = position.x;
        int y = position.y; // renderManaBar draws at y

        // Note: renderManaBar might take 'ticks' or direct values.
        // Signature in 1.7.10 Botania API
        // (vazkii.botania.client.core.handler.HUDHandler):
        // public static void renderManaBar(int x, int y, int color, float alpha, int
        // mana, int maxMana)

        HUDHandler.renderManaBar(x, y, 0x0000FF, 1.0F, amount, amount);

        // Draw amount text
        String amountStr = amount + (perTick ? " Mana/t" : " Mana");
        float scale = 0.5f;
        int realX = position.x + position.width / 2;
        int realY = position.y - 5; // Draw above the bar

        GL11.glPushMatrix();
        GL11.glTranslatef(realX, realY, 0.0f);
        GL11.glScalef(scale, scale, 1.0f);
        GuiDraw.drawStringC(amountStr, 0, 0, 0x0000FF, false);
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
