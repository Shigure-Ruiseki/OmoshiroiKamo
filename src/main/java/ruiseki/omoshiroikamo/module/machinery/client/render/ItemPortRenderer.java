package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;

import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
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
        if (stack.getItem() instanceof AbstractPortItemBlock) {
            AbstractPortItemBlock port = (AbstractPortItemBlock) stack.getItem();
            IIcon icon = port.getOverlayIcon(meta + 1);
            if (icon != null) {
                renderOverlay(t, icon, true);
            }
        } else if (block instanceof BlockMachineController) {
            BlockMachineController controller = (BlockMachineController) block;
            IIcon icon = controller.getOverlayIcon();
            if (icon != null) {
                renderOverlay(t, icon, false);
            }
        }

        GL11.glPopMatrix();
    }

    private void renderOverlay(Tessellator t, IIcon icon, boolean allFaces) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        t.startDrawingQuads();
        t.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        if (allFaces) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
                RenderUtils.renderFaceCorrected(t, dir, 0, 0, 0, icon, 0.001f, Rotation.NORMAL, Flip.NONE);
            }
        } else {
            t.setNormal(1.0F, 0.0F, 0.0F);
            RenderUtils.renderFaceCorrected(t, ForgeDirection.EAST, 0, 0, 0, icon, 0.001f, Rotation.NORMAL, Flip.NONE);
        }
        t.draw();
    }
}
