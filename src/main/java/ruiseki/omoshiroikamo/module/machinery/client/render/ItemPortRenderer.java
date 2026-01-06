package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.client.RenderUtils;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;

public class ItemPortRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        RenderBlocks renderer = (RenderBlocks) data[0];
        Tessellator t = Tessellator.instance;

        if (!(stack.getItem() instanceof ItemBlock)) return;

        Block block = Block.getBlockFromItem(stack.getItem());
        int meta = stack.getItemDamage();

        GL11.glPushMatrix();

        // ===== SCALE & ROTATE =====
        if (type == ItemRenderType.INVENTORY) {
            GL11.glScalef(1.0F, 1.0F, 1.0F);
        } else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        }

        // ===== BASE BLOCK =====
        renderer.renderBlockAsItem(block, meta, 1.0F);

        // ===== OVERLAY =====
        if (stack.getItem() instanceof AbstractPortItemBlock port) {
            var icon = port.getOverlayIcon(meta + 1);
            if (icon != null) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
                RenderUtils.renderOverlay(t, icon);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }

        GL11.glPopMatrix();
    }
}
