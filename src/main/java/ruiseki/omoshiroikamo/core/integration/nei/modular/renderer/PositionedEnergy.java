package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;

public class PositionedEnergy implements INEIPositionedRenderer {

    private final int amount;
    private final boolean perTick;
    private final Rectangle position;
    private final ItemStack displayStack;

    public PositionedEnergy(int amount, boolean perTick, boolean isInput, Rectangle position) {
        this.amount = amount;
        this.perTick = perTick;
        this.position = position;
        this.displayStack = isInput ? MachineryBlocks.ENERGY_INPUT_PORT.newItemStack()
            : MachineryBlocks.ENERGY_OUTPUT_PORT.newItemStack();
    }

    @Override
    public void draw() {
        // Draw Energy Port Icon
        if (displayStack != null) {
            GuiContainerManager.drawItem(position.x + 1, position.y - 18, displayStack);
        }

        // Draw Energy Bar Background and Foreground
        Minecraft.getMinecraft().renderEngine
            .bindTexture(new ResourceLocation(LibMisc.MOD_ID, "textures/gui/progress_energy.png"));

        int barWidth = 12;
        int barHeight = 48;
        int x = position.x + (position.width - barWidth) / 2;
        int y = position.y + (position.height - barHeight) / 2;

        GuiDraw.drawTexturedModalRect(x, y, 0, 0, 16, barHeight);
        GuiDraw.drawGradientRect(x, y, barWidth, barHeight, 0xFF550000, 0xFF330000);

        int fillHeight = barHeight; // Full for recipe view

        GuiDraw.drawGradientRect(x, y + barHeight - fillHeight, barWidth, fillHeight, 0xFFFF0000, 0xFFAA0000);

        if (amount > 0) {
            String amountStr = amount + (perTick ? " RF/t" : " RF");
            // Draw amount string, maybe scaled down
            float scale = 0.5f;
            int realX = this.position.x + this.position.width / 2;
            int realY = this.position.y + this.position.height - 5;

            GL11.glPushMatrix();
            GL11.glTranslatef(realX, realY, 0.0f);
            GL11.glScalef(scale, scale, 1.0f);

            GuiDraw.drawStringC(amountStr, 0, 0, 0xFFFFFF, true); // White with shadow

            GL11.glPopMatrix();
        }
    }

    @Override
    public void handleTooltip(List<String> currenttip) {
        currenttip.add(EnumChatFormatting.RED + "Energy");
        currenttip.add(EnumChatFormatting.GRAY + (amount + (perTick ? " RF/t" : " RF")));
    }

    @Override
    public Rectangle getPosition() {
        return position;
    }
}
