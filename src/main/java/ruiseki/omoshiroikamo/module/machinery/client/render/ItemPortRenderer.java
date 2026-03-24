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

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;

import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
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

        // ===== SPECIAL HANDLING FOR MACHINE CONTROLLER =====
        if (block instanceof BlockMachineController) {
            renderMachineController((BlockMachineController) block, meta, type);
        } else {
            // ===== BASE BLOCK (PORTS) =====
            renderer.renderBlockAsItem(block, meta, 1.0F);

            // ===== OVERLAY (PORTS) =====
            if (stack.getItem() instanceof AbstractPortItemBlock) {
                AbstractPortItemBlock port = (AbstractPortItemBlock) stack.getItem();
                IIcon icon = port.getOverlayIcon(meta + 1);
                if (icon != null) {
                    renderOverlay(t, icon, true);
                }
            }
        }

        GL11.glPopMatrix();
    }

    /**
     * Renders the Machine Controller with tinted base and layered overlays.
     */
    private void renderMachineController(BlockMachineController controller, int meta, ItemRenderType type) {
        Tessellator tess = TessellatorManager.get();

        // Get tint color from config
        int tintColor = MachineryConfig.getDefaultTintColorInt();
        float r = ((tintColor >> 16) & 0xFF) / 255.0f;
        float g = ((tintColor >> 8) & 0xFF) / 255.0f;
        float b = (tintColor & 0xFF) / 255.0f;

        // Get base texture
        IIcon baseIcon = controller.getIcon(0, meta);

        // Bind texture atlas
        RenderUtils.bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // Disable lighting for proper tint and transparency
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Layer 1: Tinted base cube
        tess.startDrawingQuads();
        tess.setColorOpaque_F(r, g, b);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            tess.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
            RenderUtils.renderFaceCorrected(tess, dir, 0, 0, 0, baseIcon, 0.0f, Rotation.NORMAL, Flip.NONE);
        }
        tess.draw();

        // Layer 2: Base overlay with tint (if exists)
        IIcon baseOverlayIcon = controller.getBaseOverlayIcon();
        if (baseOverlayIcon != null) {
            tess.startDrawingQuads();
            tess.setColorOpaque_F(r, g, b);
            tess.setNormal(1.0F, 0.0F, 0.0F);
            RenderUtils.renderFaceCorrected(
                tess,
                ForgeDirection.EAST,
                0,
                0,
                0,
                baseOverlayIcon,
                0.001f,
                Rotation.NORMAL,
                Flip.NONE);
            tess.draw();
        }

        // Layer 3: State overlay (idle/active) without tint
        IIcon overlayIcon = controller.getOverlayIcon();
        if (overlayIcon != null) {
            tess.startDrawingQuads();
            tess.setColorOpaque_F(1.0f, 1.0f, 1.0f); // White (no tint)
            tess.setNormal(1.0F, 0.0F, 0.0F);
            RenderUtils.renderFaceCorrected(
                tess,
                ForgeDirection.EAST,
                0,
                0,
                0,
                overlayIcon,
                0.002f,
                Rotation.NORMAL,
                Flip.NONE);
            tess.draw();
        }

        // Restore OpenGL state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    private void renderOverlay(Tessellator t, IIcon icon, boolean allFaces) {
        GL11.glPushMatrix();
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

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
