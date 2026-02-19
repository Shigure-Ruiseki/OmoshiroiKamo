package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
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
        if (displayStack != null) {
            GuiContainerManager.drawItem(position.x + 1, position.y + 1, displayStack);
        }

        if (amount > 0) {
            String amountStr = amount + (perTick ? " RF/t" : " RF");
            // Draw amount string, maybe scaled down
            float scale = 0.5f;
            int realX = this.position.x + this.position.width / 2;
            int realY = this.position.y + this.position.height - 5;

            GL11.glPushMatrix();
            GL11.glTranslatef(realX, realY, 0.0f);
            GL11.glScalef(scale, scale, 1.0f);

            GuiDraw.drawStringC(amountStr, 0, 0, 0xFF0000, false); // Red color for energy

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
